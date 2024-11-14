package com.rohkee.feature.group.client

sealed interface ClientIntent {
    data object ExitPage : ClientIntent

    data class ChangeGroup(
        val groupId: Long,
    ) : ClientIntent
}
