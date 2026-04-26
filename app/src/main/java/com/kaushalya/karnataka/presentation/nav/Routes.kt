package com.kaushalya.karnataka.presentation.nav

object Routes {
    const val ONBOARDING = "onboarding/intro"
    const val PHONE_ENTRY = "auth/phone"
    const val OTP = "auth/otp/{verificationId}/{phone}"
    const val ROLE_SELECT = "onboarding/role"

    const val CUSTOMER_SHELL = "customer/shell"
    const val CUSTOMER_BROWSE = "customer/browse"          // legacy, kept for back-compat
    const val CUSTOMER_BOOKMARKS = "customer/bookmarks"    // legacy, kept for back-compat
    const val WORKER_DETAIL = "customer/worker/{workerId}"

    const val WORKER_DASHBOARD = "worker/dashboard"
    const val WORKER_PROFILE = "worker/profile"
    const val WORKER_SERVICES = "worker/services"
    const val WORKER_PORTFOLIO = "worker/portfolio"

    const val SETTINGS = "settings"

    fun otp(verificationId: String, phone: String) = "auth/otp/${verificationId}/${phone}"
    fun workerDetail(workerId: String) = "customer/worker/$workerId"
}
