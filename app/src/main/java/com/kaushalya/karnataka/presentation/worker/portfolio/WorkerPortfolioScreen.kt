package com.kaushalya.karnataka.presentation.worker.portfolio

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.core.ui.components.EmptyState
import com.kaushalya.karnataka.core.ui.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerPortfolioScreen(
    onBack: () -> Unit,
    viewModel: WorkerPortfolioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var pendingUri by remember { mutableStateOf<String?>(null) }
    var caption by remember { mutableStateOf("") }

    val pickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) pendingUri = uri.toString()
    }

    val storageEnabled = false  // Toggle to true once Firebase Storage is enabled (Blaze plan)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.portfolio_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } }
            )
        },
        floatingActionButton = {
            if (storageEnabled) {
                FloatingActionButton(onClick = {
                    pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Icon(Icons.Filled.AddAPhoto, null)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (!storageEnabled) {
                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Photo uploads coming soon",
                            style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            "Portfolio uploads need Firebase Storage which requires the Blaze billing plan. Existing photos still display.",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
            when {
                state.loading -> LoadingState(modifier = Modifier.fillMaxSize())
                state.photos.isEmpty() -> EmptyState(stringResource(R.string.portfolio_empty), modifier = Modifier.fillMaxSize())
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.photos, key = { it.id }) { p ->
                        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                            AsyncImage(
                                model = p.imageUrl,
                                contentDescription = p.caption,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                            )
                            if (storageEnabled) {
                                IconButton(
                                    onClick = { viewModel.delete(p.id) },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(Icons.Filled.Close, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pendingUri?.let { uri ->
        AlertDialog(
            onDismissRequest = { pendingUri = null; caption = "" },
            title = { Text(stringResource(R.string.portfolio_caption_title)) },
            text = {
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text(stringResource(R.string.portfolio_caption_hint)) }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.upload(uri, caption)
                    pendingUri = null
                    caption = ""
                }) { Text(stringResource(R.string.action_upload)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingUri = null; caption = "" }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}
