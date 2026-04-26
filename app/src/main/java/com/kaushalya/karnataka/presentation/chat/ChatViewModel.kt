package com.kaushalya.karnataka.presentation.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaushalya.karnataka.domain.model.Chat
import com.kaushalya.karnataka.domain.model.ChatMessage
import com.kaushalya.karnataka.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val sending: Boolean = false,
    val title: String = ""
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val customerId: String = savedStateHandle.get<String>("customerId").orEmpty()
    private val workerId: String = savedStateHandle.get<String>("workerId").orEmpty()
    private val title: String = savedStateHandle.get<String>("title").orEmpty()
    private val chatId: String = Chat.chatId(customerId, workerId)

    val currentUid: String = auth.currentUser?.uid.orEmpty()

    val state: StateFlow<ChatState> = chatRepository.observeMessages(chatId)
        .map { ChatState(messages = it, title = title) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ChatState(title = title))

    fun send(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            chatRepository.sendMessage(
                chatId = chatId,
                customerId = customerId,
                workerId = workerId,
                senderId = currentUid,
                text = text.trim()
            )
        }
    }
}
