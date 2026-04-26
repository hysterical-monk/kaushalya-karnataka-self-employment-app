package com.kaushalya.karnataka.presentation.customer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaushalya.karnataka.data.FirestorePaths
import com.kaushalya.karnataka.domain.model.Worker
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class HomeState(
    val greetingName: String = "",
    val featured: List<Worker> = emptyList(),
    val topRated: List<Worker> = emptyList(),
    val recentlyJoined: List<Worker> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    workerRepository: WorkerRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val displayName = MutableStateFlow("")

    init {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val name = firestore.collection(FirestorePaths.USERS).document(uid).get().await()
                .getString("displayName").orEmpty()
            displayName.value = name
        }
    }

    private val workersFlow = workerRepository.observeWorkers(category = null)

    val state: StateFlow<HomeState> = combine(workersFlow, displayName) { all, name ->
        val sortedByRating = all.sortedByDescending { it.averageRating }
        val sortedByRecency = all // placeholder ordering — Firestore createdAt would be ideal; stays in seed order for now
        HomeState(
            greetingName = name,
            featured = sortedByRating.take(3),
            topRated = sortedByRating.take(8),
            recentlyJoined = sortedByRecency.takeLast(6).reversed(),
            loading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeState())
}
