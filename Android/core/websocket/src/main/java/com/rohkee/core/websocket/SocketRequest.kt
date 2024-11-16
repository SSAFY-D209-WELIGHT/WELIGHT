package com.rohkee.core.websocket

import kotlinx.serialization.Serializable

@Serializable
sealed interface SocketRequest {
    fun name(): String

    @Serializable
    data class CreateRoom(
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
        val roomId: Long,
        val user: User,
    ) : SocketRequest {
        override fun name(): String = "joinRoom"
    }

    @Serializable
    data class ChangeRoomDisplay(
        val displays: List<Display.Group>,
    ) : SocketRequest {
        override fun name(): String = "changeRoomDisplay"
    }

    @Serializable
    data class SelectGroup(
        val groupNumber: Int,
    ) : SocketRequest {
        override fun name(): String = "selectGroup"
    }

    @Serializable
    data object StartCheer : SocketRequest {
        override fun name(): String = "startCheer"
    }

    @Serializable
    data object EndCheer : SocketRequest {
        override fun name(): String = "endCheer"
    }

    @Serializable
    data object CloseRoom : SocketRequest {
        override fun name(): String = "closeRoom"
    }

    @Serializable
    data class ControlDisplay(
        val displays: List<Display.Control>,
    ) : SocketRequest {
        override fun name(): String = "controlDisplay"
    }
}

@Serializable
data class User(
    val id: Long,
)
