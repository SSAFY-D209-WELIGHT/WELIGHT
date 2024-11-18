enum class MessageType {
    SYSTEM,
    CHAT
}

data class Message(
    val content: String,
    val type: MessageType,
    val timestamp: Long = System.currentTimeMillis()
) 