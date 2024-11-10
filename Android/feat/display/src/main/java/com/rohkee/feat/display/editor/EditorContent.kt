package com.rohkee.feat.display.editor

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
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
import com.rohkee.core.ui.dialog.AskingDialog
import com.rohkee.core.ui.dialog.ColorPickerDialog
import com.rohkee.core.ui.dialog.TextInputDialog
import com.rohkee.core.ui.dialog.WarningDialog
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground
import com.rohkee.feat.display.R

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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun EditContent(
    modifier: Modifier = Modifier,
    state: EditorState.Edit,
    onIntent: (EditorIntent) -> Unit = {},
) {
    when (state.dialogState) {
        DialogState.Closed -> {}
        is DialogState.ExitAsking ->
            AskingDialog(
                title = stringResource(R.string.dialog_exit_edit_title),
                content = stringResource(R.string.dialog_exit_edit_content),
                onConfirm = {
                    onIntent(EditorIntent.Dialog.ExitPage)
                },
                onDismiss = { onIntent(EditorIntent.Dialog.Close) },
            )

        is DialogState.ColorPicker ->
            ColorPickerDialog(
                color = state.dialogState.color,
                onConfirm = { onIntent(EditorIntent.Dialog.ColorPicked(it)) },
                onDismiss = { onIntent(EditorIntent.Dialog.Close) },
            )

        DialogState.ImageDeleteWarning ->
            WarningDialog(
                title = stringResource(R.string.dialog_delete_image_title),
                content = stringResource(R.string.dialog_delete_image_content),
                onConfirm = { onIntent(EditorIntent.Dialog.DeleteImage) },
                onDismiss = { onIntent(EditorIntent.Dialog.Close) },
            )

        DialogState.TextDeleteWarning ->
            WarningDialog(
                title = stringResource(R.string.dialog_delete_text_title),
                content = stringResource(R.string.dialog_delete_text_content),
                onConfirm = { onIntent(EditorIntent.Dialog.DeleteText) },
                onDismiss = { onIntent(EditorIntent.Dialog.Close) },
            )

        DialogState.BackgroundDeleteWarning ->
            WarningDialog(
                title = stringResource(R.string.dialog_delete_background_title),
                content = stringResource(R.string.dialog_delete_background_content),
                onConfirm = { onIntent(EditorIntent.Dialog.DeleteBackground) },
                onDismiss = { onIntent(EditorIntent.Dialog.Close) },
            )

        is DialogState.InfoEdit ->
            InfoEditDialog(
                title = state.editorInfoState.title,
                tags = state.editorInfoState.tags,
                onDismiss = { onIntent(EditorIntent.Dialog.Close) },
                onConfirm = { title, tags -> onIntent(EditorIntent.Dialog.EditInfo(title, tags)) },
            )

        is DialogState.TextEdit ->
            TextInputDialog(
                hint = stringResource(R.string.dialog_text_input_hint),
                onConfirm = { onIntent(EditorIntent.Dialog.EditText(it)) },
                onDismiss = { onIntent(EditorIntent.Dialog.Close) },
            )
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val graphicsLayer = rememberGraphicsLayer()

    val writeStorageAccessState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            emptyList()
        } else {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    )

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier.fillMaxSize().drawWithCache {
                        onDrawWithContent {
                            graphicsLayer.record {
                                this@onDrawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        }
                    },
            ) {
                CustomDisplay(
                    modifier = Modifier.fillMaxSize(),
                    imageState = state.displayImageState,
                    textState = state.displayTextState,
                    backgroundState = state.displayBackgroundState,
                    onImageTransformed = { onIntent(EditorIntent.ImageObject.Transform(it)) },
                    onTextTransformed = { onIntent(EditorIntent.TextObject.Transform(it)) },
                )
            }

            SavableAppBar(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = innerPadding.calculateTopPadding()),
                onCloseClick = { onIntent(EditorIntent.AttemptExitPage) },
                onSaveClick = { onIntent(EditorIntent.Save(context, graphicsLayer)) },
            )
            BottomBarContent(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                state = state,
                onIntent = onIntent,
            )
        }
    }
}

@Composable
private fun BottomBarContent(
    modifier: Modifier = Modifier,
    state: EditorState.Edit,
    onIntent: (EditorIntent) -> Unit = {},
) {
    when (state.editingState) {
        is EditingState.None -> {
            InfoToolBar(
                modifier = modifier,
                state = state.editorInfoState,
                onTextEditClick = { onIntent(EditorIntent.InfoToolBar.EditText) },
                onImageEditClick = { onIntent(EditorIntent.InfoToolBar.EditImage) },
                onBackgroundEditClick = { onIntent(EditorIntent.InfoToolBar.EditBackground) },
                onEditInfo = { onIntent(EditorIntent.InfoToolBar.EditInfo) },
            )
        }

        is EditingState.Text -> {
            TextToolBar(
                modifier = modifier,
                state = state.displayTextState,
                onSelectColor = { onIntent(EditorIntent.TextToolBar.SelectColor(it)) },
                onSelectCustomColor = { onIntent(EditorIntent.TextToolBar.SelectCustomColor) },
                onSelectFont = { onIntent(EditorIntent.TextToolBar.SelectFont(it)) },
                onDelete = { onIntent(EditorIntent.TextToolBar.Delete) },
                onClose = { onIntent(EditorIntent.TextToolBar.Close) },
                onTextChange = { onIntent(EditorIntent.TextToolBar.EditText) },
            )
        }

        is EditingState.Image -> {
            ImageToolBar(
                modifier = modifier,
                state = state.displayImageState,
                onDelete = { onIntent(EditorIntent.ImageToolBar.Delete) },
                onClose = { onIntent(EditorIntent.ImageToolBar.Close) },
                onSelectColor = { onIntent(EditorIntent.ImageToolBar.SelectColor(it)) },
                onSelectCustomColor = { onIntent(EditorIntent.ImageToolBar.SelectCustomColor) },
                onChangeImage = { onIntent(EditorIntent.ImageToolBar.Change) },
            )
        }

        is EditingState.Background -> {
            BackgroundToolBar(
                modifier = modifier,
                state = state.displayBackgroundState,
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
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier) {
}

@Preview
@Composable
private fun EditorContentPreview() {
    Scaffold { innerPadding ->
        EditorContent(
            state =
                EditorState.Edit(
                    editingState = EditingState.None,
                    editorInfoState = EditorInfoState(),
                    displayTextState = DisplayTextState(),
                    displayImageState = DisplayImageState(),
                    displayBackgroundState = DisplayBackgroundState(),
                ),
        )
    }
}
