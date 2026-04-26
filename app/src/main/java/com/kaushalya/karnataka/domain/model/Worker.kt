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
    val thumbsUpCount: Int
)
