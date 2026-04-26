package com.kaushalya.karnataka.presentation.customer.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaushalya.karnataka.core.analytics.AnalyticsEvent
import com.kaushalya.karnataka.core.analytics.AnalyticsTracker
import com.kaushalya.karnataka.domain.model.Category
import com.kaushalya.karnataka.domain.model.Worker
import com.kaushalya.karnataka.domain.repository.BookmarkRepository
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import com.kaushalya.karnataka.domain.usecase.FilterByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BrowseState(
    val query: String = "",
    val selectedCategory: Category? = null,
    val workers: List<Worker> = emptyList(),
    val bookmarkedIds: Set<String> = emptySet(),
    val loading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val workerRepository: WorkerRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val filterByCategoryUseCase: FilterByCategoryUseCase,
    private val auth: FirebaseAuth,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val category = MutableStateFlow<Category?>(null)

    private val workers = category.flatMapLatest { cat ->
        workerRepository.observeWorkers(cat?.id)
    }

    private val bookmarks = auth.currentUser?.uid?.let { uid ->
        bookmarkRepository.observeBookmarkedIds(uid)
    } ?: flowOf(emptySet())

    val state: StateFlow<BrowseState> = combine(workers, query, category, bookmarks) { all, q, cat, bm ->
        BrowseState(
            query = q,
            selectedCategory = cat,
            workers = filterByCategoryUseCase(all, q, cat?.id),
            bookmarkedIds = bm,
            loading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BrowseState())

    fun onQueryChange(value: String) { query.value = value }

    fun onCategorySelect(cat: Category?) {
        category.value = cat
        analytics.log(AnalyticsEvent.CategoryFiltered(cat?.id))
    }

    fun toggleBookmark(workerId: String) {
        val uid = auth.currentUser?.uid ?: return
        val currently = state.value.bookmarkedIds.contains(workerId)
        viewModelScope.launch {
            bookmarkRepository.toggle(uid, workerId, currently)
            analytics.log(AnalyticsEvent.BookmarkToggled(added = !currently))
        }
    }
}
