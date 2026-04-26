package com.kaushalya.karnataka.presentation.worker.services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.core.ui.components.EmptyState
import com.kaushalya.karnataka.core.ui.components.LoadingState
import com.kaushalya.karnataka.domain.model.PriceType
import com.kaushalya.karnataka.domain.model.ServiceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerServicesScreen(
    onBack: () -> Unit,
    viewModel: WorkerServicesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<ServiceCard?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.services_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showDialog = true }) {
                Icon(Icons.Filled.Add, null)
            }
        }
    ) { padding ->
        when {
            state.loading -> LoadingState(modifier = Modifier.fillMaxSize().padding(padding))
            state.services.isEmpty() -> EmptyState(stringResource(R.string.services_empty), modifier = Modifier.fillMaxSize().padding(padding))
            else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(state.services, key = { it.id }) { svc ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(svc.title, style = MaterialTheme.typography.titleMedium)
                                if (svc.description.isNotBlank()) Text(svc.description, style = MaterialTheme.typography.bodySmall)
                                val priceLabel = when (svc.priceType) {
                                    PriceType.FIXED -> "₹${svc.priceInr}"
                                    PriceType.STARTING_AT -> "Starting at ₹${svc.priceInr}"
                                }
                                Text(priceLabel, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { editing = svc; showDialog = true }) { Icon(Icons.Filled.Edit, null) }
                            IconButton(onClick = { viewModel.delete(svc.id) }) { Icon(Icons.Filled.Delete, null) }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        ServiceDialog(
            initial = editing,
            onDismiss = { showDialog = false },
            onSave = { id, title, desc, type, price, active ->
                viewModel.upsert(id, title, desc, type, price, active)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ServiceDialog(
    initial: ServiceCard?,
    onDismiss: () -> Unit,
    onSave: (id: String, title: String, description: String, priceType: PriceType, priceInr: Int, active: Boolean) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title.orEmpty()) }
    var desc by remember { mutableStateOf(initial?.description.orEmpty()) }
    var price by remember { mutableStateOf(initial?.priceInr?.toString().orEmpty()) }
    var priceType by remember { mutableStateOf(initial?.priceType ?: PriceType.FIXED) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) stringResource(R.string.services_add) else stringResource(R.string.services_edit)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.services_field_title)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text(stringResource(R.string.services_field_description)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it.filter { c -> c.isDigit() } }, label = { Text(stringResource(R.string.services_field_price)) }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = priceType == PriceType.FIXED, onClick = { priceType = PriceType.FIXED }, label = { Text(stringResource(R.string.services_price_fixed)) })
                    FilterChip(selected = priceType == PriceType.STARTING_AT, onClick = { priceType = PriceType.STARTING_AT }, label = { Text(stringResource(R.string.services_price_starting)) })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(initial?.id.orEmpty(), title.trim(), desc.trim(), priceType, price.toIntOrNull() ?: 0, true)
                },
                enabled = title.isNotBlank() && price.isNotBlank()
            ) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}
