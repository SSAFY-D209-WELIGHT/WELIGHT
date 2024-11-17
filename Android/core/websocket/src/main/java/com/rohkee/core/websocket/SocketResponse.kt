package com.rohkee.core.websocket

import kotlinx.serialization.Serializable

@Serializable
sealed interface SocketResponse {
    @Serializable
    data class RoomCreate(
        val roomId: Long,
        val title: String,
        val description: String,
        val location: Location,
        val displays: List<Display.Group>,
        val address: String,
        val isOwner: Boolean,
        val clientCount: Int,
        val groupNumber: Int,
        val createdAt: Long,
    ) : SocketResponse

    @Serializable
    data class RoomJoin(
        val roomId: Long,
        val title: String,
        val description: String,
        val location: Location,
        val address: String,
        val isOwner: Boolean,
        val clientCount: Int,
        val groupNumber: Int,
        val createdAt: Long,
        val displays: List<Display.Group>
    ) : SocketResponse

    @Serializable
    data class RoomInfo(
        val roomId: Long,
        val title: String,
        val description: String,
        val clientCount: Int,
    ) : SocketResponse

    @Serializable
    data class RoomDisplayChange(
        val roomId: Long,
        val displays: List<Display.Group>,
    ) : SocketResponse

    @Serializable
    data class GroupChange(
        val groupNumber: Int,
    ) : SocketResponse

    @Serializable
    data class CheerStart(
        val roomId: Long
    ) : SocketResponse

    @Serializable
    data class CheerEnd(
        val roomId: Long
    ) : SocketResponse

    @Serializable
    data class RoomClose(
        val roomId: Long,
    ) : SocketResponse

    @Serializable
    data class DisplayControl(
        val roomId: Long,
        val groupNumber: Int,
        val displayId: Long,
        val offset: Float,
        val interval: Float,
    ) : SocketResponse

    @Serializable
    data object Error : SocketResponse
}

sealed interface Display {
    @Serializable
    data class Group(
        val displayId: Long,
    ) : Display

    @Serializable
    data class Control(
        val displayId: Long,
        val offset: Float,
        val interval: Float,
    )
}

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
)
