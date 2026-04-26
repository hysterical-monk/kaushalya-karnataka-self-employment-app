package com.kaushalya.karnataka.domain.repository

import com.kaushalya.karnataka.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(chatId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(chatId: String, customerId: String, workerId: String, senderId: String, text: String): Result<Unit>
}
