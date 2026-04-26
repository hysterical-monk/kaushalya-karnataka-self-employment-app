package com.kaushalya.karnataka.presentation.worker.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaushalya.karnataka.domain.model.PortfolioPhoto
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PortfolioState(
    val photos: List<PortfolioPhoto> = emptyList(),
    val uploading: Boolean = false,
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class WorkerPortfolioViewModel @Inject constructor(
    private val workerRepository: WorkerRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val uid: String = auth.currentUser?.uid.orEmpty()
    private val uploading = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    val state: StateFlow<PortfolioState> = (
        if (uid.isBlank()) flowOf(PortfolioState(loading = false))
        else workerRepository.observePortfolio(uid)
            .map { PortfolioState(photos = it, uploading = uploading.value, loading = false, error = error.value) }
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PortfolioState())

    fun upload(uri: String, caption: String) {
        if (uid.isBlank()) return
        uploading.value = true
        error.value = null
        viewModelScope.launch {
            workerRepository.addPortfolioPhoto(uid, uri, caption)
                .onFailure { t -> error.value = t.message }
            uploading.value = false
        }
    }

    fun delete(photoId: String) {
        viewModelScope.launch { workerRepository.deletePortfolioPhoto(uid, photoId) }
    }
}
