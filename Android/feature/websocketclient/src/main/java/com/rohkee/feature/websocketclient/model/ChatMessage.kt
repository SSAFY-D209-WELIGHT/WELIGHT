data class ChatMessage(
    val socketId: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
) 