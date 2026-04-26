package com.kaushalya.karnataka.domain.model

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val sentAtMillis: Long
)

object Chat {
    /** Deterministic chat id from a customer/worker uid pair. */
    fun chatId(a: String, b: String): String {
        val (lo, hi) = if (a < b) a to b else b to a
        return "${lo}__${hi}"
    }
}
