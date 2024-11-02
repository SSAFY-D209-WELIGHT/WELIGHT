package com.rohkee.core.ui.component.display.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

@Immutable
sealed interface BottomToolBarState {
    data object Info : BottomToolBarState

    data object Text : BottomToolBarState

    data object Image : BottomToolBarState

    data object Background : BottomToolBarState
}

@Composable
fun BottomToolBar(
    modifier: Modifier = Modifier,
    state: BottomToolBarState,
    infoState: EditorInfoState,
    textState: EditorTextState,
    imageState: EditorImageState,
    backgroundState: EditorBackgroundState,
) {
    when (state) {
        is BottomToolBarState.Info -> {
            InfoToolBar(
                modifier = modifier,
                state = infoState,
            )
        }

        is BottomToolBarState.Text -> {
            TextToolBar(
                modifier = modifier,
                state = textState,
            )
        }

        is BottomToolBarState.Image -> {
            ImageToolBar(
                modifier = modifier,
                state = imageState,
            )
        }

        is BottomToolBarState.Background -> {
            BackgroundToolBar(
                modifier = modifier,
                state = backgroundState,
            )
        }
    }
}
