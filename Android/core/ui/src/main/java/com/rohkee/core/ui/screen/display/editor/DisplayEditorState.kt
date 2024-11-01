package com.rohkee.core.ui.screen.display.editor

sealed interface DisplayEditorState {
    data object Loading : DisplayEditorState

    data class Edit(
        val displayId: Long,
    ) : DisplayEditorState

    data object Create : DisplayEditorState

    data object Error : DisplayEditorState
}
