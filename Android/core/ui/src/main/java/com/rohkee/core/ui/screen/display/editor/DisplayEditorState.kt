package com.rohkee.core.ui.screen.display.editor

import com.rohkee.core.ui.component.display.editor.BottomToolBarState
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.EditorInfoState

sealed interface DisplayEditorState {
    data object Loading : DisplayEditorState

    data class Edit(
        val displayId: Long? = null,
        val editorInfoState: EditorInfoState,
        val editorImageState: DisplayImageState,
        val editorTextState: DisplayTextState,
        val editorBackgroundState: DisplayBackgroundState,
        val bottomBarState: BottomToolBarState,
    ) : DisplayEditorState

    data object Error : DisplayEditorState
}
