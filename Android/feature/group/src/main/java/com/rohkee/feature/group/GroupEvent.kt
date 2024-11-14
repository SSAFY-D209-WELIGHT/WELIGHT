package com.rohkee.feature.group

sealed interface GroupEvent {
    data object OpenRoomCreation : GroupEvent

    data class OpenClient(
        val roomId: Long,
    ) : GroupEvent
}
