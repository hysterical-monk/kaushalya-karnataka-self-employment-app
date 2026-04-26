package com.kaushalya.karnataka.presentation.customer.workerdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.core.ui.components.RatingStars
import com.kaushalya.karnataka.domain.model.PriceType
import com.kaushalya.karnataka.domain.model.Review
import com.kaushalya.karnataka.domain.model.ServiceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDetailScreen(
    workerId: String,
    onBack: () -> Unit,
    viewModel: WorkerDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.toast) {
        state.toast?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeToast()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.worker?.displayName ?: stringResource(R.string.worker_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val worker = state.worker
        if (worker == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.worker_detail_loading))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(worker.displayName, style = MaterialTheme.typography.headlineSmall)
                    if (worker.locality.isNotBlank()) Text(worker.locality, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    RatingStars(rating = worker.averageRating, count = worker.ratingCount)
                    if (worker.bio.isNotBlank()) Text(worker.bio, style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider()
            }

            item {
                Text(
                    stringResource(R.string.worker_detail_services),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(state.services, key = { it.id }) { service ->
                ServiceCardRow(service = service, onHire = { viewModel.hire("Hi, I'd like to hire you for: ${service.title}", service) })
            }

            if (state.portfolio.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.worker_detail_portfolio),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    LazyRow(
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.portfolio, key = { it.id }) { photo ->
                            AsyncImage(
                                model = photo.imageUrl,
                                contentDescription = photo.caption,
                                modifier = Modifier.size(140.dp).clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.worker_detail_review_wall),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                ReviewComposer(onPost = { stars, text -> viewModel.postReview(stars, text) })
                HorizontalDivider()
            }
            items(state.reviews, key = { it.id }) { review ->
                ReviewItem(review)
            }
        }
    }
}

@Composable
private fun ServiceCardRow(service: ServiceCard, onHire: () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(service.title, style = MaterialTheme.typography.titleSmall)
                if (service.description.isNotBlank()) Text(service.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val priceLabel = when (service.priceType) {
                    PriceType.FIXED -> "₹${service.priceInr}"
                    PriceType.STARTING_AT -> "Starting at ₹${service.priceInr}"
                }
                Text(priceLabel, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Button(onClick = onHire) { Text(stringResource(R.string.action_hire_me)) }
        }
    }
}

@Composable
private fun ReviewComposer(onPost: (Int, String) -> Unit) {
    var stars by remember { mutableIntStateOf(5) }
    var text by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.review_your_rating))
            Spacer(Modifier.size(8.dp))
            (1..5).forEach { i ->
                IconButton(onClick = { stars = i }) {
                    Icon(
                        imageVector = if (i <= stars) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(R.string.review_text_hint)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Button(
            onClick = {
                onPost(stars, text)
                text = ""
            },
            enabled = text.isNotBlank()
        ) { Text(stringResource(R.string.review_submit)) }
    }
}

@Composable
private fun ReviewItem(review: Review) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.customerName, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                RatingStars(rating = review.stars.toFloat())
            }
            if (review.text.isNotBlank()) Text(review.text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
