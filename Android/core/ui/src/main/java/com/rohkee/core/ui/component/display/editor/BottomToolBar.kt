package com.rohkee.core.ui.component.display.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

@Immutable
sealed interface BottomToolBarState {
    data class Info(
        val infoState: DisplayInfoState,
    ) : BottomToolBarState
}

@Composable
fun BottomToolBar(
    modifier: Modifier = Modifier,
    state: BottomToolBarState,
) {
    when (state) {
        is BottomToolBarState.Info -> {
            InfoToolBar(
                modifier = modifier,
                state = state.infoState,
            )
        }
        else -> {}
    }
}

