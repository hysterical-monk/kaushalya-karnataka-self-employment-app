package com.kaushalya.karnataka.presentation.worker.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaushalya.karnataka.domain.model.JobPost
import com.kaushalya.karnataka.domain.repository.JobPostRepository
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class WorkerJobsState(
    val jobs: List<JobPost> = emptyList(),
    val loading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WorkerJobsViewModel @Inject constructor(
    workerRepository: WorkerRepository,
    jobRepository: JobPostRepository,
    auth: FirebaseAuth
) : ViewModel() {

    private val uid = auth.currentUser?.uid

    val state: StateFlow<WorkerJobsState> = if (uid == null) {
        flowOf(WorkerJobsState(loading = false))
    } else {
        workerRepository.observeWorker(uid).flatMapLatest { w ->
            val town = w?.town?.takeIf { it.isNotBlank() }
            val cats = w?.categories ?: emptyList()
            jobRepository.observeOpenJobs(town = town, categoryIds = cats)
                .map { WorkerJobsState(jobs = it, loading = false) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkerJobsState())
}
