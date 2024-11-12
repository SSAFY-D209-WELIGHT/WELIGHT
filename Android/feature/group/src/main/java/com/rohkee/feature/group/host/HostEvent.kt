package com.rohkee.feature.group.host

sealed interface HostEvent {
    data object ExitPage : HostEvent
}
