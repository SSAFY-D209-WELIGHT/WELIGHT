package com.rohkee.feature.group.host

sealed interface HostIntent {
    sealed interface Creation : HostIntent {
        data class Confirm(
            val title: String,
            val description: String,
        ) : Creation
        data object Cancel : Creation
    }

    sealed interface Control : HostIntent {
        data object Exit : Control
        data object AddDisplayGroup : Control
        data class ChangeEffect(
            val effect: Int, // TODO change to enum?
        ) : Control
        data object StartCheer : Control
    }
}
