package com.rohkee.core.ui.screen.display.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rohkee.core.ui.component.appbar.SavableAppBar
import com.rohkee.core.ui.component.display.editor.BottomToolBar
import com.rohkee.core.ui.component.display.editor.CustomDisplay
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground

@Composable
fun DisplayEditorContent(
    modifier: Modifier = Modifier,
    state: DisplayEditorState,
) {
    when (state) {
        is DisplayEditorState.Loading -> LoadingContent()

        is DisplayEditorState.Edit -> EditContent(state = state)

        is DisplayEditorState.Error -> {
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier =
            Modifier
                .animateGradientBackground(
                    startColor = AppColor.Background,
                    endColor = AppColor.Surface,
                ),
    )
}

@Composable
private fun EditContent(
    modifier: Modifier = Modifier,
    state: DisplayEditorState.Edit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SavableAppBar(
                onCloseClick = {},
                onSaveClick = {},
            )
        },
        bottomBar = {
            BottomToolBar(
                state = state.bottomBarState,
                infoState = state.editorInfoState,
                textState = state.editorTextState,
                imageState = state.editorImageState,
                backgroundState = state.editorBackgroundState,
            )
        },
    ) { innerPadding ->
        CustomDisplay(
            modifier = Modifier.padding(innerPadding),
            imageState = state.editorImageState.imageState,
            textState = state.editorTextState.textState,
            backgroundState = state.editorBackgroundState.backgroundState,
        )
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier) {
}
