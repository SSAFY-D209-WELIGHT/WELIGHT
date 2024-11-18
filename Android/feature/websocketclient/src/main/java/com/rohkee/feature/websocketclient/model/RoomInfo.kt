data class RoomInfo(
    val location: Location,
    val address: String,
    val groupNumber: Int,
    val clientNumber: Int,
    val isOwner: Boolean
)

data class Client(
    val socketId: String,
    val groupNumber: Int,
    val clientNumber: Int,
    val isOwner: Boolean
)

data class RoomUpdate(
    val clients: List<Client>
) 