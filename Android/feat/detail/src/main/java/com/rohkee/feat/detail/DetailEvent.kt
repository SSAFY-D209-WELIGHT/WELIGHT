package com.rohkee.feat.detail

sealed interface DetailEvent {
    data object ExitPage : DetailEvent

    sealed interface Download : DetailEvent {
        data object Success : Download

        data object Error : Download
    }

    data class EditDisplay(
        val displayId: Long,
    ) : DetailEvent

    sealed interface Duplicate : DetailEvent {
        data class Success(
            val displayId: Long,
        ) : Duplicate

        data object Error : Duplicate
    }

    sealed interface Delete : DetailEvent {
        data object Success : Delete

        data object Error : Delete
    }
}
