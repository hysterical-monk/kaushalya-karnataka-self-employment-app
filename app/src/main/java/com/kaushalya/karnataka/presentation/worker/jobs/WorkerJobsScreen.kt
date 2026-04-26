package com.kaushalya.karnataka.presentation.worker.jobs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaushalya.karnataka.core.ui.components.EmptyState
import com.kaushalya.karnataka.core.ui.components.LoadingState
import com.kaushalya.karnataka.domain.model.Category
import com.kaushalya.karnataka.domain.model.JobPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerJobsScreen(
    onBack: () -> Unit,
    viewModel: WorkerJobsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jobs for me") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        when {
            state.loading -> LoadingState(modifier = Modifier.fillMaxSize().padding(padding))
            state.jobs.isEmpty() -> EmptyState(
                title = "No matching jobs yet",
                message = "Customers in your town haven't posted anything matching your skills. Check back later.",
                icon = Icons.Outlined.WorkOutline,
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(state.jobs, key = { it.id }) { job -> JobCard(job) }
            }
        }
    }
}

@Composable
private fun JobCard(job: JobPost) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(job.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                if (job.budgetMaxInr != null) {
                    Text("≤ ₹${job.budgetMaxInr}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            Text("Posted by ${job.customerName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(job.description, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${Category.fromId(job.categoryId).displayName}  •  ${job.town}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
