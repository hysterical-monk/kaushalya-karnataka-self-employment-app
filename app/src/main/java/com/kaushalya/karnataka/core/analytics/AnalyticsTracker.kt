package com.kaushalya.karnataka.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

interface AnalyticsTracker {
    fun log(event: AnalyticsEvent)
}

@Singleton
class FirebaseAnalyticsTracker @Inject constructor(
    private val analytics: FirebaseAnalytics
) : AnalyticsTracker {

    override fun log(event: AnalyticsEvent) {
        val bundle = Bundle().apply {
            event.params.forEach { (k, v) ->
                when (v) {
                    null -> { /* skip */ }
                    is String -> putString(k, v)
                    is Int -> putInt(k, v)
                    is Long -> putLong(k, v)
                    is Boolean -> putBoolean(k, v)
                    is Double -> putDouble(k, v)
                    is Float -> putFloat(k, v)
                    else -> putString(k, v.toString())
                }
            }
        }
        analytics.logEvent(event.name, bundle)
    }
}
