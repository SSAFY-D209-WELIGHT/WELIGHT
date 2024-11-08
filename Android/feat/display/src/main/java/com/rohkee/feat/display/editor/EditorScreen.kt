package com.rohkee.feat.display.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.dialog.AskingDialog
import com.rohkee.core.ui.dialog.ColorPickerDialog
import com.rohkee.core.ui.dialog.TextInputDialog
import com.rohkee.core.ui.dialog.WarningDialog
import com.rohkee.core.ui.util.collectWithLifecycle
import com.rohkee.feat.display.R

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    editorViewModel: EditorViewModel = hiltViewModel(),
    onNavigateToDisplayDetail: (Long) -> Unit,
    onPopBackStack: () -> Unit,
) {
    val editorUIState by editorViewModel.editorState.collectAsStateWithLifecycle()

    var isExitAskingDialogOpen by remember { mutableStateOf(false) }
    var isColorPickerOpen by remember { mutableStateOf(false) }
    var isTextDeleteWarningDialogOpen by remember { mutableStateOf(false) }
    var isImageDeleteWarningDialogOpen by remember { mutableStateOf(false) }
    var isBackgroundDeleteWarningDialogOpen by remember { mutableStateOf(false) }
    var isTextEditDialogOpen by remember { mutableStateOf(false) }

    editorViewModel.editorEvent.collectWithLifecycle { event ->
        when (event) {
            is EditorEvent.ExitPage -> onPopBackStack()
            is EditorEvent.Open.ColorPicker -> {
                isColorPickerOpen = true
            }
            is EditorEvent.SaveDisplay -> onNavigateToDisplayDetail(event.displayId)

            else -> {}
        }
    }

    if (isExitAskingDialogOpen) {
        AskingDialog(
            title = stringResource(R.string.dialog_exit_edit_title),
            content = stringResource(R.string.dialog_exit_edit_content),
            onConfirm = { editorViewModel.onIntent(EditorIntent.ExitPage) },
            onDismiss = { isExitAskingDialogOpen = false },
        )
    }
    if (isColorPickerOpen) {
        ColorPickerDialog(
            onConfirm = { editorViewModel.onIntent(EditorIntent.Dialog.ColorPicked(it)) },
            onDismiss = { isColorPickerOpen = false },
        )
    }
    if (isTextDeleteWarningDialogOpen) {
        WarningDialog(
            title = stringResource(R.string.dialog_delete_text_title),
            content = stringResource(R.string.dialog_delete_text_content),
            onConfirm = {},
            onDismiss = { isTextDeleteWarningDialogOpen = false },
        )
    }
    if (isImageDeleteWarningDialogOpen) {
        WarningDialog(
            title = stringResource(R.string.dialog_delete_text_title),
            content = stringResource(R.string.dialog_delete_text_content),
            onConfirm = {},
            onDismiss = { isImageDeleteWarningDialogOpen = false },
        )
    }
    if (isBackgroundDeleteWarningDialogOpen) {
        WarningDialog(
            title = stringResource(R.string.dialog_delete_text_title),
            content = stringResource(R.string.dialog_delete_text_content),
            onConfirm = {},
            onDismiss = { isBackgroundDeleteWarningDialogOpen = false },
        )
    }
    if (isTextEditDialogOpen) {
        TextInputDialog(
            hint = stringResource(R.string.dialog_text_input_hint),
            onDismiss = { isTextEditDialogOpen = false },
            onConfirm = { editorViewModel.onIntent(EditorIntent.Dialog.EditText(it)) },
        )
    }

    EditorContent(
        modifier = modifier,
        state = editorUIState,
        onIntent = editorViewModel::onIntent,
    )
}
