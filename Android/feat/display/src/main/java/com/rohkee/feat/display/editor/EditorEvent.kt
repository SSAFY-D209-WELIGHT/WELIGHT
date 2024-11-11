package com.rohkee.feat.display.editor

sealed interface EditorEvent {
    data object OpenPhotoGallery : EditorEvent

    data object ExitPage : EditorEvent

    data class ShowSnackBar(
        val message: String,
    ) : EditorEvent

    sealed interface Save : EditorEvent {
        data object Success : Save

        data object Failure : Save
    }
}
