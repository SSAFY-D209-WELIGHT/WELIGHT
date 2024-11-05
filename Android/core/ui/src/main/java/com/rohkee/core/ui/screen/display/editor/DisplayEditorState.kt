package com.rohkee.core.ui.screen.display.editor

import androidx.compose.runtime.Immutable
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.EditorInfoState

sealed interface DisplayEditorState {
    @Immutable
    data object Loading : DisplayEditorState

    @Immutable
    data class Edit(
        val displayId: Long? = null,
        val editorInfoState: EditorInfoState = EditorInfoState(),
        val editorImageState: DisplayImageState = DisplayImageState(),
        val editorTextState: DisplayTextState = DisplayTextState(),
        val editorBackgroundState: DisplayBackgroundState = DisplayBackgroundState(),
        val bottomBarState: EditingState,
    ) : DisplayEditorState

    @Immutable
    data object Error : DisplayEditorState
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
