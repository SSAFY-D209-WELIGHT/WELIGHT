package com.rohkee.feature.editor

sealed interface EditorEvent {
    data object OpenPhotoGallery : EditorEvent

    data object ExitPage : EditorEvent

    sealed interface Save : EditorEvent {
        data class Success(
            val displayId: Long,
        ) : Save

        data object Failure : Save
    }
}
