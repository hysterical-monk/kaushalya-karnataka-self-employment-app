package com.kaushalya.karnataka.presentation.worker.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaushalya.karnataka.domain.model.Category
import com.kaushalya.karnataka.domain.model.Worker
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileFormState(
    val displayName: String = "",
    val bio: String = "",
    val town: String = "",
    val locality: String = "",
    val phone: String = "",
    val photoUrl: String? = null,
    val categories: Set<Category> = emptySet(),
    val available: Boolean = true,
    val openHour: Int? = null,
    val closeHour: Int? = null,
    val workingDays: Set<Int> = emptySet(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WorkerProfileViewModel @Inject constructor(
    private val workerRepository: WorkerRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val uid: String = auth.currentUser?.uid.orEmpty()

    private val _state = MutableStateFlow(ProfileFormState())
    val state: StateFlow<ProfileFormState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            workerRepository.observeWorker(uid).collect { w ->
                if (w != null && _state.value.displayName.isBlank()) hydrate(w)
            }
        }
    }

    private fun hydrate(w: Worker) {
        _state.update {
            it.copy(
                displayName = w.displayName,
                bio = w.bio,
                town = w.town,
                locality = w.locality,
                phone = w.phone,
                photoUrl = w.photoUrl,
                categories = w.categories.map { id -> Category.fromId(id) }.toSet(),
                available = w.isAvailable,
                openHour = w.openHour,
                closeHour = w.closeHour,
                workingDays = w.workingDays.toSet()
            )
        }
    }

    fun toggleWorkingDay(day: Int) {
        _state.update {
            val days = if (it.workingDays.contains(day)) it.workingDays - day else it.workingDays + day
            it.copy(workingDays = days)
        }
    }

    fun update(transform: (ProfileFormState) -> ProfileFormState) { _state.update(transform) }

    fun toggleCategory(c: Category) {
        _state.update {
            val cats = if (it.categories.contains(c)) it.categories - c else it.categories + c
            it.copy(categories = cats)
        }
    }

    fun save() {
        val s = _state.value
        if (s.displayName.isBlank()) {
            _state.update { it.copy(error = "Name is required") }; return
        }
        _state.update { it.copy(saving = true, error = null, saved = false) }
        viewModelScope.launch {
            val worker = Worker(
                id = uid,
                displayName = s.displayName,
                phone = s.phone,
                bio = s.bio,
                photoUrl = s.photoUrl,
                town = s.town,
                locality = s.locality,
                categories = s.categories.map { it.id },
                isAvailable = s.available,
                averageRating = 0f,
                ratingCount = 0,
                thumbsUpCount = 0,
                openHour = s.openHour,
                closeHour = s.closeHour,
                workingDays = s.workingDays.sorted()
            )
            workerRepository.upsertWorkerProfile(worker)
                .onSuccess { _state.update { it.copy(saving = false, saved = true) } }
                .onFailure { t -> _state.update { it.copy(saving = false, error = t.message) } }
        }
    }
}
