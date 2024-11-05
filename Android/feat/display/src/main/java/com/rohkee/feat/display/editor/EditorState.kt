package com.rohkee.feat.display.editor

import androidx.compose.runtime.Immutable
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.EditorInfoState

sealed interface EditorState {
    @Immutable
    data object Loading : EditorState

    @Immutable
    data class Edit(
        val displayId: Long? = null,
        val editorInfoState: EditorInfoState = EditorInfoState(),
        val editorImageState: DisplayImageState = DisplayImageState(),
        val editorTextState: DisplayTextState = DisplayTextState(),
        val editorBackgroundState: DisplayBackgroundState = DisplayBackgroundState(),
        val bottomBarState: EditingState,
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
