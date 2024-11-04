package com.rohkee.core.ui.screen.display.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rohkee.core.ui.component.appbar.SavableAppBar
import com.rohkee.core.ui.component.display.editor.BackgroundToolBar
import com.rohkee.core.ui.component.display.editor.CustomDisplay
import com.rohkee.core.ui.component.display.editor.ImageToolBar
import com.rohkee.core.ui.component.display.editor.InfoToolBar
import com.rohkee.core.ui.component.display.editor.TextToolBar
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground

@Composable
fun DisplayEditorContent(
    modifier: Modifier = Modifier,
    state: DisplayEditorState,
    onIntent: (DisplayEditorIntent) -> Unit = {},
) {
    when (state) {
        is DisplayEditorState.Loading -> LoadingContent()

        is DisplayEditorState.Edit -> EditContent(state = state, onIntent = onIntent)

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
    onIntent: (DisplayEditorIntent) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SavableAppBar(
                onCloseClick = { onIntent(DisplayEditorIntent.ExitPage) },
                onSaveClick = { onIntent(DisplayEditorIntent.SaveDisplay) },
            )
        },
        bottomBar = {
            when (state.bottomBarState) {
                is EditingState.None -> {
                    InfoToolBar(
                        modifier = modifier,
                        state = state.editorInfoState,
                        onTextEditClick = { onIntent(DisplayEditorIntent.InfoToolBar.EditInfo) },
                        onImageEditClick = { onIntent(DisplayEditorIntent.InfoToolBar.EditImage) },
                        onBackgroundEditClick = { onIntent(DisplayEditorIntent.InfoToolBar.EditBackground) },
                        onEditInfo = { onIntent(DisplayEditorIntent.InfoToolBar.EditInfo) },
                    )
                }

                is EditingState.Text -> {
                    TextToolBar(
                        modifier = modifier,
                        state = state.editorTextState,
                        onSelectColor = { onIntent(DisplayEditorIntent.TextToolBar.SelectColor(it)) },
                        onSelectCustomColor = { onIntent(DisplayEditorIntent.TextToolBar.SelectCustomColor) },
                        onSelectFont = { onIntent(DisplayEditorIntent.TextToolBar.SelectFont(it)) },
                        onRotate = { onIntent(DisplayEditorIntent.TextToolBar.Rotate(it)) },
                        onDelete = { onIntent(DisplayEditorIntent.TextToolBar.Delete) },
                        onClose = { onIntent(DisplayEditorIntent.TextToolBar.Close) },
                    )
                }

                is EditingState.Image -> {
                    ImageToolBar(
                        modifier = modifier,
                        state = state.editorImageState,
                        onDelete = { onIntent(DisplayEditorIntent.ImageToolBar.Delete) },
                        onClose = { onIntent(DisplayEditorIntent.ImageToolBar.Close) },
                        onSelectColor = { onIntent(DisplayEditorIntent.ImageToolBar.SelectColor(it)) },
                        onSelectCustomColor = { onIntent(DisplayEditorIntent.ImageToolBar.SelectCustomColor) },
                        onRotate = { onIntent(DisplayEditorIntent.ImageToolBar.Rotate(it)) },
                        onChangeImage = { onIntent(DisplayEditorIntent.ImageToolBar.Change) },
                    )
                }

                is EditingState.Background -> {
                    BackgroundToolBar(
                        modifier = modifier,
                        state = state.editorBackgroundState,
                        onSelectColor = { onIntent(DisplayEditorIntent.BackgroundToolBar.SelectColor(it)) },
                        onSelectCustomColor = { onIntent(DisplayEditorIntent.BackgroundToolBar.SelectCustomColor) },
                        onChangeBrightness = { onIntent(DisplayEditorIntent.BackgroundToolBar.ChangeBrightness(it)) },
                        onDelete = { onIntent(DisplayEditorIntent.BackgroundToolBar.Delete) },
                        onClose = { onIntent(DisplayEditorIntent.BackgroundToolBar.Close) },
                    )
                }
            }
        },
    ) { innerPadding ->
        CustomDisplay(
            modifier = Modifier.padding(innerPadding),
            imageState = state.editorImageState,
            textState = state.editorTextState,
            backgroundState = state.editorBackgroundState,
        )
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier) {
}
