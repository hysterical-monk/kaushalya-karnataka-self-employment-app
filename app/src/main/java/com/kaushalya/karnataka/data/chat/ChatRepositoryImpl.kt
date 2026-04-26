package com.kaushalya.karnataka.data.chat

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.kaushalya.karnataka.data.snapshotsFlow
import com.kaushalya.karnataka.domain.model.ChatMessage
import com.kaushalya.karnataka.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override fun observeMessages(chatId: String): Flow<List<ChatMessage>> =
        firestore.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("sentAt", Query.Direction.ASCENDING)
            .snapshotsFlow()
            .map { snap ->
                snap.documents.map { d ->
                    ChatMessage(
                        id = d.id,
                        senderId = d.getString("senderId").orEmpty(),
                        text = d.getString("text").orEmpty(),
                        sentAtMillis = d.getTimestamp("sentAt")?.toDate()?.time ?: 0L
                    )
                }
            }

    override suspend fun sendMessage(
        chatId: String,
        customerId: String,
        workerId: String,
        senderId: String,
        text: String
    ): Result<Unit> = runCatching {
        val parent = firestore.collection("chats").document(chatId)
        // Ensure parent doc exists with participant ids
        parent.set(
            mapOf(
                "customerId" to customerId,
                "workerId" to workerId,
                "lastMessage" to text,
                "lastMessageAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).await()
        parent.collection("messages").add(
            mapOf(
                "senderId" to senderId,
                "text" to text,
                "sentAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }
}
