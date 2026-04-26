package com.kaushalya.karnataka.domain.model

data class Worker(
    val id: String,
    val displayName: String,
    val phone: String,
    val bio: String,
    val photoUrl: String?,
    val town: String,
    val locality: String,
    val categories: List<String>,
    val isAvailable: Boolean,
    val averageRating: Float,
    val ratingCount: Int,
    val thumbsUpCount: Int,
    val minPriceInr: Int? = null,
    val tier: String = "free",                  // "free" | "pro"
    val openHour: Int? = null,                  // 0-23, null = always available
    val closeHour: Int? = null,                 // 0-23
    val workingDays: List<Int> = emptyList()    // 1=Mon..7=Sun; empty = every day
) {
    val isPro: Boolean get() = tier == "pro"

    /** Returns true if the worker's listed working hours include the given clock time. */
    fun isOpenAt(now: java.util.Calendar): Boolean {
        val day = ((now.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7) + 1 // 1=Mon..7=Sun
        if (workingDays.isNotEmpty() && day !in workingDays) return false
        val o = openHour ?: return true
        val c = closeHour ?: return true
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        return if (o <= c) hour in o until c else hour >= o || hour < c
    }
}
