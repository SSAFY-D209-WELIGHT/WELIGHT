package com.rohkee.core.ui.screen.display.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rohkee.core.ui.component.appbar.SavableAppBar
import com.rohkee.core.ui.component.display.editor.CustomDisplay
import com.rohkee.core.ui.component.display.editor.BottomToolBar
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground

@Composable
fun DisplayEditorContent(
    modifier: Modifier = Modifier,
    state: DisplayEditorState,
    onIntent: (DisplayEditorIntent) -> Unit = {},
) {
    when (state) {
        is DisplayEditorState.Loading -> {
        }

        is DisplayEditorState.Edit -> {
        }

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
    onIntent: (DisplayEditorIntent) -> Unit,
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
            )
        },
    ) { innerPadding ->
        CustomDisplay(
            modifier = Modifier.padding(innerPadding),
            imageState = state.displayImageState,
            textState = state.displayTextState,
            backgroundState = state.displayBackgroundState,
        )
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier) {
}
