package com.rohkee.feat.display.editor

import com.rohkee.core.ui.model.CustomColor

sealed interface EditorEvent {
    sealed interface Open : EditorEvent {
        data object TextDeleteDialog : Open
        data object ImageDeleteDialog : Open
        data object BackgroundDeleteDialog : Open
        data object ExitDialog : Open
        data class ColorPicker(
            val color: CustomColor,
        ) : Open
        data class TextEditor(
            val title: String,
            val tags: List<String>,
        ) : Open
    }

    data object ExitPage : EditorEvent

    data class SaveDisplay(
        val displayId: Long,
    ) : EditorEvent

    data class ShowSnackBar(
        val message: String,
    ) : EditorEvent
}