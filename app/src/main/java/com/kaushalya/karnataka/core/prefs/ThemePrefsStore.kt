package com.kaushalya.karnataka.core.prefs

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode { SYSTEM, LIGHT, DARK }

enum class ColorPalette(val displayName: String) {
    SAFFRON("Karnataka Saffron"),
    INDIGO("Indigo Mango"),
    FOREST("Forest"),
    CORAL("Coral");

    companion object {
        fun fromString(s: String?): ColorPalette = entries.firstOrNull { it.name == s } ?: SAFFRON
    }
}

data class ThemePrefs(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val palette: ColorPalette = ColorPalette.SAFFRON,
    val dynamic: Boolean = false
)

@Singleton
class ThemePrefsStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(): ThemePrefs = ThemePrefs(
        mode = runCatching { ThemeMode.valueOf(prefs.getString(KEY_MODE, "SYSTEM")!!) }.getOrDefault(ThemeMode.SYSTEM),
        palette = ColorPalette.fromString(prefs.getString(KEY_PALETTE, "SAFFRON")),
        dynamic = prefs.getBoolean(KEY_DYNAMIC, false)
    )

    fun save(p: ThemePrefs) {
        prefs.edit()
            .putString(KEY_MODE, p.mode.name)
            .putString(KEY_PALETTE, p.palette.name)
            .putBoolean(KEY_DYNAMIC, p.dynamic)
            .apply()
    }

    fun observe(): Flow<ThemePrefs> = callbackFlow {
        trySend(load())
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> trySend(load()) }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private companion object {
        const val PREFS = "theme_prefs"
        const val KEY_MODE = "mode"
        const val KEY_PALETTE = "palette"
        const val KEY_DYNAMIC = "dynamic"
    }
}
