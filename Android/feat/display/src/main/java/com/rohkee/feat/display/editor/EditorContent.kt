package com.rohkee.feat.display.editor

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
    Scaffold(
        modifier = modifier,
        topBar = {
            SavableAppBar(
                onCloseClick = { onIntent(EditorIntent.ExitPage) },
                onSaveClick = { onIntent(EditorIntent.Save) },
            )
        },
        bottomBar = {
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
                        onSelectCustomColor = { onIntent(EditorIntent.TextToolBar.SelectCustomColor) },
                        onSelectFont = { onIntent(EditorIntent.TextToolBar.SelectFont(it)) },
                        onRotate = { onIntent(EditorIntent.TextToolBar.Rotate(it)) },
                        onDelete = { onIntent(EditorIntent.TextToolBar.Delete) },
                        onClose = { onIntent(EditorIntent.TextToolBar.Close) },
                    )
                }

                is EditingState.Image -> {
                    ImageToolBar(
                        modifier = modifier,
                        state = state.editorImageState,
                        onDelete = { onIntent(EditorIntent.ImageToolBar.Delete) },
                        onClose = { onIntent(EditorIntent.ImageToolBar.Close) },
                        onSelectColor = { onIntent(EditorIntent.ImageToolBar.SelectColor(it)) },
                        onSelectCustomColor = { onIntent(EditorIntent.ImageToolBar.SelectCustomColor) },
                        onRotate = { onIntent(EditorIntent.ImageToolBar.Rotate(it)) },
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
                        onSelectCustomColor = { onIntent(EditorIntent.BackgroundToolBar.SelectCustomColor) },
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
        },
    ) { innerPadding ->
        CustomDisplay(
            modifier = Modifier.padding(innerPadding),
            imageState = state.editorImageState,
            textState = state.editorTextState,
            backgroundState = state.editorBackgroundState,
            onImageTransformed = { onIntent(EditorIntent.TransformImage(it)) },
            onTextTransformed = { onIntent(EditorIntent.TransfromText(it)) },
        )
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier) {
}
