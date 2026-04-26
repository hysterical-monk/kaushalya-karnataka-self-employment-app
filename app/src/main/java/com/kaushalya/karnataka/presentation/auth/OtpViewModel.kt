package com.kaushalya.karnataka.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.domain.model.UserRole
import com.kaushalya.karnataka.domain.repository.AuthRepository
import com.kaushalya.karnataka.domain.repository.VerificationHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OtpState(
    val code: String = "",
    val verifying: Boolean = false,
    val error: String? = null
)

sealed interface OtpEvent {
    data object NeedsProfile : OtpEvent
    data object SignedInCustomer : OtpEvent
    data object SignedInWorker : OtpEvent
}

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state: StateFlow<OtpState> = _state.asStateFlow()

    fun onCodeChange(value: String) {
        _state.value = _state.value.copy(code = value.filter { it.isDigit() }.take(6), error = null)
    }

    fun verify(verificationId: String, phone: String, onEvent: (OtpEvent) -> Unit) {
        val code = _state.value.code
        if (code.length != 6) {
            _state.value = _state.value.copy(error = "Enter the 6-digit code")
            return
        }
        _state.value = _state.value.copy(verifying = true)
        viewModelScope.launch {
            val handle = VerificationHandle(verificationId = verificationId, phone = phone)
            authRepository.verifyOtp(handle, code)
                .onSuccess { user ->
                    _state.value = _state.value.copy(verifying = false)
                    val event = when (user.role) {
                        UserRole.WORKER -> OtpEvent.SignedInWorker
                        UserRole.CUSTOMER -> OtpEvent.SignedInCustomer
                        UserRole.UNKNOWN -> OtpEvent.NeedsProfile
                    }
                    onEvent(event)
                }
                .onFailure { t ->
                    _state.value = _state.value.copy(verifying = false, error = t.message ?: "Invalid code")
                }
        }
    }
}
