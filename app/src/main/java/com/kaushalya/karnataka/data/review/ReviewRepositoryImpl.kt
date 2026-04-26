package com.kaushalya.karnataka.data.review

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaushalya.karnataka.data.FirestorePaths
import com.kaushalya.karnataka.data.snapshotsFlow
import com.kaushalya.karnataka.domain.model.Review
import com.kaushalya.karnataka.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewRepository {

    override fun observeReviews(workerId: String): Flow<List<Review>> =
        firestore.collection(FirestorePaths.WORKERS).document(workerId)
            .collection(FirestorePaths.REVIEWS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshotsFlow()
            .map { snap ->
                snap.documents.map { d ->
                    Review(
                        id = d.id,
                        workerId = workerId,
                        customerId = d.getString("customerId").orEmpty(),
                        customerName = d.getString("customerName").orEmpty(),
                        stars = (d.getLong("stars") ?: 0L).toInt(),
                        thumbsUp = d.getBoolean("thumbsUp") ?: false,
                        text = d.getString("text").orEmpty(),
                        createdAtMillis = d.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                    )
                }
            }

    override suspend fun postReview(review: Review): Result<Unit> = runCatching {
        firestore.collection(FirestorePaths.WORKERS).document(review.workerId)
            .collection(FirestorePaths.REVIEWS).document(review.customerId).set(
                mapOf(
                    "customerId" to review.customerId,
                    "customerName" to review.customerName,
                    "stars" to review.stars,
                    "thumbsUp" to review.thumbsUp,
                    "text" to review.text,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
    }

    override suspend fun toggleThumbsUp(workerId: String, reviewId: String, thumbsUp: Boolean): Result<Unit> = runCatching {
        firestore.collection(FirestorePaths.WORKERS).document(workerId)
            .collection(FirestorePaths.REVIEWS).document(reviewId)
            .update("thumbsUp", thumbsUp).await()
    }
}
