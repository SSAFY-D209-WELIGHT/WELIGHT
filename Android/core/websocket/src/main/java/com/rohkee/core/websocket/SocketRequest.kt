package com.rohkee.core.websocket

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface SocketRequest {
    val roomId: Long

    fun name(): String

    @Serializable
    data class CreateRoom(
        @Transient
        override val roomId: Long = 0,
        val user: User,
        val title: String,
        val description: String,
        val location: Location,
        val displays: List<Display.Group>,
    ) : SocketRequest {
        override fun name(): String = "createRoom"
    }

    @Serializable
    data class JoinRoom(
        override val roomId: Long,
        val user: User,
    ) : SocketRequest {
        override fun name(): String = "joinRoom"
    }

    @Serializable
    data class ChangeRoomDisplay(
        override val roomId: Long,
        val displays: List<Display.Group>,
    ) : SocketRequest {
        override fun name(): String = "changeRoomDisplay"
    }

    @Serializable
    data class ChangeGroup(
        override val roomId: Long,
        val groupNumber: Int,
    ) : SocketRequest {
        override fun name(): String = "changeGroup"
    }

    @Serializable
    data class StartCheer(
        override val roomId: Long,
    ) : SocketRequest {
        override fun name(): String = "startCheering"
    }

    @Serializable
    data class EndCheer(
        override val roomId: Long,
    ) : SocketRequest {
        override fun name(): String = "endCheering"
    }

    @Serializable
    data class CloseRoom(
        override val roomId: Long,
    ) : SocketRequest {
        override fun name(): String = "closeRoom"
    }

    @Serializable
    data class LeaveRoom(
        override val roomId: Long,
    ) : SocketRequest {
        override fun name(): String = "leaveRoom"
    }

    @Serializable
    data class ControlDisplay(
        override val roomId: Long,
        val displays: List<Display.Control>,
    ) : SocketRequest {
        override fun name(): String = "controlDisplay"
    }
}

@Serializable
data class User(
    val token: String,
)
