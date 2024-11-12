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
            val effect: DisplayEffect,
        ) : Control

        data object StartCheer : Control
    }

    sealed interface Dialog : HostIntent {
        data object Cancel : Dialog

        data class SelectDisplay(
            val displayId: Long,
            val thumbnailUrl: String,
        ) : Dialog
    }
}
