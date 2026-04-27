package com.kaushalya.karnataka.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.kaushalya.karnataka.core.prefs.ColorPalette

/** Returns a (light, dark) ColorScheme pair for the given palette. */
fun schemesFor(palette: ColorPalette): Pair<ColorScheme, ColorScheme> = when (palette) {
    ColorPalette.SAFFRON -> saffronLight() to saffronDark()
    ColorPalette.INDIGO -> indigoLight() to indigoDark()
    ColorPalette.FOREST -> forestLight() to forestDark()
    ColorPalette.CORAL -> coralLight() to coralDark()
}

// ─────── Karnataka Saffron (default) ───────
private fun saffronLight() = lightColorScheme(
    primary = Color(0xFFB8860B), onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE082), onPrimaryContainer = Color(0xFF3E2723),
    secondary = Color(0xFF2E7D32), onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8E6C9), onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = Color(0xFFD32F2F), onTertiary = Color.White,
    background = Color(0xFFFFFBF5), onBackground = Color(0xFF1F1B16),
    surface = Color(0xFFFFFBF5), onSurface = Color(0xFF1F1B16),
    error = Color(0xFFB00020), onError = Color.White
)

private fun saffronDark() = darkColorScheme(
    primary = Color(0xFFFFD54F), onPrimary = Color(0xFF2A1A00),
    primaryContainer = Color(0xFF8C5A00), onPrimaryContainer = Color(0xFFFFE9B0),
    secondary = Color(0xFF9DD49B), onSecondary = Color(0xFF0E2F12),
    secondaryContainer = Color(0xFF1F5523), onSecondaryContainer = Color(0xFFD0F0CE),
    tertiary = Color(0xFFFFB4A8), onTertiary = Color(0xFF560E08),
    background = Color(0xFF14110D), onBackground = Color(0xFFEFE6DA),
    surface = Color(0xFF14110D), onSurface = Color(0xFFEFE6DA),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005)
)

// ─────── Indigo Mango ───────
private fun indigoLight() = lightColorScheme(
    primary = Color(0xFF3F51B5), onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE1FF), onPrimaryContainer = Color(0xFF001257),
    secondary = Color(0xFFFF9800), onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2), onSecondaryContainer = Color(0xFF422600),
    tertiary = Color(0xFF7B1FA2), onTertiary = Color.White,
    background = Color(0xFFFAFAFF), onBackground = Color(0xFF1B1B23),
    surface = Color(0xFFFAFAFF), onSurface = Color(0xFF1B1B23),
    error = Color(0xFFB00020), onError = Color.White
)

private fun indigoDark() = darkColorScheme(
    primary = Color(0xFFB8C0FF), onPrimary = Color(0xFF0F1B7C),
    primaryContainer = Color(0xFF2A3697), onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = Color(0xFFFFB66E), onSecondary = Color(0xFF482900),
    secondaryContainer = Color(0xFF673D00), onSecondaryContainer = Color(0xFFFFE0B2),
    tertiary = Color(0xFFE0B0FF), onTertiary = Color(0xFF42006A),
    background = Color(0xFF12121A), onBackground = Color(0xFFE6E1E9),
    surface = Color(0xFF12121A), onSurface = Color(0xFFE6E1E9),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005)
)

// ─────── Forest ───────
private fun forestLight() = lightColorScheme(
    primary = Color(0xFF2E7D32), onPrimary = Color.White,
    primaryContainer = Color(0xFFB8E6C0), onPrimaryContainer = Color(0xFF002106),
    secondary = Color(0xFF8D6E63), onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFE3DC), onSecondaryContainer = Color(0xFF2A1B14),
    tertiary = Color(0xFFCDDC39), onTertiary = Color(0xFF1A2900),
    background = Color(0xFFF7FAF5), onBackground = Color(0xFF181D17),
    surface = Color(0xFFF7FAF5), onSurface = Color(0xFF181D17),
    error = Color(0xFFB00020), onError = Color.White
)

private fun forestDark() = darkColorScheme(
    primary = Color(0xFF8DD18F), onPrimary = Color(0xFF003912),
    primaryContainer = Color(0xFF155224), onPrimaryContainer = Color(0xFFC0EBC2),
    secondary = Color(0xFFCFB7AC), onSecondary = Color(0xFF422E27),
    secondaryContainer = Color(0xFF5B453D), onSecondaryContainer = Color(0xFFEFE3DC),
    tertiary = Color(0xFFC2D680), onTertiary = Color(0xFF2A3900),
    background = Color(0xFF0F1410), onBackground = Color(0xFFE2E4DE),
    surface = Color(0xFF0F1410), onSurface = Color(0xFFE2E4DE),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005)
)

// ─────── Coral ───────
private fun coralLight() = lightColorScheme(
    primary = Color(0xFFE91E63), onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E2), onPrimaryContainer = Color(0xFF3F0017),
    secondary = Color(0xFF00897B), onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB), onSecondaryContainer = Color(0xFF002021),
    tertiary = Color(0xFFFFB300), onTertiary = Color(0xFF3D2D00),
    background = Color(0xFFFFFBFA), onBackground = Color(0xFF1F1A1B),
    surface = Color(0xFFFFFBFA), onSurface = Color(0xFF1F1A1B),
    error = Color(0xFFB00020), onError = Color.White
)

private fun coralDark() = darkColorScheme(
    primary = Color(0xFFFFB1C8), onPrimary = Color(0xFF65002B),
    primaryContainer = Color(0xFF8E0040), onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = Color(0xFF82DDD3), onSecondary = Color(0xFF003733),
    secondaryContainer = Color(0xFF00504C), onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = Color(0xFFFFC862), onTertiary = Color(0xFF402D00),
    background = Color(0xFF161213), onBackground = Color(0xFFEDE0DF),
    surface = Color(0xFF161213), onSurface = Color(0xFFEDE0DF),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005)
)
