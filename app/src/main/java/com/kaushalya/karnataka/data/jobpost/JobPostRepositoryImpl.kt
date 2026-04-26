package com.kaushalya.karnataka.data.jobpost

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaushalya.karnataka.data.snapshotsFlow
import com.kaushalya.karnataka.domain.model.JobPost
import com.kaushalya.karnataka.domain.model.JobStatus
import com.kaushalya.karnataka.domain.repository.JobPostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobPostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : JobPostRepository {

    private fun com.google.firebase.firestore.DocumentSnapshot.toJobPost(): JobPost = JobPost(
        id = id,
        customerId = getString("customerId").orEmpty(),
        customerName = getString("customerName").orEmpty(),
        title = getString("title").orEmpty(),
        description = getString("description").orEmpty(),
        categoryId = getString("categoryId").orEmpty(),
        town = getString("town").orEmpty(),
        budgetMaxInr = getLong("budgetMaxInr")?.toInt(),
        status = JobStatus.fromString(getString("status")),
        createdAtMillis = getTimestamp("createdAt")?.toDate()?.time ?: 0L
    )

    override fun observeOpenJobs(town: String?, categoryIds: List<String>): Flow<List<JobPost>> {
        var q: Query = firestore.collection("job_posts").whereEqualTo("status", "open")
        if (!town.isNullOrBlank()) q = q.whereEqualTo("town", town)
        return q.orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshotsFlow()
            .map { snap ->
                val list = snap.documents.map { it.toJobPost() }
                if (categoryIds.isEmpty()) list
                else list.filter { it.categoryId in categoryIds }
            }
            .catch { emit(emptyList()) }   // index missing or rules denied — show empty UI
    }

    override fun observeMyJobs(customerId: String): Flow<List<JobPost>> =
        firestore.collection("job_posts")
            .whereEqualTo("customerId", customerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshotsFlow()
            .map { snap -> snap.documents.map { it.toJobPost() } }
            .catch { emit(emptyList()) }   // index missing or rules denied — show empty UI

    override suspend fun postJob(post: JobPost): Result<String> = runCatching {
        val data = mapOf(
            "customerId" to post.customerId,
            "customerName" to post.customerName,
            "title" to post.title,
            "description" to post.description,
            "categoryId" to post.categoryId,
            "town" to post.town,
            "budgetMaxInr" to post.budgetMaxInr,
            "status" to JobStatus.OPEN.asString(),
            "createdAt" to FieldValue.serverTimestamp()
        )
        val ref = firestore.collection("job_posts").add(data).await()
        ref.id
    }

    override suspend fun closeJob(jobId: String): Result<Unit> = runCatching {
        firestore.collection("job_posts").document(jobId)
            .update("status", JobStatus.CANCELLED.asString()).await()
    }
}
