package com.kaushalya.karnataka.data.bookmark

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kaushalya.karnataka.data.FirestorePaths
import com.kaushalya.karnataka.data.snapshotsFlow
import com.kaushalya.karnataka.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BookmarkRepository {

    override fun observeBookmarkedIds(customerId: String): Flow<Set<String>> =
        firestore.collection(FirestorePaths.BOOKMARKS).document(customerId)
            .collection(FirestorePaths.BOOKMARK_WORKERS)
            .snapshotsFlow()
            .map { snap -> snap.documents.map { it.id }.toSet() }

    override suspend fun toggle(customerId: String, workerId: String, currentlyBookmarked: Boolean): Result<Unit> = runCatching {
        val ref = firestore.collection(FirestorePaths.BOOKMARKS).document(customerId)
            .collection(FirestorePaths.BOOKMARK_WORKERS).document(workerId)
        if (currentlyBookmarked) ref.delete().await()
        else ref.set(mapOf("bookmarkedAt" to FieldValue.serverTimestamp())).await()
    }
}
