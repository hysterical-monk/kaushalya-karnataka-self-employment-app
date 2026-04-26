package com.kaushalya.karnataka.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhoneEntryState(
    val phone: String = "",
    val sending: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PhoneEntryViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PhoneEntryState())
    val state: StateFlow<PhoneEntryState> = _state.asStateFlow()

    fun onPhoneChange(value: String) {
        _state.value = _state.value.copy(phone = value.filter { it.isDigit() }.take(10), error = null)
    }

    fun sendCode(onSent: (verificationId: String, phone: String) -> Unit) {
        val phone = _state.value.phone
        if (phone.length != 10) {
            _state.value = _state.value.copy(error = "Enter a valid 10-digit phone number")
            return
        }
        val e164 = "+91$phone"
        _state.value = _state.value.copy(sending = true, error = null)
        viewModelScope.launch {
            runCatching { authRepository.startPhoneVerification(e164) }
                .onSuccess { handle ->
                    _state.value = _state.value.copy(sending = false)
                    onSent(handle.verificationId, e164)
                }
                .onFailure { t ->
                    _state.value = _state.value.copy(sending = false, error = t.message ?: "Could not send code")
                }
        }
    }
}
