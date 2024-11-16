package com.rohkee.core.websocket

import kotlinx.serialization.Serializable

sealed interface SocketRequest{
    @Serializable
    data class CreateRoom(
        val title: Long,
        val description: String,
        val location: Location,
        val displays: List<Display.Group>,
    )

    @Serializable
    data class JoinRoom(
        val roomId: Long,
        val user: User,
    )

    @Serializable
    data class ChangeRoomDisplay(
        val displays: List<Display.Group>
    )
}

@Serializable
data class User(
    val id: Long
)