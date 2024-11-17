package com.rohkee.feature.group.client

import androidx.compose.runtime.Immutable
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState

data class ClientData(
    val roomId: Long,
    val title: String = "",
    val description: String = "",
    val participants: Int = 0,
    val groupNumber: Int = 1,
    val displays: List<Long> = emptyList(),
    val imageState: DisplayImageState = DisplayImageState(),
    val textState: DisplayTextState = DisplayTextState(),
    val backgroundState: DisplayBackgroundState = DisplayBackgroundState(),
    val dialogState: ClientDialogState = ClientDialogState.Closed,
    val isCheering: Boolean = false,
) {
    fun toState(): ClientState =
        if (displays.isEmpty()) {
            ClientState.Loading
        } else {
            if(!isCheering) {
                ClientState.Loaded(
                    title = title,
                    description = description,
                    groupNumber = groupNumber,
                    displays = displays,
                    dialogState = dialogState,
                    imageState = imageState,
                    textState = textState,
                    backgroundState = backgroundState,
                )
            } else {
                ClientState.Cheering(
                    imageState = imageState,
                    textState = textState,
                    backgroundState = backgroundState
                )
            }
        }
}

@Immutable
sealed interface ClientDialogState {
    data object Closed : ClientDialogState

    data class StartCheer(
        val displayId: Long,
        val offset: Float,
        val interval: Float,
    ) : ClientDialogState
}
