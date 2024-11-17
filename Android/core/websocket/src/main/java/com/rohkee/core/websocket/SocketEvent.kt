package com.rohkee.core.websocket

sealed interface SocketEvent {
    enum class On(
        val event: String,
    ) : SocketEvent {
        ROOM_CREATE("roomCreate"),
        ROOM_JOIN("roomJoin"),
        ROOM_INFO_RECEIVE("roomInfo"),
        ROOM_DISPLAY_CHANGE("roomDisplayChange"),
        GROUP_SELECT("groupChange"),
        CHEER_START("cheeringStart"),
        CHEER_END("cheeringEnd"),
        DISPLAY_CONTROL("displayControl"),
        ROOM_CLOSE("roomClose"),
        ERROR("error"),
    }
}
