package com.kaushalya.karnataka.presentation.customer.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaushalya.karnataka.domain.model.HireRequest
import com.kaushalya.karnataka.domain.model.HireStatus
import com.kaushalya.karnataka.domain.repository.HireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerRequestsState(
    val requests: List<HireRequest> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class CustomerRequestsViewModel @Inject constructor(
    private val hireRepository: HireRepository,
    auth: FirebaseAuth
) : ViewModel() {

    val currentUid: String = auth.currentUser?.uid.orEmpty()

    val state: StateFlow<CustomerRequestsState> = if (currentUid.isBlank()) {
        flowOf(CustomerRequestsState(loading = false))
    } else {
        hireRepository.observeOutgoing(currentUid).map {
            CustomerRequestsState(requests = it, loading = false)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CustomerRequestsState())

    fun cancel(requestId: String) {
        viewModelScope.launch { hireRepository.updateStatus(requestId, HireStatus.CANCELLED) }
    }
}
