package com.kaushalya.karnataka.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.kaushalya.karnataka.core.prefs.ColorPalette
import com.kaushalya.karnataka.core.prefs.ThemeMode
import com.kaushalya.karnataka.core.prefs.ThemePrefs

@Composable
fun KaushalyaTheme(
    prefs: ThemePrefs = ThemePrefs(),
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (prefs.mode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val context = LocalContext.current
    val canDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val scheme = when {
        prefs.dynamic && canDynamic -> if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        else -> {
            val (light, dark) = schemesFor(prefs.palette)
            if (isDark) dark else light
        }
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = AppTypography,
        content = content
    )
}
