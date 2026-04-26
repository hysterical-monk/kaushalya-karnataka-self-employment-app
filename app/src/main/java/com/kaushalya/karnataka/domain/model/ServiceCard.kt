package com.kaushalya.karnataka.domain.model

data class ServiceCard(
    val id: String,
    val workerId: String,
    val title: String,
    val description: String,
    val priceType: PriceType,
    val priceInr: Int,
    val active: Boolean
)

enum class PriceType { FIXED, STARTING_AT;
    companion object {
        fun fromString(s: String?): PriceType = when (s) {
            "starting_at" -> STARTING_AT
            else -> FIXED
        }
    }
    fun asString(): String = when (this) {
        FIXED -> "fixed"
        STARTING_AT -> "starting_at"
    }
}
