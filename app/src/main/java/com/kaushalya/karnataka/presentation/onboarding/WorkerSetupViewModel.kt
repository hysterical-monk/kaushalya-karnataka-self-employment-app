package com.kaushalya.karnataka.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaushalya.karnataka.domain.model.Category
import com.kaushalya.karnataka.domain.model.PriceType
import com.kaushalya.karnataka.domain.model.ServiceCard
import com.kaushalya.karnataka.domain.model.Worker
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkerSetupState(
    val town: String = "",
    val locality: String = "",
    val bio: String = "",
    val categories: Set<Category> = emptySet(),
    val firstServiceTitle: String = "",
    val firstServicePrice: String = "",
    val firstServiceType: PriceType = PriceType.FIXED,
    val saving: Boolean = false,
    val done: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WorkerSetupViewModel @Inject constructor(
    private val workerRepository: WorkerRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val uid: String = auth.currentUser?.uid.orEmpty()

    private val _state = MutableStateFlow(WorkerSetupState())
    val state: StateFlow<WorkerSetupState> = _state.asStateFlow()

    fun update(transform: (WorkerSetupState) -> WorkerSetupState) { _state.update(transform) }

    fun toggleCategory(c: Category) {
        _state.update {
            val cats = if (it.categories.contains(c)) it.categories - c else it.categories + c
            it.copy(categories = cats)
        }
    }

    fun finish() {
        val s = _state.value
        if (s.categories.isEmpty()) {
            _state.update { it.copy(error = "Pick at least one category") }; return
        }
        if (s.firstServiceTitle.isBlank() || s.firstServicePrice.isBlank()) {
            _state.update { it.copy(error = "Add a first service") }; return
        }
        _state.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            val worker = Worker(
                id = uid,
                displayName = auth.currentUser?.displayName ?: "",
                phone = auth.currentUser?.phoneNumber.orEmpty(),
                bio = s.bio,
                photoUrl = null,
                town = s.town,
                locality = s.locality,
                categories = s.categories.map { it.id },
                isAvailable = true,
                averageRating = 0f,
                ratingCount = 0,
                thumbsUpCount = 0,
                minPriceInr = s.firstServicePrice.toIntOrNull()
            )
            workerRepository.upsertWorkerProfile(worker)
                .onFailure { t -> _state.update { it.copy(saving = false, error = t.message) }; return@launch }

            val service = ServiceCard(
                id = "",
                workerId = uid,
                title = s.firstServiceTitle,
                description = "",
                priceType = s.firstServiceType,
                priceInr = s.firstServicePrice.toIntOrNull() ?: 0,
                active = true
            )
            workerRepository.upsertService(service)
                .onSuccess { _state.update { it.copy(saving = false, done = true) } }
                .onFailure { t -> _state.update { it.copy(saving = false, error = t.message) } }
        }
    }
}
