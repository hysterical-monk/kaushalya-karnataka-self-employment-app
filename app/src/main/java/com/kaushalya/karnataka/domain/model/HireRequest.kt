package com.kaushalya.karnataka.domain.model

data class HireRequest(
    val id: String,
    val customerId: String,
    val customerName: String,
    val workerId: String,
    val serviceId: String?,
    val serviceTitle: String?,
    val message: String,
    val status: HireStatus,
    val createdAtMillis: Long
)

enum class HireStatus {
    PENDING, SEEN, COMPLETED, CANCELLED;

    companion object {
        fun fromString(s: String?): HireStatus = when (s) {
            "seen" -> SEEN
            "completed" -> COMPLETED
            "cancelled" -> CANCELLED
            else -> PENDING
        }
    }

    fun asString(): String = name.lowercase()
}
