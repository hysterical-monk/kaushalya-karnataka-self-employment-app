package com.kaushalya.karnataka.presentation.customer.workerdetail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kaushalya.karnataka.core.ui.components.RatingStars
import com.kaushalya.karnataka.domain.model.PriceType
import com.kaushalya.karnataka.domain.model.Review
import com.kaushalya.karnataka.domain.model.ServiceCard
import com.kaushalya.karnataka.domain.model.Worker

private enum class DetailTab(val label: String) {
    ABOUT("About"), SERVICES("Services"), PORTFOLIO("Photos"), REVIEWS("Reviews")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDetailScreen(
    workerId: String,
    onBack: () -> Unit,
    viewModel: WorkerDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var moreMenuOpen by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var photoViewerStartIndex by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(state.toast) {
        state.toast?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeToast()
        }
    }

    val worker = state.worker
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(worker?.displayName ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                    if (worker != null) {
                        IconButton(onClick = {
                            val text = "Check out ${worker.displayName} on Kaushalya Karnataka — ${worker.categories.joinToString()}, ${worker.locality}"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share worker"))
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "Share")
                        }
                        Box {
                            IconButton(onClick = { moreMenuOpen = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "More")
                            }
                            DropdownMenu(expanded = moreMenuOpen, onDismissRequest = { moreMenuOpen = false }) {
                                DropdownMenuItem(
                                    text = { Text("Report worker") },
                                    onClick = {
                                        moreMenuOpen = false
                                        showReportDialog = true
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            if (worker != null) {
                StickyHireBar(
                    worker = worker,
                    sending = state.sending,
                    onHire = { viewModel.hire("Hi, I'd like to hire you", null) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (worker == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Loading worker…")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())
        ) {
            // Hero
            item {
                HeroSection(worker = worker, topInset = padding.calculateTopPadding())
            }
            // Tabs
            item {
                SecondaryTabRow(selectedTabIndex = selectedTab) {
                    DetailTab.entries.forEachIndexed { i, tab ->
                        Tab(
                            selected = selectedTab == i,
                            onClick = { selectedTab = i },
                            text = { Text(tab.label) }
                        )
                    }
                }
            }
            when (DetailTab.entries[selectedTab]) {
                DetailTab.ABOUT -> aboutTab(worker)
                DetailTab.SERVICES -> servicesTab(state.services) { svc ->
                    viewModel.hire("Hi, I'd like to hire you for: ${svc.title}", svc)
                }
                DetailTab.PORTFOLIO -> portfolioTab(state.portfolio.map { it.imageUrl }) { idx ->
                    photoViewerStartIndex = idx
                }
                DetailTab.REVIEWS -> reviewsTab(state.reviews) { stars, text ->
                    viewModel.postReview(stars, text)
                }
            }
        }
    }

    if (showReportDialog) {
        ReportDialog(
            onSubmit = { reason ->
                viewModel.reportWorker(reason)
                showReportDialog = false
            },
            onDismiss = { showReportDialog = false }
        )
    }

    photoViewerStartIndex?.let { startIdx ->
        com.kaushalya.karnataka.core.ui.components.PhotoViewerDialog(
            urls = state.portfolio.map { it.imageUrl },
            initialIndex = startIdx,
            onDismiss = { photoViewerStartIndex = null }
        )
    }
}

@Composable
private fun ReportDialog(onSubmit: (String) -> Unit, onDismiss: () -> Unit) {
    var reason by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report this worker") },
        text = {
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("What's wrong? (e.g. fake profile)") },
                minLines = 2
            )
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(reason.trim()) }, enabled = reason.trim().length >= 5) {
                Text("Submit")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun HeroSection(worker: Worker, topInset: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        if (!worker.photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = worker.photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer))
        }
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(Color(0x66000000), Color.Transparent, Color(0xCC000000))
                )
            )
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(color = MaterialTheme.colorScheme.tertiary, shape = MaterialTheme.shapes.small) {
                Text(
                    text = "★ ${"%.1f".format(worker.averageRating)}  •  ${worker.ratingCount} reviews",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Text(worker.displayName, style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Text(
                text = listOfNotNull(
                    worker.categories.joinToString(" • ") { it.replaceFirstChar(Char::titlecase) }.takeIf { it.isNotBlank() },
                    worker.locality.takeIf { it.isNotBlank() },
                    worker.town.takeIf { it.isNotBlank() }
                ).joinToString("  ·  "),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.aboutTab(worker: Worker) {
    item {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (worker.bio.isNotBlank()) {
                Text("About", style = MaterialTheme.typography.titleMedium)
                Text(worker.bio, style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider()
            InfoRow(label = "Town", value = worker.town.ifBlank { "—" })
            InfoRow(label = "Locality", value = worker.locality.ifBlank { "—" })
            InfoRow(
                label = "Availability",
                value = if (worker.isAvailable) "Available now" else "Currently busy",
                valueColor = if (worker.isAvailable) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
            )
            if (worker.minPriceInr != null) {
                InfoRow(label = "Starting at", value = "₹${worker.minPriceInr}")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = valueColor)
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.servicesTab(
    services: List<ServiceCard>,
    onHire: (ServiceCard) -> Unit
) {
    if (services.isEmpty()) {
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No services listed yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }
    items(services, key = { it.id }) { service ->
        ServiceCardRow(service = service, onHire = { onHire(service) })
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
            Button(onClick = onHire) { Text("Hire") }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.portfolioTab(
    urls: List<String>,
    onPhotoTap: (Int) -> Unit
) {
    if (urls.isEmpty()) {
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No photos uploaded", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }
    item {
        LazyRow(
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(urls) { index, url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onPhotoTap(index) }
                )
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.reviewsTab(
    reviews: List<Review>,
    onPost: (stars: Int, text: String) -> Unit
) {
    item { ReviewComposer(onPost = onPost) }
    item { HorizontalDivider() }
    if (reviews.isEmpty()) {
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Be the first to review", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }
    items(reviews, key = { it.id }) { review -> ReviewItem(review) }
}

@Composable
private fun ReviewComposer(onPost: (Int, String) -> Unit) {
    var stars by remember { mutableIntStateOf(5) }
    var text by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Your rating")
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
            label = { Text("Tell others how the work went") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Button(
            onClick = { onPost(stars, text); text = "" },
            enabled = text.isNotBlank()
        ) { Text("Post review") }
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

@Composable
private fun StickyHireBar(worker: Worker, sending: Boolean, onHire: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (worker.minPriceInr != null) {
                    Text(
                        text = "From ₹${worker.minPriceInr}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = if (worker.isAvailable) "● Available" else "● Busy",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (worker.isAvailable) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                )
            }
            Button(onClick = onHire, enabled = !sending && worker.isAvailable) {
                Text(if (sending) "Sending…" else "Hire me")
            }
        }
    }
}
