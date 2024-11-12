package com.rohkee.feature.group.host

import com.rohkee.core.ui.component.storage.DisplayCardState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class HostData(
    val roomId: Long = 0,
    val title: String = "",
    val description: String = "",
    val list: PersistentList<DisplayCardState> = persistentListOf(),
    val effect: DisplayEffect = DisplayEffect.NONE,
    val doDetect: Boolean = false,
) {
    fun toState() = HostState.WaitingRoom(
        title = title,
        description = description,
        list = list,
        effect = effect,
        doDetect = doDetect,
    )
}