package com.kaushalya.karnataka.domain.model

data class JobPost(
    val id: String,
    val customerId: String,
    val customerName: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val town: String,
    val budgetMaxInr: Int?,
    val status: JobStatus,
    val createdAtMillis: Long
)

enum class JobStatus {
    OPEN, AWARDED, CANCELLED;

    companion object {
        fun fromString(s: String?): JobStatus = when (s) {
            "awarded" -> AWARDED
            "cancelled" -> CANCELLED
            else -> OPEN
        }
    }
    fun asString(): String = name.lowercase()
}
