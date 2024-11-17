package com.rohkee.feature.group.client

import androidx.compose.runtime.Immutable

data class ClientData(
    val roomId: Long,
    val title: String = "",
    val description: String = "",
    val participants: Int = 0,
    val groupNumber: Int = 1,
    val displays: List<Long> = emptyList(),
    val thumbnailUrl: String? = null,
    val dialogState: ClientDialogState = ClientDialogState.Closed,
) {
    fun toState(): ClientState =
        if (displays.isEmpty()) {
            ClientState.Loading
        } else {
            ClientState.Loaded(
                title = title,
                description = description,
                groupNumber = groupNumber,
                thumbnailUrl = thumbnailUrl,
                displays = displays,
                dialogState = dialogState,
            )
        }
}

@Immutable
sealed interface ClientDialogState {
    data object Closed : ClientDialogState

    data class StartCheer(
        val displayId: Long,
        val offset: Float,
        val interval: Float,
    ) : ClientDialogState
}
