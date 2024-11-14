package com.rohkee.feature.board

sealed interface BoardIntent {
    data object LoadBoards : BoardIntent

    data class SearchBoards(
        val query: String,
    ) : BoardIntent

    data object ToggleSearch : BoardIntent

    data object CloseSearch : BoardIntent

    data class SelectBoardItem(
        val displayId: Long,
    ) : BoardIntent
}
