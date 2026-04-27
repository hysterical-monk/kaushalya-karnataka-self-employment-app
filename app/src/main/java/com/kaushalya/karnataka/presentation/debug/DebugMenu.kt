package com.kaushalya.karnataka.presentation.debug

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kaushalya.karnataka.BuildConfig

@Composable
fun DebugMenu(
    onDismiss: () -> Unit,
    onClearLocalData: () -> Unit,
    onForceCrash: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Debug menu") },
        text = {
            Text(
                "Build: ${BuildConfig.BUILD_TYPE} v${BuildConfig.VERSION_NAME}\n" +
                    "Choose an action:"
            )
        },
        confirmButton = {
            TextButton(onClick = { onClearLocalData(); onDismiss() }) { Text("Clear local data") }
        },
        dismissButton = {
            TextButton(onClick = { onForceCrash() }) { Text("Force crash (test Crashlytics)") }
        }
    )
}

fun forceCrash() {
    FirebaseCrashlytics.getInstance().log("manual debug crash triggered")
    throw RuntimeException("Debug menu force-crash")
}

fun clearLocalPrefs(context: Context) {
    listOf("onboarding_prefs", "location_prefs", "recently_viewed_prefs", "notification_prefs", "theme_prefs").forEach { name ->
        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
