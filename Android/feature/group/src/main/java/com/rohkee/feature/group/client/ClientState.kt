package com.rohkee.feature.group.client

sealed interface ClientState {
    data object Loading : ClientState

    data class Loaded(
        val title: String,
        val description: String,
        val groupNumber: Int,
        val displays: List<Long>,
        val thumbnailUrl: String?,
        val dialogState: ClientDialogState,
    ) : ClientState


}