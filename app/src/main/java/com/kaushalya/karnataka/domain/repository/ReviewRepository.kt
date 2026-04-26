package com.kaushalya.karnataka.domain.repository

import com.kaushalya.karnataka.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun observeReviews(workerId: String): Flow<List<Review>>
    suspend fun postReview(review: Review): Result<Unit>
    suspend fun toggleThumbsUp(workerId: String, reviewId: String, thumbsUp: Boolean): Result<Unit>
}
