package com.kaushalya.karnataka.presentation.customer.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaushalya.karnataka.domain.model.Worker
import com.kaushalya.karnataka.domain.repository.BookmarkRepository
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookmarksState(
    val workers: List<Worker> = emptyList(),
    val loading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val workerRepository: WorkerRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val uid = auth.currentUser?.uid

    val state: StateFlow<BookmarksState> = if (uid == null) {
        flowOf(BookmarksState(loading = false))
    } else {
        bookmarkRepository.observeBookmarkedIds(uid).flatMapLatest { ids ->
            if (ids.isEmpty()) flowOf(BookmarksState(loading = false))
            else combine(ids.map { workerRepository.observeWorker(it) }) { arr ->
                BookmarksState(workers = arr.filterNotNull(), loading = false)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookmarksState())

    fun toggleBookmark(workerId: String) {
        val u = uid ?: return
        viewModelScope.launch {
            bookmarkRepository.toggle(u, workerId, currentlyBookmarked = true)
        }
    }
}
