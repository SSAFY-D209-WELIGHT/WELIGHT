package com.rohkee.feat.detail

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
    val like: Int = 0,
    val download: Int = 0,
    val comment: Int = 0,
    val displayImageState: DisplayImageState = DisplayImageState(),
    val displayTextState: DisplayTextState = DisplayTextState(),
    val displayBackgroundState: DisplayBackgroundState = DisplayBackgroundState(),
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
                    )
                } else {
                    DetailInfoState.Loaded.Default(
                        title = title,
                        tags = tags,
                        author = author,
                    )
                },
            detailAppBarState =
                if (!isAuthor) {
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
        )
}
