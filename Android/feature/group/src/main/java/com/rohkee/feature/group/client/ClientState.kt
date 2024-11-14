package com.rohkee.feature.group.client

sealed interface ClientState {
    data object Loading : ClientState

    data class Loaded(
        val title: String,
        val description: String,
        val groupId: Long,
        val groupSize: Long,
        val displayId: Long?,
        val thumbnailUrl: String?,
    ) : ClientState
}
