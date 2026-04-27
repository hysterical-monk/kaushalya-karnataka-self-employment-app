package com.kaushalya.karnataka.presentation.impact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.kaushalya.karnataka.data.FirestorePaths
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ImpactStatsState(
    val workersCount: Long = 0,
    val totalReviews: Long = 0,
    val averageRating: Double = 0.0,
    val jobsPosted: Long = 0,
    val townsCovered: Int = 0,
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ImpactStatsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow(ImpactStatsState())
    val state: StateFlow<ImpactStatsState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            runCatching {
                val workersSnap = firestore.collection(FirestorePaths.WORKERS).get().await()
                val workersCount = workersSnap.size().toLong()

                var totalReviews = 0L
                var ratingSum = 0.0
                var ratingCount = 0
                val towns = mutableSetOf<String>()
                workersSnap.documents.forEach { d ->
                    totalReviews += d.getLong("ratingCount") ?: 0L
                    val avg = d.getDouble("averageRating") ?: 0.0
                    val rc = d.getLong("ratingCount") ?: 0L
                    if (rc > 0L) {
                        ratingSum += avg
                        ratingCount += 1
                    }
                    d.getString("town")?.takeIf { it.isNotBlank() }?.let { towns += it }
                }
                val avgRating = if (ratingCount == 0) 0.0 else ratingSum / ratingCount

                val jobsCount = runCatching {
                    firestore.collection("job_posts").count().get(AggregateSource.SERVER).await().count
                }.getOrDefault(0L)

                _state.value = ImpactStatsState(
                    workersCount = workersCount,
                    totalReviews = totalReviews,
                    averageRating = avgRating,
                    jobsPosted = jobsCount,
                    townsCovered = towns.size,
                    loading = false
                )
            }.onFailure { t ->
                _state.value = _state.value.copy(loading = false, error = t.message)
            }
        }
    }
}
