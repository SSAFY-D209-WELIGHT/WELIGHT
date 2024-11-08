package com.rohkee.feat.detail

sealed interface DetailEvent {
    data object ExitPage : DetailEvent

    data class ShowSnackBar(
        val message: String,
    ) : DetailEvent
}
