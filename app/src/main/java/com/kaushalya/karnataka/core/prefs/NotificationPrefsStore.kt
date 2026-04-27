package com.kaushalya.karnataka.core.prefs

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class NotificationPrefs(
    val hireRequests: Boolean = true,
    val newReviews: Boolean = true,
    val chatMessages: Boolean = true,
    val jobsForMe: Boolean = true
)

@Singleton
class NotificationPrefsStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(): NotificationPrefs = NotificationPrefs(
        hireRequests = prefs.getBoolean("hireRequests", true),
        newReviews = prefs.getBoolean("newReviews", true),
        chatMessages = prefs.getBoolean("chatMessages", true),
        jobsForMe = prefs.getBoolean("jobsForMe", true)
    )

    fun save(p: NotificationPrefs) {
        prefs.edit()
            .putBoolean("hireRequests", p.hireRequests)
            .putBoolean("newReviews", p.newReviews)
            .putBoolean("chatMessages", p.chatMessages)
            .putBoolean("jobsForMe", p.jobsForMe)
            .apply()
    }

    private companion object {
        const val PREFS = "notification_prefs"
    }
}
