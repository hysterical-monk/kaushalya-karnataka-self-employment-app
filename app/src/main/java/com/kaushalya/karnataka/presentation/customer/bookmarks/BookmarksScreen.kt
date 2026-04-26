package com.kaushalya.karnataka.presentation.customer.bookmarks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
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
import com.kaushalya.karnataka.core.ui.components.EmptyState
import com.kaushalya.karnataka.core.ui.components.WorkerCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onWorkerClick: (String) -> Unit,
    onBack: () -> Unit = {},
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.bookmarks_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = {
                refreshing = true
                scope.launch { delay(700); refreshing = false }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                state.loading -> BrowseSkeleton(modifier = Modifier.fillMaxSize())
                state.workers.isEmpty() -> EmptyState(
                    title = "No bookmarks yet",
                    message = stringResource(R.string.bookmarks_empty),
                    icon = Icons.Outlined.BookmarkBorder,
                    modifier = Modifier.fillMaxSize()
                )
                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.workers, key = { it.id }) { w ->
                        WorkerCard(
                            worker = w,
                            onClick = { onWorkerClick(w.id) },
                            onBookmarkClick = { viewModel.toggleBookmark(w.id) },
                            isBookmarked = true
                        )
                    }
                }
            }
        }
    }
    }
}
