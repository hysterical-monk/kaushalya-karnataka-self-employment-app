package com.kaushalya.karnataka.presentation.worker.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.core.ui.components.EmptyState
import com.kaushalya.karnataka.core.ui.components.LoadingState
import com.kaushalya.karnataka.domain.model.HireRequest
import com.kaushalya.karnataka.domain.model.HireStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDashboardScreen(
    onProfileClick: () -> Unit,
    onServicesClick: () -> Unit,
    onPortfolioClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: WorkerDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard_title)) },
                actions = {
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Filled.Settings, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ElevatedCard(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(state.worker?.displayName.orEmpty(), style = MaterialTheme.typography.titleLarge)
                            Text(
                                if (state.worker?.isAvailable == true) stringResource(R.string.availability_available) else stringResource(R.string.availability_busy),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = state.worker?.isAvailable == true,
                            onCheckedChange = { viewModel.toggleAvailability() }
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = onProfileClick, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.Person, null); Text(" " + stringResource(R.string.dashboard_profile))
                        }
                        FilledTonalButton(onClick = onServicesClick, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.Work, null); Text(" " + stringResource(R.string.dashboard_services))
                        }
                        FilledTonalButton(onClick = onPortfolioClick, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.PhotoLibrary, null); Text(" " + stringResource(R.string.dashboard_portfolio))
                        }
                    }
                }
            }

            Text(
                stringResource(R.string.dashboard_incoming_hires),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when {
                state.loading -> LoadingState()
                state.incoming.isEmpty() -> EmptyState(stringResource(R.string.dashboard_no_hires))
                else -> LazyColumn {
                    items(state.incoming, key = { it.id }) { req ->
                        HireRequestRow(req, onMarkSeen = { viewModel.markStatus(req.id, HireStatus.SEEN) },
                            onComplete = { viewModel.markStatus(req.id, HireStatus.COMPLETED) })
                    }
                }
            }
        }
    }
}

@Composable
private fun HireRequestRow(
    req: HireRequest,
    onMarkSeen: () -> Unit,
    onComplete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(req.customerName, style = MaterialTheme.typography.titleSmall)
            req.serviceTitle?.let { Text("Service: $it", style = MaterialTheme.typography.bodySmall) }
            Text(req.message, style = MaterialTheme.typography.bodyMedium)
            Text("Status: ${req.status.asString().replaceFirstChar(Char::titlecase)}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (req.status == HireStatus.PENDING) OutlinedButton(onClick = onMarkSeen) { Text("Mark Seen") }
                if (req.status != HireStatus.COMPLETED) OutlinedButton(onClick = onComplete) { Text("Mark Done") }
            }
        }
    }
}
