package com.rohkee.feature.detail

import androidx.compose.runtime.Immutable
import com.rohkee.core.ui.component.display.detail.DetailAppBarState
import com.rohkee.core.ui.component.display.detail.DetailInfoState
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class DisplayDetailData(
    val displayId: Long = 0,
    val thumbnailUrl: String = "",
    val isAuthor: Boolean = false,
    val isPublished: Boolean = false,
    val isFavorite: Boolean = false,
    val title: String = "",
    val tags: PersistentList<String> = persistentListOf(),
    val author: String = "",
    val liked: Boolean = false,
    val stored: Boolean = false,
    val like: Int = 0,
    val download: Int = 0,
    val comment: Int = 0,
    val displayImageState: DisplayImageState = DisplayImageState(),
    val displayTextState: DisplayTextState = DisplayTextState(),
    val displayBackgroundState: DisplayBackgroundState = DisplayBackgroundState(),
    val dialogState: DetailDialogState = DetailDialogState.Closed,
) {
    fun toState(): DetailState =
        DetailState.Loaded(
            displayId = displayId,
            thumbnailUrl = thumbnailUrl,
            detailInfoState =
            if (isPublished) {
                DetailInfoState.Loaded.Shared(
                    title = title,
                    tags = tags,
                    author = author,
                    liked = liked,
                    like = like,
                    download = download,
                    comment = comment,
                    stored = stored,
                )
            } else {
                DetailInfoState.Loaded.Default(
                    title = title,
                    tags = tags,
                    author = author,
                )
            },
            detailAppBarState =
            if (!stored) {
                DetailAppBarState.View
            } else if (isPublished) {
                DetailAppBarState.Editable.Shared(
                    isFavorite = isFavorite,
                )
            } else {
                DetailAppBarState.Editable.Default(
                    isFavorite = isFavorite,
                )
            },
            displayImageState = displayImageState,
            displayTextState = displayTextState,
            displayBackgroundState = displayBackgroundState,
            dialogState = dialogState
        )
}
