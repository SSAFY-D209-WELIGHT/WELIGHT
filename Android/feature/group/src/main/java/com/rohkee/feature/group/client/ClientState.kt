package com.rohkee.feature.group.client

import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState

sealed interface ClientState {
    data object Loading : ClientState

    data class Loaded(
        val title: String,
        val description: String,
        val groupNumber: Int,
        val displays: List<Long>,
        val imageState: DisplayImageState,
        val textState: DisplayTextState,
        val backgroundState: DisplayBackgroundState,
        val dialogState: ClientDialogState,
    ) : ClientState

    data class Cheering(
        val imageState: DisplayImageState,
        val textState: DisplayTextState,
        val backgroundState: DisplayBackgroundState,
    ) : ClientState
}
