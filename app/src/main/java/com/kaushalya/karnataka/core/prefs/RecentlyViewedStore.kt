package com.kaushalya.karnataka.core.prefs

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentlyViewedStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun ids(): List<String> =
        prefs.getString(KEY, "").orEmpty()
            .split(",")
            .filter { it.isNotBlank() }

    fun observe(): Flow<List<String>> = callbackFlow {
        trySend(ids())
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY) trySend(ids())
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    fun push(workerId: String) {
        if (workerId.isBlank()) return
        val current = ids().filter { it != workerId }
        val updated = (listOf(workerId) + current).take(10)
        prefs.edit().putString(KEY, updated.joinToString(",")).apply()
    }

    private companion object {
        const val PREFS = "recently_viewed_prefs"
        const val KEY = "ids"
    }
}
