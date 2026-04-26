package com.kaushalya.karnataka.domain.model

data class Review(
    val id: String,
    val workerId: String,
    val customerId: String,
    val customerName: String,
    val stars: Int,
    val thumbsUp: Boolean,
    val text: String,
    val createdAtMillis: Long
)
