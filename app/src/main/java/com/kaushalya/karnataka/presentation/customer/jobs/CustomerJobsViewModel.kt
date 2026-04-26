package com.kaushalya.karnataka.presentation.customer.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaushalya.karnataka.data.FirestorePaths
import com.kaushalya.karnataka.domain.model.Category
import com.kaushalya.karnataka.domain.model.JobPost
import com.kaushalya.karnataka.domain.model.JobStatus
import com.kaushalya.karnataka.domain.repository.JobPostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class CustomerJobsState(
    val myJobs: List<JobPost> = emptyList(),
    val loading: Boolean = true,
    val toast: String? = null
)

@HiltViewModel
class CustomerJobsViewModel @Inject constructor(
    private val jobRepository: JobPostRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val uid = auth.currentUser?.uid

    val state: StateFlow<CustomerJobsState> = if (uid == null) {
        flowOf(CustomerJobsState(loading = false))
    } else {
        jobRepository.observeMyJobs(uid).map { CustomerJobsState(myJobs = it, loading = false) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CustomerJobsState())

    fun postJob(title: String, description: String, category: Category, town: String, budget: Int?, onPosted: () -> Unit) {
        val u = uid ?: return
        viewModelScope.launch {
            val name = firestore.collection(FirestorePaths.USERS).document(u).get().await()
                .getString("displayName").orEmpty()
            jobRepository.postJob(
                JobPost(
                    id = "",
                    customerId = u,
                    customerName = name.ifBlank { "Customer" },
                    title = title,
                    description = description,
                    categoryId = category.id,
                    town = town,
                    budgetMaxInr = budget,
                    status = JobStatus.OPEN,
                    createdAtMillis = System.currentTimeMillis()
                )
            ).onSuccess { onPosted() }
        }
    }

    fun closeJob(id: String) {
        viewModelScope.launch { jobRepository.closeJob(id) }
    }
}
