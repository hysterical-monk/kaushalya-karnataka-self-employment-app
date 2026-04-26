package com.kaushalya.karnataka.presentation.worker.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaushalya.karnataka.core.analytics.AnalyticsEvent
import com.kaushalya.karnataka.core.analytics.AnalyticsTracker
import com.kaushalya.karnataka.domain.model.HireRequest
import com.kaushalya.karnataka.domain.model.HireStatus
import com.kaushalya.karnataka.domain.model.Worker
import com.kaushalya.karnataka.domain.repository.HireRepository
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkerDashboardState(
    val worker: Worker? = null,
    val incoming: List<HireRequest> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class WorkerDashboardViewModel @Inject constructor(
    private val workerRepository: WorkerRepository,
    private val hireRepository: HireRepository,
    private val auth: FirebaseAuth,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val uid = auth.currentUser?.uid

    val state: StateFlow<WorkerDashboardState> = if (uid == null) {
        flowOf(WorkerDashboardState(loading = false))
    } else {
        combine(
            workerRepository.observeWorker(uid),
            hireRepository.observeIncoming(uid)
        ) { w, incoming -> WorkerDashboardState(worker = w, incoming = incoming, loading = false) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkerDashboardState())

    fun toggleAvailability() {
        val u = uid ?: return
        val current = state.value.worker?.isAvailable ?: return
        viewModelScope.launch {
            workerRepository.setAvailability(u, !current)
            analytics.log(AnalyticsEvent.AvailabilityToggled(available = !current))
        }
    }

    fun markStatus(requestId: String, status: HireStatus) {
        viewModelScope.launch { hireRepository.updateStatus(requestId, status) }
    }
}
