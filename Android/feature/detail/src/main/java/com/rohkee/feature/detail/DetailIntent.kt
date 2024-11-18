package com.rohkee.feature.detail

sealed interface DetailIntent {
    data object ExitPage : DetailIntent

    data object ToggleUI : DetailIntent

    data object ToggleLike : DetailIntent

    data object Download : DetailIntent

    data object Comment : DetailIntent

    data object ToggleFavorite : DetailIntent

    data object Post : DetailIntent

    data object Edit : DetailIntent

    data object Duplicate : DetailIntent

    data object Delete : DetailIntent

    sealed interface Dialog : DetailIntent {
        data object Close : Dialog

        data object Publish : Dialog

        data object Delete : Dialog
    }
}
