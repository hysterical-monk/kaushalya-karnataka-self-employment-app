package com.kaushalya.karnataka.presentation.customer.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaushalya.karnataka.core.ui.components.CategoryGrid
import com.kaushalya.karnataka.core.ui.components.CompactWorkerTile
import com.kaushalya.karnataka.core.ui.components.FeaturedWorkerCard
import com.kaushalya.karnataka.core.ui.components.HomeSkeleton
import com.kaushalya.karnataka.core.ui.components.SectionHeader
import com.kaushalya.karnataka.domain.model.Category

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onWorkerClick: (String) -> Unit,
    onCategoryClick: (Category) -> Unit,
    onSeeAll: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.loading) {
        HomeSkeleton(modifier = Modifier.fillMaxSize())
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
                Text(
                    text = greeting() + (if (state.greetingName.isNotBlank()) ", ${state.greetingName.split(' ').first()}" else ""),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Find skilled workers near you",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (state.featured.isNotEmpty()) {
            item {
                val pagerState = rememberPagerState(pageCount = { state.featured.size })
                HorizontalPager(
                    state = pagerState,
                    pageSpacing = 12.dp,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) { page ->
                    val w = state.featured[page]
                    FeaturedWorkerCard(worker = w, onClick = { onWorkerClick(w.id) })
                }
            }
        }

        item {
            SectionHeader(title = "Browse by category")
            CategoryGrid(
                categories = Category.entries.filter { it != Category.OTHER },
                onCategoryClick = onCategoryClick
            )
        }

        if (state.topRated.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Top rated this week",
                    actionLabel = "See all",
                    onActionClick = onSeeAll
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.topRated, key = { "tr-" + it.id }) { w ->
                        CompactWorkerTile(
                            worker = w,
                            onClick = { onWorkerClick(w.id) },
                            modifier = Modifier.size(width = 160.dp, height = 220.dp)
                        )
                    }
                }
            }
        }

        if (state.recentlyJoined.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Recently joined",
                    actionLabel = "See all",
                    onActionClick = onSeeAll
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.recentlyJoined, key = { "rj-" + it.id }) { w ->
                        CompactWorkerTile(
                            worker = w,
                            onClick = { onWorkerClick(w.id) },
                            modifier = Modifier.size(width = 160.dp, height = 220.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun greeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}
