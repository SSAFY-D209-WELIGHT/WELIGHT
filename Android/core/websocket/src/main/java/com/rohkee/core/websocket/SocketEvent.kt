package com.rohkee.core.websocket

sealed interface SocketEvent {
    enum class On(
        val event: String,
    ) : SocketEvent {
        ROOM_CREATE("roomCreate"),
        ROOM_JOIN("roomJoin"),
        ROOM_INFO_RECEIVE("roomInfoReceive"),
        ROOM_DISPLAY_CHANGE("roomDisplayChange"),
        GROUP_SELECT("groupSelect"),
        CHEER_START("cheerStart"),
        CHEER_END("cheerEnd"),
        DISPLAY_CONTROL("displayControl"),
        ERROR("error"),
    }

    enum class Emit(
        val event: String,
    ) : SocketEvent {
        CREATE_ROOM("createRoom"),
        JOIN_ROOM("joinRoom"),
        CHANGE_ROOM_DISPLAY("changeRoomDisplay"),
        SELECT_GROUP("selectGroup"),
        START_CHEER("startCheer"),
        END_CHEER("endCheer"),
        CLOSE_ROOM("closeRoom"),
        CONTROL_DISPLAY("controlDisplay"),
    }
}
