package com.rohkee.feature.board

sealed class BoardIntent {
    object LoadBoards : BoardIntent()
    data class SearchBoards(val query: String) : BoardIntent()
    object ToggleSearch : BoardIntent()
    object CloseSearch : BoardIntent()
}
