package com.kaushalya.karnataka.presentation.customer.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.core.ui.components.BrowseSkeleton
import com.kaushalya.karnataka.core.ui.components.CategoryChip
import com.kaushalya.karnataka.core.ui.components.EmptyState
import com.kaushalya.karnataka.core.ui.components.WorkerCard
import com.kaushalya.karnataka.domain.model.Category
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    onWorkerClick: (String) -> Unit,
    initialCategory: Category? = null,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(initialCategory) {
        if (initialCategory != null) viewModel.onCategorySelect(initialCategory)
    }
    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.browse_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChange,
            placeholder = { Text(stringResource(R.string.browse_search_hint)) },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                CategoryChip(
                    label = stringResource(R.string.category_all),
                    selected = state.selectedCategory == null,
                    onClick = { viewModel.onCategorySelect(null) }
                )
            }
            items(Category.entries) { cat ->
                CategoryChip(
                    label = cat.displayName,
                    selected = state.selectedCategory == cat,
                    onClick = { viewModel.onCategorySelect(cat) }
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = {
                refreshing = true
                scope.launch {
                    delay(700) // Firestore listener is real-time; gesture is ack-only
                    refreshing = false
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                state.loading -> BrowseSkeleton()
                state.workers.isEmpty() -> EmptyState(
                    title = "No workers found",
                    message = stringResource(R.string.browse_empty)
                )
                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.workers, key = { it.id }) { worker ->
                        WorkerCard(
                            worker = worker,
                            onClick = { onWorkerClick(worker.id) },
                            onBookmarkClick = { viewModel.toggleBookmark(worker.id) },
                            isBookmarked = state.bookmarkedIds.contains(worker.id)
                        )
                    }
                }
            }
        }
    }
}
