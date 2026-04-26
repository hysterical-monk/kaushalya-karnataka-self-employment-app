package com.kaushalya.karnataka.presentation.customer.jobs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaushalya.karnataka.core.ui.components.CategoryChip
import com.kaushalya.karnataka.core.ui.components.EmptyState
import com.kaushalya.karnataka.core.ui.components.LoadingState
import com.kaushalya.karnataka.domain.model.Category
import com.kaushalya.karnataka.domain.model.JobPost
import com.kaushalya.karnataka.domain.model.JobStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerJobsScreen(
    viewModel: CustomerJobsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showPostDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showPostDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Post a job")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = "Job board",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
            )
            Text(
                text = "Post what you need. Workers in your town will see it.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            when {
                state.loading -> LoadingState(modifier = Modifier.fillMaxSize())
                state.myJobs.isEmpty() -> EmptyState(
                    title = "No jobs posted yet",
                    message = "Tap + to post your first job.",
                    icon = Icons.Outlined.WorkOutline,
                    modifier = Modifier.fillMaxSize()
                )
                else -> LazyColumn {
                    items(state.myJobs, key = { it.id }) { job ->
                        JobRow(job, onClose = { viewModel.closeJob(job.id) })
                    }
                }
            }
        }
    }

    if (showPostDialog) {
        PostJobDialog(
            onSubmit = { title, desc, cat, town, budget ->
                viewModel.postJob(title, desc, cat, town, budget) { showPostDialog = false }
            },
            onDismiss = { showPostDialog = false }
        )
    }
}

@Composable
private fun JobRow(job: JobPost, onClose: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(job.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = MaterialTheme.shapes.small) {
                    Text(
                        when (job.status) {
                            JobStatus.OPEN -> "Open"
                            JobStatus.AWARDED -> "Awarded"
                            JobStatus.CANCELLED -> "Closed"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(job.description, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${Category.fromId(job.categoryId).displayName}  •  ${job.town}" +
                    (job.budgetMaxInr?.let { "  •  Budget ₹$it" } ?: ""),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (job.status == JobStatus.OPEN) {
                Row {
                    TextButton(onClick = onClose) { Text("Close job") }
                }
            }
        }
    }
}

@Composable
private fun PostJobDialog(
    onSubmit: (title: String, description: String, category: Category, town: String, budget: Int?) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var town by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(Category.ELECTRICIAN) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Post a job") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title (e.g. Need a plumber)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Describe the work") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                Text("Category", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(Category.entries.filter { it != Category.OTHER }) { c ->
                        CategoryChip(label = c.displayName, selected = category == c, onClick = { category = c })
                    }
                }
                OutlinedTextField(value = town, onValueChange = { town = it }, label = { Text("Your town") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it.filter { c -> c.isDigit() } },
                    label = { Text("Max budget ₹ (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = { onSubmit(title.trim(), desc.trim(), category, town.trim(), budget.toIntOrNull()) },
                enabled = title.isNotBlank() && desc.isNotBlank() && town.isNotBlank()
            ) { Text("Post") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
