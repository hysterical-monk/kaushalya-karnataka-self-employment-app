package com.kaushalya.karnataka.core.analytics

sealed class AnalyticsEvent(val name: String, val params: Map<String, Any?> = emptyMap()) {
    data object SignInStarted : AnalyticsEvent("sign_in_started")
    data object SignInCompleted : AnalyticsEvent("sign_in_completed")
    data class RoleSelected(val role: String) : AnalyticsEvent("role_selected", mapOf("role" to role))
    data class WorkerProfileSaved(val categoryCount: Int) :
        AnalyticsEvent("worker_profile_saved", mapOf("category_count" to categoryCount))
    data class ServiceCardSaved(val priceType: String, val priceInr: Int) :
        AnalyticsEvent("service_card_saved", mapOf("price_type" to priceType, "price_inr" to priceInr))
    data object PortfolioPhotoUploaded : AnalyticsEvent("portfolio_photo_uploaded")
    data class HireRequestSent(val hasService: Boolean) :
        AnalyticsEvent("hire_request_sent", mapOf("has_service" to hasService))
    data class ReviewPosted(val stars: Int) :
        AnalyticsEvent("review_posted", mapOf("stars" to stars))
    data class BookmarkToggled(val added: Boolean) :
        AnalyticsEvent("bookmark_toggled", mapOf("added" to added))
    data class AvailabilityToggled(val available: Boolean) :
        AnalyticsEvent("availability_toggled", mapOf("available" to available))
    data class LanguageSwitched(val language: String) :
        AnalyticsEvent("language_switched", mapOf("language" to language))
    data class CategoryFiltered(val categoryId: String?) :
        AnalyticsEvent("category_filtered", mapOf("category_id" to (categoryId ?: "all")))
}
