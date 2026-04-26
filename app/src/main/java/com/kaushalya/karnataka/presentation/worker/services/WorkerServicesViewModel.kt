package com.kaushalya.karnataka.presentation.worker.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaushalya.karnataka.domain.model.PriceType
import com.kaushalya.karnataka.domain.model.ServiceCard
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkerServicesState(
    val services: List<ServiceCard> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class WorkerServicesViewModel @Inject constructor(
    private val workerRepository: WorkerRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val uid: String = auth.currentUser?.uid.orEmpty()

    val state: StateFlow<WorkerServicesState> = (
        if (uid.isBlank()) flowOf(WorkerServicesState(loading = false))
        else workerRepository.observeServices(uid)
            .map { WorkerServicesState(services = it, loading = false) }
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkerServicesState())

    fun upsert(id: String, title: String, description: String, priceType: PriceType, priceInr: Int, active: Boolean) {
        viewModelScope.launch {
            workerRepository.upsertService(
                ServiceCard(
                    id = id,
                    workerId = uid,
                    title = title,
                    description = description,
                    priceType = priceType,
                    priceInr = priceInr,
                    active = active
                )
            )
        }
    }

    fun delete(serviceId: String) {
        viewModelScope.launch { workerRepository.deleteService(uid, serviceId) }
    }
}
