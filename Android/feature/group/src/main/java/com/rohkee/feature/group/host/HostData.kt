package com.rohkee.feature.group.host

import com.rohkee.core.ui.component.storage.DisplayCardState
import kotlinx.collections.immutable.toPersistentList

data class HostData(
    val roomId: Long = 0,
    val hasPermission: Boolean = false,
    val title: String = "",
    val description: String = "",
    val clients: Int = 0,
    val list: List<GroupDisplayData> = emptyList(),
    val effect: DisplayEffect = DisplayEffect.NONE,
    val interval: Float = 1.0f,
    val doDetect: Boolean = false,
    val hostDialogState: HostDialogState = HostDialogState.Closed,
) {
    fun toState() =
        if (roomId > 0) {
            HostState.WaitingRoom(
                title = title,
                description = description,
                list =
                    list
                        .map {
                            DisplayCardState(
                                cardId = it.displayId,
                                imageSource = it.thumbnailUrl,
                                selected = false,
                            )
                        }.toPersistentList(),
                clients = clients,
                effect = effect,
                doDetect = doDetect,
                interval = interval,
                hostDialogState = hostDialogState,
            )
        } else {
            HostState.Creation(
                title = title,
                description = description,
                list =
                    list
                        .map {
                            DisplayCardState(
                                cardId = it.displayId,
                                imageSource = it.thumbnailUrl,
                                selected = false,
                            )
                        }.toPersistentList(),
                hostDialogState = hostDialogState,
            )
        }
}

data class GroupDisplayData(
    val displayId: Long,
    val thumbnailUrl: String,
)

sealed interface HostDialogState {
    data object Closed : HostDialogState

    data object SelectDisplay : HostDialogState

    data class StartCheer(
        val displayId: Long,
        val offset: Float,
        val interval: Float,
    ) : HostDialogState

    data object Loading : HostDialogState
}
