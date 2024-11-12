package com.rohkee.feature.editor

import androidx.compose.runtime.Immutable
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.EditorInfoState
import com.rohkee.core.ui.model.CustomColor

sealed interface EditorState {
    @Immutable
    data object Loading : EditorState

    @Immutable
    data class Edit(
        val displayId: Long? = null,
        val editorInfoState: EditorInfoState = EditorInfoState(),
        val displayImageState: DisplayImageState = DisplayImageState(),
        val displayTextState: DisplayTextState = DisplayTextState(),
        val displayBackgroundState: DisplayBackgroundState = DisplayBackgroundState(),
        val editingState: EditingState = EditingState.None,
        val dialogState: DialogState = DialogState.Closed,
    ) : EditorState

    @Immutable
    data object Error : EditorState
}

sealed interface EditingState {
    @Immutable
    data object None : EditingState

    @Immutable
    data object Text : EditingState

    @Immutable
    data object Image : EditingState

    @Immutable
    data object Background : EditingState
}

sealed interface DialogState {
    @Immutable
    data object Closed : DialogState

    @Immutable
    data object ExitAsking : DialogState

    @Immutable
    data object TextDeleteWarning : DialogState

    @Immutable
    data object ImageDeleteWarning : DialogState

    @Immutable
    data object BackgroundDeleteWarning : DialogState

    @Immutable
    data class ColorPicker(
        val color: CustomColor? = null,
    ) : DialogState

    @Immutable
    data class TextEdit(
        val text: String = "",
    ) : DialogState

    @Immutable
    data class InfoEdit(
        val info: EditorInfoState? = null,
    ) : DialogState
}
