package com.rohkee.feature.group.host

import androidx.compose.runtime.Immutable
import com.rohkee.core.ui.component.storage.DisplayCardState
import kotlinx.collections.immutable.PersistentList

sealed interface HostState {
    @Immutable
    data object Creation : HostState

    @Immutable
    data class WaitingRoom(
        val title: String,
        val description: String,
        val list: PersistentList<DisplayCardState>,
        val effect: DisplayEffect,
        val doDetect: Boolean,
    ) : HostState
}
