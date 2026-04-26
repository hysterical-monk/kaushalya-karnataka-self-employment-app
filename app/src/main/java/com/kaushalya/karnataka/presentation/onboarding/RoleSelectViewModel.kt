package com.kaushalya.karnataka.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.domain.model.UserRole
import com.kaushalya.karnataka.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoleSelectState(
    val displayName: String = "",
    val saving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RoleSelectViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RoleSelectState())
    val state: StateFlow<RoleSelectState> = _state.asStateFlow()

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(displayName = value, error = null)
    }

    fun choose(role: UserRole, onWorker: () -> Unit, onCustomer: () -> Unit) {
        if (_state.value.displayName.trim().length < 2) {
            _state.value = _state.value.copy(error = "Enter your name")
            return
        }
        _state.value = _state.value.copy(saving = true)
        viewModelScope.launch {
            authRepository.completeProfile(_state.value.displayName.trim(), role)
                .onSuccess {
                    _state.value = _state.value.copy(saving = false)
                    if (role == UserRole.WORKER) onWorker() else onCustomer()
                }
                .onFailure { t ->
                    _state.value = _state.value.copy(saving = false, error = t.message ?: "Could not save")
                }
        }
    }
}
