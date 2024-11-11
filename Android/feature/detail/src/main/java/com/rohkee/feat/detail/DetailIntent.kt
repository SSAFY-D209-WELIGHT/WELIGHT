package com.rohkee.feat.detail

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
}
