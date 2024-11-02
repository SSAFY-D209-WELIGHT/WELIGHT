package com.rohkee.core.ui.screen.display.editor

import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.BottomToolBarState

sealed interface DisplayEditorState {
    data object Loading : DisplayEditorState

    data class Edit(
        val displayId: Long? = null,
        val displayImageState: DisplayImageState,
        val displayTextState: DisplayTextState,
        val displayBackgroundState: DisplayBackgroundState,
        val bottomBarState: BottomToolBarState,
    ) : DisplayEditorState

    data object Error : DisplayEditorState
}
