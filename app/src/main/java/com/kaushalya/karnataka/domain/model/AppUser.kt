package com.kaushalya.karnataka.domain.model

enum class UserRole { WORKER, CUSTOMER, UNKNOWN;
    companion object {
        fun fromString(s: String?): UserRole = when (s) {
            "worker" -> WORKER
            "customer" -> CUSTOMER
            else -> UNKNOWN
        }
    }
    fun asString(): String = when (this) {
        WORKER -> "worker"
        CUSTOMER -> "customer"
        UNKNOWN -> "unknown"
    }
}

data class AppUser(
    val uid: String,
    val phone: String,
    val displayName: String,
    val role: UserRole,
    val language: String
)
