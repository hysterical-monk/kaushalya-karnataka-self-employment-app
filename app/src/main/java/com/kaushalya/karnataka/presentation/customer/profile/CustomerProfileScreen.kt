package com.kaushalya.karnataka.presentation.customer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CustomerProfileScreen(
    onLanguageClick: () -> Unit,
    onSignOut: () -> Unit,
    onOpenPrivacy: () -> Unit = {},
    onOpenTerms: () -> Unit = {},
    onOpenBookmarks: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenImpact: () -> Unit = {},
    viewModel: CustomerProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showEditName by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(state.toast) {
        state.toast?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeToast()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.size(72.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(36.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(state.displayName.ifBlank { "Customer" }, style = MaterialTheme.typography.titleLarge)
                    Text(state.phone, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { showEditName = true }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit name")
                }
            }
            HorizontalDivider()
            Card(modifier = Modifier.padding(16.dp)) {
                ListItem(
                    headlineContent = { Text("Saved workers") },
                    leadingContent = { Icon(Icons.Filled.Bookmark, null) },
                    modifier = Modifier.fillMaxWidth().clickable { onOpenBookmarks() }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Notifications") },
                    leadingContent = { Icon(Icons.Filled.Notifications, null) },
                    modifier = Modifier.fillMaxWidth().clickable { onOpenNotifications() }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Community impact") },
                    supportingContent = { Text("See how the platform is growing") },
                    leadingContent = { Icon(Icons.Filled.Insights, null) },
                    modifier = Modifier.fillMaxWidth().clickable { onOpenImpact() }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Language") },
                    supportingContent = { Text(if (state.language == "kn") "ಕನ್ನಡ" else "English") },
                    leadingContent = { Icon(Icons.Filled.Language, null) },
                    modifier = Modifier.fillMaxWidth().clickable { onLanguageClick() }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Privacy policy") },
                    leadingContent = { Icon(Icons.Filled.Lock, null) },
                    modifier = Modifier.fillMaxWidth().clickable { onOpenPrivacy() }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Terms of service") },
                    leadingContent = { Icon(Icons.Filled.Description, null) },
                    modifier = Modifier.fillMaxWidth().clickable { onOpenTerms() }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Sign out", color = MaterialTheme.colorScheme.error) },
                    leadingContent = { Icon(Icons.Filled.Logout, null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.signOut(onSignOut) }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Delete account", color = MaterialTheme.colorScheme.error) },
                    supportingContent = { Text("Permanently removes your profile, bookmarks, and history.") },
                    leadingContent = { Icon(Icons.Filled.DeleteForever, null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.fillMaxWidth().clickable { showDeleteConfirm = true }
                )
            }
        }
    }

    if (showEditName) {
        EditNameDialog(
            initial = state.displayName,
            onSave = { viewModel.updateName(it); showEditName = false },
            onDismiss = { showEditName = false }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete account?") },
            text = { Text("This is permanent. Your profile, bookmarks, and reviews will be removed. You can re-register with the same number, but old data will be gone.") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; viewModel.deleteAccount(onSignOut) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun EditNameDialog(
    initial: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit name") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name.trim()) },
                enabled = name.trim().isNotBlank() && name.trim() != initial
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
