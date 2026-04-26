package com.kaushalya.karnataka.data.hire

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaushalya.karnataka.data.FirestorePaths
import com.kaushalya.karnataka.data.snapshotsFlow
import com.kaushalya.karnataka.domain.model.HireRequest
import com.kaushalya.karnataka.domain.model.HireStatus
import com.kaushalya.karnataka.domain.repository.HireRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HireRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HireRepository {

    private fun com.google.firebase.firestore.DocumentSnapshot.toHire(): HireRequest =
        HireRequest(
            id = id,
            customerId = getString("customerId").orEmpty(),
            customerName = getString("customerName").orEmpty(),
            workerId = getString("workerId").orEmpty(),
            serviceId = getString("serviceId"),
            serviceTitle = getString("serviceTitle"),
            message = getString("message").orEmpty(),
            status = HireStatus.fromString(getString("status")),
            createdAtMillis = getTimestamp("createdAt")?.toDate()?.time ?: 0L
        )

    override fun observeIncoming(workerId: String): Flow<List<HireRequest>> =
        firestore.collection(FirestorePaths.HIRE_REQUESTS)
            .whereEqualTo("workerId", workerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshotsFlow()
            .map { snap -> snap.documents.map { it.toHire() } }

    override fun observeOutgoing(customerId: String): Flow<List<HireRequest>> =
        firestore.collection(FirestorePaths.HIRE_REQUESTS)
            .whereEqualTo("customerId", customerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshotsFlow()
            .map { snap -> snap.documents.map { it.toHire() } }

    override suspend fun sendHireRequest(request: HireRequest): Result<String> = runCatching {
        val data = mapOf(
            "customerId" to request.customerId,
            "customerName" to request.customerName,
            "workerId" to request.workerId,
            "serviceId" to request.serviceId,
            "serviceTitle" to request.serviceTitle,
            "message" to request.message,
            "status" to HireStatus.PENDING.asString(),
            "createdAt" to FieldValue.serverTimestamp()
        )
        val ref = firestore.collection(FirestorePaths.HIRE_REQUESTS).add(data).await()
        ref.id
    }

    override suspend fun updateStatus(requestId: String, status: HireStatus): Result<Unit> = runCatching {
        firestore.collection(FirestorePaths.HIRE_REQUESTS).document(requestId)
            .update("status", status.asString()).await()
    }
}
