package com.rohkee.feat.display.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rohkee.core.ui.component.appbar.SavableAppBar
import com.rohkee.core.ui.component.display.editor.BackgroundToolBar
import com.rohkee.core.ui.component.display.editor.CustomDisplay
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.EditorInfoState
import com.rohkee.core.ui.component.display.editor.ImageToolBar
import com.rohkee.core.ui.component.display.editor.InfoToolBar
import com.rohkee.core.ui.component.display.editor.TextToolBar
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground

@Composable
fun EditorContent(
    modifier: Modifier = Modifier,
    state: EditorState,
    onIntent: (EditorIntent) -> Unit = {},
) {
    when (state) {
        is EditorState.Loading -> LoadingContent(modifier = modifier)

        is EditorState.Edit ->
            EditContent(
                modifier = modifier,
                state = state,
                onIntent = onIntent,
            )

        is EditorState.Error -> {
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .animateGradientBackground(
                    startColor = AppColor.Background,
                    endColor = AppColor.Surface,
                ),
    )
}

@Composable
private fun EditContent(
    modifier: Modifier = Modifier,
    state: EditorState.Edit,
    onIntent: (EditorIntent) -> Unit = {},
) {
    Box(
        modifier = modifier,
    ) {
        CustomDisplay(
            modifier = Modifier.fillMaxSize(),
            imageState = state.editorImageState,
            textState = state.editorTextState,
            backgroundState = state.editorBackgroundState,
            onImageTransformed = { onIntent(EditorIntent.ImageObject.Transform(it)) },
            onTextTransformed = { onIntent(EditorIntent.TextObject.Transform(it)) },
        )

        SavableAppBar(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
            onCloseClick = { onIntent(EditorIntent.ExitPage) },
            onSaveClick = { onIntent(EditorIntent.Save) },
        )

        BottomBarContent(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            state = state,
            onIntent = onIntent,
        )
    }
}

@Composable
private fun BottomBarContent(
    modifier: Modifier = Modifier,
    state: EditorState.Edit,
    onIntent: (EditorIntent) -> Unit = {},
) {
    when (state.bottomBarState) {
        is EditingState.None -> {
            InfoToolBar(
                modifier = modifier,
                state = state.editorInfoState,
                onTextEditClick = { onIntent(EditorIntent.InfoToolBar.EditText) },
                onImageEditClick = { onIntent(EditorIntent.InfoToolBar.EditImage) },
                onBackgroundEditClick = { onIntent(EditorIntent.InfoToolBar.EditBackground) },
                onEditInfo = { onIntent(EditorIntent.InfoToolBar.EditText) },
            )
        }

        is EditingState.Text -> {
            TextToolBar(
                modifier = modifier,
                state = state.editorTextState,
                onSelectColor = { onIntent(EditorIntent.TextToolBar.SelectColor(it)) },
                onSelectCustomColor = { onIntent(EditorIntent.TextToolBar.SelectCustomColor(it)) },
                onSelectFont = { onIntent(EditorIntent.TextToolBar.SelectFont(it)) },
                onDelete = { onIntent(EditorIntent.TextToolBar.Delete) },
                onClose = { onIntent(EditorIntent.TextToolBar.Close) },
                onTextChange = { onIntent(EditorIntent.TextToolBar.EditText) },
            )
        }

        is EditingState.Image -> {
            ImageToolBar(
                modifier = modifier,
                state = state.editorImageState,
                onDelete = { onIntent(EditorIntent.ImageToolBar.Delete) },
                onClose = { onIntent(EditorIntent.ImageToolBar.Close) },
                onSelectColor = { onIntent(EditorIntent.ImageToolBar.SelectColor(it)) },
                onSelectCustomColor = { onIntent(EditorIntent.ImageToolBar.SelectCustomColor(it)) },
                onChangeImage = { onIntent(EditorIntent.ImageToolBar.Change) },
            )
        }

        is EditingState.Background -> {
            BackgroundToolBar(
                modifier = modifier,
                state = state.editorBackgroundState,
                onSelectColor = {
                    onIntent(
                        EditorIntent.BackgroundToolBar.SelectColor(
                            it,
                        ),
                    )
                },
                onSelectCustomColor = { onIntent(EditorIntent.BackgroundToolBar.SelectCustomColor(it)) },
                onChangeBrightness = {
                    onIntent(
                        EditorIntent.BackgroundToolBar.ChangeBrightness(
                            it,
                        ),
                    )
                },
                onDelete = { onIntent(EditorIntent.BackgroundToolBar.Delete) },
                onClose = { onIntent(EditorIntent.BackgroundToolBar.Close) },
            )
        }
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier) {
}

@Preview
@Composable
private fun EditorContentPreview() {
    EditorContent(
        state =
            EditorState.Edit(
                bottomBarState = EditingState.None,
                editorInfoState = EditorInfoState(),
                editorTextState = DisplayTextState(),
                editorImageState = DisplayImageState(),
                editorBackgroundState = DisplayBackgroundState(),
            ),
    )
}
