package com.kaushalya.karnataka.data.report

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kaushalya.karnataka.domain.repository.ReportRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReportRepository {

    override suspend fun report(
        reporterId: String,
        targetType: String,
        targetId: String,
        reason: String
    ): Result<Unit> = runCatching {
        firestore.collection("reports").add(
            mapOf(
                "reporterId" to reporterId,
                "targetType" to targetType,
                "targetId" to targetId,
                "reason" to reason,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }
}
