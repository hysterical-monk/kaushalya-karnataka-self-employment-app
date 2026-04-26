package com.kaushalya.karnataka.core.prefs

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the customer's selected town. `null` means "all of Karnataka".
 */
@Singleton
class LocationStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun selectedTown(): String? = prefs.getString(KEY_TOWN, null)

    fun setTown(town: String?) {
        prefs.edit().apply {
            if (town.isNullOrBlank()) remove(KEY_TOWN) else putString(KEY_TOWN, town)
            apply()
        }
    }

    fun observeTown(): Flow<String?> = callbackFlow {
        trySend(selectedTown())
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_TOWN) trySend(selectedTown())
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private companion object {
        const val PREFS = "location_prefs"
        const val KEY_TOWN = "town"
    }
}

object KarnatakaTowns {
    val ALL = listOf(
        "Bengaluru",
        "Mysuru",
        "Mangalore",
        "Hubballi",
        "Belagavi",
        "Ballari",
        "Tumakuru",
        "Davangere",
        "Shivamogga"
    )
}
