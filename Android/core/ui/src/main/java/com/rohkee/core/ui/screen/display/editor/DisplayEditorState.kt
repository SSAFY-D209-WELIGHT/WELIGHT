package com.rohkee.core.ui.screen.display.editor

import com.rohkee.core.ui.component.display.editor.BottomToolBarState
import com.rohkee.core.ui.component.display.editor.EditorBackgroundState
import com.rohkee.core.ui.component.display.editor.EditorImageState
import com.rohkee.core.ui.component.display.editor.EditorInfoState
import com.rohkee.core.ui.component.display.editor.EditorTextState

sealed interface DisplayEditorState {
    data object Loading : DisplayEditorState

    data class Edit(
        val displayId: Long? = null,
        val editorInfoState: EditorInfoState,
        val editorImageState: EditorImageState,
        val editorTextState: EditorTextState,
        val editorBackgroundState: EditorBackgroundState,
        val bottomBarState: BottomToolBarState,
    ) : DisplayEditorState

    data object Error : DisplayEditorState
}
