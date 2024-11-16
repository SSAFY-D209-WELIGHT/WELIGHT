package com.rohkee.feature.group.host

sealed interface HostIntent {
    sealed interface Creation : HostIntent {
        data class UpdateTitle(
            val title: String,
        ) : Creation

        data class UpdateDescription(
            val description: String,
        ) : Creation

        data class CreateRoom(
            val latitude: Double,
            val longitude: Double,
        ) : Creation

        data object AddDisplay : Creation

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
