package com.rohkee.feature.board

sealed interface BoardEvent {
    data class OpenBoardDisplayItem(
        val displayId: Long,
    ) : BoardEvent
}
