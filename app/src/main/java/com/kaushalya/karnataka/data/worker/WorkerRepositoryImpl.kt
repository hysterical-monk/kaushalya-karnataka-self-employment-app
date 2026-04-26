package com.kaushalya.karnataka.data.worker

import android.net.Uri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.kaushalya.karnataka.data.FirestorePaths
import com.kaushalya.karnataka.data.snapshotsFlow
import com.kaushalya.karnataka.domain.model.PortfolioPhoto
import com.kaushalya.karnataka.domain.model.PriceType
import com.kaushalya.karnataka.domain.model.ServiceCard
import com.kaushalya.karnataka.domain.model.Worker
import com.kaushalya.karnataka.domain.repository.WorkerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : WorkerRepository {

    private fun workersQuery(category: String?): Query {
        var q: Query = firestore.collection(FirestorePaths.WORKERS)
        if (!category.isNullOrBlank()) {
            q = q.whereArrayContains("categories", category)
        }
        return q.orderBy("averageRating", Query.Direction.DESCENDING)
    }

    override fun observeWorkers(category: String?): Flow<List<Worker>> =
        workersQuery(category).snapshotsFlow().map { snap ->
            snap.documents.mapNotNull { it.toWorker() }
        }

    override fun observeWorker(workerId: String): Flow<Worker?> =
        firestore.collection(FirestorePaths.WORKERS).document(workerId).snapshotsFlow().map { it.toWorker() }

    override fun observeServices(workerId: String): Flow<List<ServiceCard>> =
        firestore.collection(FirestorePaths.WORKERS).document(workerId)
            .collection(FirestorePaths.SERVICES)
            .snapshotsFlow()
            .map { snap ->
                snap.documents.map { d ->
                    ServiceCard(
                        id = d.id,
                        workerId = workerId,
                        title = d.getString("title").orEmpty(),
                        description = d.getString("description").orEmpty(),
                        priceType = PriceType.fromString(d.getString("priceType")),
                        priceInr = (d.getLong("priceInr") ?: 0L).toInt(),
                        active = d.getBoolean("active") ?: true
                    )
                }
            }

    override fun observePortfolio(workerId: String): Flow<List<PortfolioPhoto>> =
        firestore.collection(FirestorePaths.WORKERS).document(workerId)
            .collection(FirestorePaths.PORTFOLIO)
            .orderBy("uploadedAt", Query.Direction.DESCENDING)
            .snapshotsFlow()
            .map { snap ->
                snap.documents.map { d ->
                    PortfolioPhoto(
                        id = d.id,
                        workerId = workerId,
                        imageUrl = d.getString("imageUrl").orEmpty(),
                        caption = d.getString("caption").orEmpty(),
                        uploadedAtMillis = d.getTimestamp("uploadedAt")?.toDate()?.time ?: 0L
                    )
                }
            }

    override suspend fun upsertWorkerProfile(worker: Worker): Result<Unit> = runCatching {
        val data = mapOf(
            "displayName" to worker.displayName,
            "phone" to worker.phone,
            "bio" to worker.bio,
            "photoUrl" to worker.photoUrl,
            "town" to worker.town,
            "locality" to worker.locality,
            "categories" to worker.categories,
            "availability" to if (worker.isAvailable) "available" else "busy",
            "updatedAt" to FieldValue.serverTimestamp()
        )
        firestore.collection(FirestorePaths.WORKERS).document(worker.id)
            .set(data, SetOptions.merge()).await()
    }

    override suspend fun setAvailability(workerId: String, available: Boolean): Result<Unit> = runCatching {
        firestore.collection(FirestorePaths.WORKERS).document(workerId)
            .update("availability", if (available) "available" else "busy").await()
    }

    override suspend fun upsertService(service: ServiceCard): Result<Unit> = runCatching {
        val coll = firestore.collection(FirestorePaths.WORKERS).document(service.workerId)
            .collection(FirestorePaths.SERVICES)
        val data = mapOf(
            "title" to service.title,
            "description" to service.description,
            "priceType" to service.priceType.asString(),
            "priceInr" to service.priceInr,
            "active" to service.active,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (service.id.isBlank()) coll.add(data).await() else coll.document(service.id).set(data, SetOptions.merge()).await()
    }

    override suspend fun deleteService(workerId: String, serviceId: String): Result<Unit> = runCatching {
        firestore.collection(FirestorePaths.WORKERS).document(workerId)
            .collection(FirestorePaths.SERVICES).document(serviceId).delete().await()
    }

    override suspend fun addPortfolioPhoto(workerId: String, localImageUri: String, caption: String): Result<Unit> = runCatching {
        val photoId = UUID.randomUUID().toString()
        val ref = storage.reference.child("portfolio/$workerId/$photoId.jpg")
        ref.putFile(Uri.parse(localImageUri)).await()
        val url = ref.downloadUrl.await().toString()
        firestore.collection(FirestorePaths.WORKERS).document(workerId)
            .collection(FirestorePaths.PORTFOLIO).document(photoId).set(
                mapOf(
                    "imageUrl" to url,
                    "caption" to caption,
                    "uploadedAt" to FieldValue.serverTimestamp()
                )
            ).await()
    }

    override suspend fun deletePortfolioPhoto(workerId: String, photoId: String): Result<Unit> = runCatching {
        firestore.collection(FirestorePaths.WORKERS).document(workerId)
            .collection(FirestorePaths.PORTFOLIO).document(photoId).delete().await()
        runCatching { storage.reference.child("portfolio/$workerId/$photoId.jpg").delete().await() }
    }
}

internal fun com.google.firebase.firestore.DocumentSnapshot.toWorker(): Worker? {
    if (!exists()) return null
    return Worker(
        id = id,
        displayName = getString("displayName").orEmpty(),
        phone = getString("phone").orEmpty(),
        bio = getString("bio").orEmpty(),
        photoUrl = getString("photoUrl"),
        town = getString("town").orEmpty(),
        locality = getString("locality").orEmpty(),
        categories = (get("categories") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        isAvailable = getString("availability") == "available",
        averageRating = (getDouble("averageRating") ?: 0.0).toFloat(),
        ratingCount = (getLong("ratingCount") ?: 0L).toInt(),
        thumbsUpCount = (getLong("thumbsUpCount") ?: 0L).toInt()
    )
}
