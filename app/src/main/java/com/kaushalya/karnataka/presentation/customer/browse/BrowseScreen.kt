package com.kaushalya.karnataka.presentation.customer.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.core.ui.components.CategoryChip
import com.kaushalya.karnataka.core.ui.components.EmptyState
import com.kaushalya.karnataka.core.ui.components.LoadingState
import com.kaushalya.karnataka.core.ui.components.WorkerCard
import com.kaushalya.karnataka.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    onWorkerClick: (String) -> Unit,
    onBookmarksClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.browse_title)) },
                actions = {
                    IconButton(onClick = onBookmarksClick) {
                        Icon(Icons.Filled.Bookmark, contentDescription = stringResource(R.string.bookmarks_title))
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_title))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                placeholder = { Text(stringResource(R.string.browse_search_hint)) },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp)
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

            when {
                state.loading -> LoadingState()
                state.workers.isEmpty() -> EmptyState(stringResource(R.string.browse_empty))
                else -> LazyColumn {
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
