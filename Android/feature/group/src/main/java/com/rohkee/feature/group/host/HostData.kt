package com.rohkee.feature.group.host

import com.rohkee.core.ui.component.storage.DisplayCardState
import kotlinx.collections.immutable.toPersistentList

data class HostData(
    val roomId: Long = 0,
    val title: String = "",
    val description: String = "",
    val clients: Int = 0,
    val list: List<GroupDisplayData> = emptyList(),
    val effect: DisplayEffect = DisplayEffect.NONE,
    val doDetect: Boolean = false,
    val dialogState: DialogState = DialogState.Closed,
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
                effect = effect,
                doDetect = doDetect,
                dialogState = dialogState,
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
                dialogState = dialogState,
            )
        }
}

data class GroupDisplayData(
    val displayId: Long,
    val thumbnailUrl: String,
)

sealed interface DialogState {
    data object Closed : DialogState

    data object SelectDisplay : DialogState
}
