package com.kaushalya.karnataka.presentation.worker.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.core.ui.components.CategoryChip
import com.kaushalya.karnataka.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerProfileScreen(
    onBack: () -> Unit,
    viewModel: WorkerProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.displayName,
                onValueChange = { v -> viewModel.update { it.copy(displayName = v) } },
                label = { Text(stringResource(R.string.profile_name)) },
                isError = state.error?.contains("Name") == true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.bio,
                onValueChange = { v -> viewModel.update { it.copy(bio = v) } },
                label = { Text(stringResource(R.string.profile_bio)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            OutlinedTextField(
                value = state.town,
                onValueChange = { v -> viewModel.update { it.copy(town = v) } },
                label = { Text(stringResource(R.string.profile_town)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.locality,
                onValueChange = { v -> viewModel.update { it.copy(locality = v) } },
                label = { Text(stringResource(R.string.profile_locality)) },
                modifier = Modifier.fillMaxWidth()
            )
            Text(stringResource(R.string.profile_categories), style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(Category.entries) { c ->
                    CategoryChip(
                        label = c.displayName,
                        selected = state.categories.contains(c),
                        onClick = { viewModel.toggleCategory(c) }
                    )
                }
            }

            Text("Working hours (optional)", style = MaterialTheme.typography.titleMedium)
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.openHour?.toString().orEmpty(),
                    onValueChange = { v -> viewModel.update { it.copy(openHour = v.toIntOrNull()?.coerceIn(0, 23)) } },
                    label = { Text("Open (0-23)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.closeHour?.toString().orEmpty(),
                    onValueChange = { v -> viewModel.update { it.copy(closeHour = v.toIntOrNull()?.coerceIn(0, 23)) } },
                    label = { Text("Close (0-23)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Text("Working days", style = MaterialTheme.typography.bodyMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                items(labels.size) { idx ->
                    val day = idx + 1
                    CategoryChip(
                        label = labels[idx],
                        selected = state.workingDays.contains(day),
                        onClick = { viewModel.toggleWorkingDay(day) }
                    )
                }
            }
            Button(
                onClick = viewModel::save,
                enabled = !state.saving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.saving) stringResource(R.string.action_saving) else stringResource(R.string.action_save))
            }
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
