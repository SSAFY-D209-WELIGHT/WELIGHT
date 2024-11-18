package com.rohkee.feature.detail

import androidx.compose.runtime.Immutable
import com.rohkee.core.ui.component.display.detail.DetailAppBarState
import com.rohkee.core.ui.component.display.detail.DetailInfoState
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState

sealed interface DetailState {
    @Immutable
    data object Loading : DetailState

    @Immutable
    data class Loaded(
        val displayId: Long,
        val thumbnailUrl: String,
        val detailAppBarState: DetailAppBarState,
        val detailInfoState: DetailInfoState,
        val displayImageState: DisplayImageState,
        val displayTextState: DisplayTextState,
        val displayBackgroundState: DisplayBackgroundState,
        val dialogState: DetailDialogState,
    ) : DetailState

    @Immutable
    data class Error(
        val message: String,
    ) : DetailState
}

sealed interface DetailDialogState {
    @Immutable
    data object Closed : DetailDialogState

    @Immutable
    data object Publish : DetailDialogState

    @Immutable
    data object Delete : DetailDialogState
}
