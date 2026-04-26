package com.kaushalya.karnataka.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaushalya.karnataka.R

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.settings_language), style = MaterialTheme.typography.titleMedium)
            androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = state.language == "en", onClick = { viewModel.setLanguage("en") }, label = { Text("English") })
                FilterChip(selected = state.language == "kn", onClick = { viewModel.setLanguage("kn") }, label = { Text("ಕನ್ನಡ") })
                FilterChip(selected = state.language == "ta", onClick = { viewModel.setLanguage("ta") }, label = { Text("தமிழ்") })
                FilterChip(selected = state.language == "te", onClick = { viewModel.setLanguage("te") }, label = { Text("తెలుగు") })
                FilterChip(selected = state.language == "hi", onClick = { viewModel.setLanguage("hi") }, label = { Text("हिन्दी") })
            }
            HorizontalDivider()
            Text(stringResource(R.string.settings_about_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.settings_about_body), color = MaterialTheme.colorScheme.onSurfaceVariant)
            HorizontalDivider()
            OutlinedButton(
                onClick = { viewModel.signOut(onSignedOut) },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.settings_sign_out)) }
        }
    }
}
