package com.rohkee.feature.group.host

sealed interface HostIntent {
    sealed interface Permission : HostIntent {
        data object Granted : Permission
        data object Rejected : Permission
    }

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

        data class ChangeInterval(
            val interval: Float,
        ) : Control

        data class ToggleDetect(
            val doDetect: Boolean,
        ) : Control

        data object StartCheer : Control
    }

    sealed interface ExitDialog : HostIntent {
        data object Cancel : ExitDialog
        data object Exit : ExitDialog
    }

    sealed interface SelectionDialog : HostIntent {
        data object Cancel : SelectionDialog

        data class SelectDisplay(
            val displayId: Long,
            val thumbnailUrl: String,
        ) : SelectionDialog
    }

    sealed interface CheerDialog : HostIntent {
        data object Cancel : CheerDialog
    }
}
