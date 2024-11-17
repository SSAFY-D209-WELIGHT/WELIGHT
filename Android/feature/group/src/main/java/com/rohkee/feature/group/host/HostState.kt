package com.rohkee.feature.group.host

import androidx.compose.runtime.Immutable
import com.rohkee.core.ui.component.storage.DisplayCardState
import kotlinx.collections.immutable.PersistentList

sealed interface HostState {
    @Immutable
    data class Creation(
        val title: String,
        val description: String,
        val list: PersistentList<DisplayCardState>,
        val dialogState: DialogState,
    ) : HostState

    @Immutable
    data class WaitingRoom(
        val title: String,
        val description: String,
        val list: PersistentList<DisplayCardState>,
        val clients: Int,
        val effect: DisplayEffect,
        val doDetect: Boolean,
        val dialogState: DialogState,
    ) : HostState
}
