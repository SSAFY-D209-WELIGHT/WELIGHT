package com.rohkee.feature.group.host

sealed interface HostEvent {
    data object ExitPage : HostEvent
    data object ChooseDisplayForNewGroup : HostEvent
    data class StartCheer(val roomId: Long) : HostEvent
}
