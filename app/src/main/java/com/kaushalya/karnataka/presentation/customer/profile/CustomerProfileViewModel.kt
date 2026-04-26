package com.kaushalya.karnataka.presentation.customer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaushalya.karnataka.core.locale.AppLocaleManager
import com.kaushalya.karnataka.data.FirestorePaths
import com.kaushalya.karnataka.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class CustomerProfileState(
    val displayName: String = "",
    val phone: String = "",
    val language: String = "en",
    val toast: String? = null
)

@HiltViewModel
class CustomerProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val locale: AppLocaleManager
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerProfileState(language = locale.current()))
    val state: StateFlow<CustomerProfileState> = _state.asStateFlow()

    init { refresh() }

    private fun refresh() {
        viewModelScope.launch {
            val user = auth.currentUser ?: return@launch
            val doc = firestore.collection(FirestorePaths.USERS).document(user.uid).get().await()
            _state.value = _state.value.copy(
                displayName = doc.getString("displayName").orEmpty(),
                phone = user.phoneNumber.orEmpty()
            )
        }
    }

    fun updateName(newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            authRepository.updateDisplayName(newName)
                .onSuccess { _state.value = _state.value.copy(displayName = newName, toast = "Name updated") }
                .onFailure { _state.value = _state.value.copy(toast = "Could not update: ${it.message}") }
        }
    }

    fun deleteAccount(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.deleteAccount()
                .onSuccess { onDone() }
                .onFailure {
                    _state.value = _state.value.copy(
                        toast = "Couldn't delete: ${it.message}. Sign out and back in, then try again."
                    )
                }
        }
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onDone()
        }
    }

    fun consumeToast() { _state.value = _state.value.copy(toast = null) }
}
