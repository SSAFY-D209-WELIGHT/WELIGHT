data class Room(
    val roomId: String,
    val clientCount: Int,
    val location: Location,
    val address: String,
    val description: String = ""
) 