package com.rohkee.feature.group.client

sealed interface ClientEvent{
    data object ExitPage : ClientEvent
}