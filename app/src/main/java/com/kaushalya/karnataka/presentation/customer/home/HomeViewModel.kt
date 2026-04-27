package com.kaushalya.karnataka.presentation.customer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaushalya.karnataka.core.prefs.LocationStore
import com.kaushalya.karnataka.core.prefs.RecentlyViewedStore
import com.kaushalya.karnataka.data.FirestorePaths
import com.kaushalya.karnataka.domain.model.Worker
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class HomeState(
    val greetingName: String = "",
    val town: String? = null,
    val featured: List<Worker> = emptyList(),
    val topRated: List<Worker> = emptyList(),
    val recentlyJoined: List<Worker> = emptyList(),
    val recentlyViewed: List<Worker> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    workerRepository: WorkerRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val locationStore: LocationStore,
    private val recentlyViewedStore: RecentlyViewedStore
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
    private val townFlow = locationStore.observeTown()
    private val recentIdsFlow = recentlyViewedStore.observe()

    val state: StateFlow<HomeState> = combine(workersFlow, displayName, townFlow, recentIdsFlow) { all, name, town, recentIds ->
        val scoped = if (town == null) all else all.filter { it.town.equals(town, ignoreCase = true) }
        val sortedByRating = scoped.sortedByDescending { it.averageRating }
        val recentlyViewed = recentIds
            .mapNotNull { id -> all.firstOrNull { it.id == id } }
            .take(6)
        HomeState(
            greetingName = name,
            town = town,
            featured = sortedByRating.take(3),
            topRated = sortedByRating.take(8),
            recentlyJoined = scoped.takeLast(6).reversed(),
            recentlyViewed = recentlyViewed,
            loading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeState())

    fun setTown(town: String?) { locationStore.setTown(town) }
}
