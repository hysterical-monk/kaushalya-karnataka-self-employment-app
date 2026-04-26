package com.kaushalya.karnataka.domain.model

data class PortfolioPhoto(
    val id: String,
    val workerId: String,
    val imageUrl: String,
    val caption: String,
    val uploadedAtMillis: Long
)
