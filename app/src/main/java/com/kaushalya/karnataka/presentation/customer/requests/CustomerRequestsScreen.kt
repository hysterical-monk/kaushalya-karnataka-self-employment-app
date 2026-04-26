package com.kaushalya.karnataka.presentation.customer.requests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaushalya.karnataka.core.ui.components.EmptyState
import com.kaushalya.karnataka.core.ui.components.LoadingState
import com.kaushalya.karnataka.domain.model.HireRequest
import com.kaushalya.karnataka.domain.model.HireStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CustomerRequestsScreen(
    onWorkerClick: (String) -> Unit,
    onChatClick: (customerId: String, workerId: String, title: String) -> Unit,
    viewModel: CustomerRequestsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "My requests",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        when {
            state.loading -> LoadingState(modifier = Modifier.fillMaxSize())
            state.requests.isEmpty() -> EmptyState(
                title = "No requests yet",
                message = "When you tap Hire me on a worker, the request will appear here.",
                icon = Icons.Outlined.Inbox,
                modifier = Modifier.fillMaxSize()
            )
            else -> {
                val grouped = state.requests.groupBy { it.status }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    HireStatus.entries.forEach { status ->
                        val list = grouped[status].orEmpty()
                        if (list.isEmpty()) return@forEach
                        item {
                            Text(
                                text = sectionLabel(status, list.size),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
                            )
                        }
                        items(list, key = { it.id }) { req ->
                            RequestRow(
                                req = req,
                                customerId = viewModel.currentUid,
                                onWorkerClick = { onWorkerClick(req.workerId) },
                                onCancel = { viewModel.cancel(req.id) },
                                onChat = { onChatClick(viewModel.currentUid, req.workerId, req.serviceTitle ?: "Chat") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestRow(
    req: HireRequest,
    customerId: String,
    onWorkerClick: () -> Unit,
    onCancel: () -> Unit,
    onChat: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = req.serviceTitle ?: "General request",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onWorkerClick)
                )
                StatusBadge(req.status)
            }
            Text(req.message, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = formatTime(req.createdAtMillis),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (req.status == HireStatus.PENDING || req.status == HireStatus.SEEN) {
                    OutlinedButton(onClick = onCancel) {
                        Icon(Icons.Filled.Close, null, modifier = Modifier.padding(end = 4.dp))
                        Text("Cancel")
                    }
                }
                if (req.status != HireStatus.CANCELLED && req.status != HireStatus.DECLINED) {
                    OutlinedButton(onClick = onChat) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.padding(end = 4.dp))
                        Text("Chat")
                    }
                }
                if (req.status == HireStatus.COMPLETED) {
                    OutlinedButton(onClick = onWorkerClick) {
                        Icon(Icons.Filled.Star, null, modifier = Modifier.padding(end = 4.dp))
                        Text("Leave a review")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: HireStatus) {
    val (bg, fg, label) = when (status) {
        HireStatus.PENDING -> Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, "Pending")
        HireStatus.SEEN -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "Seen")
        HireStatus.COMPLETED -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, "Done")
        HireStatus.CANCELLED -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "Cancelled")
        HireStatus.DECLINED -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "Declined")
    }
    Surface(color = bg, shape = MaterialTheme.shapes.small) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

private fun sectionLabel(status: HireStatus, count: Int): String {
    val title = when (status) {
        HireStatus.PENDING -> "Pending"
        HireStatus.SEEN -> "Seen by worker"
        HireStatus.COMPLETED -> "Completed"
        HireStatus.CANCELLED -> "Cancelled"
        HireStatus.DECLINED -> "Declined"
    }
    return "$title ($count)"
}

private fun formatTime(millis: Long): String {
    if (millis == 0L) return ""
    val now = System.currentTimeMillis()
    val diff = now - millis
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000} min ago"
        diff < 86_400_000 -> "${diff / 3_600_000} hr ago"
        else -> SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(millis))
    }
}
