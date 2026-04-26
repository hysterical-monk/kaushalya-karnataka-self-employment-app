package com.kaushalya.karnataka.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaushalya.karnataka.core.ui.components.CategoryChip
import com.kaushalya.karnataka.domain.model.Category
import com.kaushalya.karnataka.domain.model.PriceType
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkerSetupScreen(
    onFinish: () -> Unit,
    viewModel: WorkerSetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.done) { if (state.done) onFinish() }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Welcome, let's set you up", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Step ${pagerState.currentPage + 1} of 4",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) { page ->
            when (page) {
                0 -> StepLocation(state, viewModel)
                1 -> StepCategories(state, viewModel)
                2 -> StepFirstService(state, viewModel)
                3 -> StepDone(state)
            }
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                enabled = pagerState.currentPage > 0 && !state.saving
            ) { Text("Back") }
            Button(
                onClick = {
                    when (pagerState.currentPage) {
                        0, 1 -> scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        2 -> {
                            viewModel.finish()
                            scope.launch { pagerState.animateScrollToPage(3) }
                        }
                        3 -> onFinish()
                    }
                },
                enabled = !state.saving
            ) {
                Text(
                    when (pagerState.currentPage) {
                        0, 1 -> "Next"
                        2 -> if (state.saving) "Saving…" else "Finish setup"
                        else -> "Go to dashboard"
                    }
                )
            }
        }
    }
}

@Composable
private fun StepLocation(state: WorkerSetupState, vm: WorkerSetupViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Where do you work?", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = state.town, onValueChange = { v -> vm.update { it.copy(town = v) } },
            label = { Text("Town (e.g. Mysuru)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.locality, onValueChange = { v -> vm.update { it.copy(locality = v) } },
            label = { Text("Locality / area") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.bio, onValueChange = { v -> vm.update { it.copy(bio = v) } },
            label = { Text("A short intro (optional)") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StepCategories(state: WorkerSetupState, vm: WorkerSetupViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Pick your skills", style = MaterialTheme.typography.titleMedium)
        Text("Choose all that apply. You can change these later.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(Category.entries) { c ->
                CategoryChip(
                    label = c.displayName,
                    selected = state.categories.contains(c),
                    onClick = { vm.toggleCategory(c) }
                )
            }
        }
    }
}

@Composable
private fun StepFirstService(state: WorkerSetupState, vm: WorkerSetupViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Add your first service", style = MaterialTheme.typography.titleMedium)
        Text("Customers see this on your card. Add more later.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = state.firstServiceTitle,
            onValueChange = { v -> vm.update { it.copy(firstServiceTitle = v) } },
            label = { Text("Service title (e.g. Fan repair)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.firstServicePrice,
            onValueChange = { v -> vm.update { it.copy(firstServicePrice = v.filter { c -> c.isDigit() }) } },
            label = { Text("Price (₹)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.firstServiceType == PriceType.FIXED,
                onClick = { vm.update { it.copy(firstServiceType = PriceType.FIXED) } },
                label = { Text("Fixed") }
            )
            FilterChip(
                selected = state.firstServiceType == PriceType.STARTING_AT,
                onClick = { vm.update { it.copy(firstServiceType = PriceType.STARTING_AT) } },
                label = { Text("Starting at") }
            )
        }
    }
}

@Composable
private fun StepDone(state: WorkerSetupState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("You're all set!", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "Your profile is live. Customers can find and hire you starting now.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
