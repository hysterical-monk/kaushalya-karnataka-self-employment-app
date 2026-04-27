package com.kaushalya.karnataka.presentation.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.kaushalya.karnataka.core.prefs.NotificationPrefs
import com.kaushalya.karnataka.core.prefs.NotificationPrefsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val store: NotificationPrefsStore
) : ViewModel() {
    fun load(): NotificationPrefs = store.load()
    fun save(p: NotificationPrefs) = store.save(p)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    var prefs by remember { mutableStateOf(viewModel.load()) }

    fun update(p: NotificationPrefs) {
        prefs = p
        viewModel.save(p)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                "Choose which alerts you want to receive. Some channels need the Blaze backend; toggles persist regardless.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalDivider()
            ToggleRow(
                title = "Hire requests",
                subtitle = "Get notified when a customer wants to hire you",
                checked = prefs.hireRequests,
                onChange = { update(prefs.copy(hireRequests = it)) }
            )
            HorizontalDivider()
            ToggleRow(
                title = "New reviews",
                subtitle = "When someone leaves a review on your profile",
                checked = prefs.newReviews,
                onChange = { update(prefs.copy(newReviews = it)) }
            )
            HorizontalDivider()
            ToggleRow(
                title = "Chat messages",
                subtitle = "New messages from customers or workers",
                checked = prefs.chatMessages,
                onChange = { update(prefs.copy(chatMessages = it)) }
            )
            HorizontalDivider()
            ToggleRow(
                title = "Jobs for me",
                subtitle = "When customers post jobs matching your skills + town",
                checked = prefs.jobsForMe,
                onChange = { update(prefs.copy(jobsForMe = it)) }
            )
        }
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onChange) },
        modifier = Modifier.fillMaxWidth()
    )
}
