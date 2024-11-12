package com.rohkee.feature.group.host

import com.rohkee.core.ui.component.storage.DisplayCardState
import kotlinx.collections.immutable.toPersistentList

data class HostData(
    val roomId: Long = 0,
    val title: String = "",
    val description: String = "",
    val list: List<GroupDisplayData> = emptyList(),
    val effect: DisplayEffect = DisplayEffect.NONE,
    val doDetect: Boolean = false,
) {
    fun toState() =
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
        )
}

data class GroupDisplayData(
    val displayId: Long,
    val thumbnailUrl: String,
)
