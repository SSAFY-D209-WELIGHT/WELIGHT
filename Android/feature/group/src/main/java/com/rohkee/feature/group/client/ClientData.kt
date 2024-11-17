package com.rohkee.feature.group.client

import androidx.compose.runtime.Immutable

data class ClientData(
    val roomId: Long,
    val title: String = "",
    val description: String = "",
    val participants: Int = 0,
    val groupNumber: Int = 1,
    val groupSize: Int = 1,
    val displayId: Long? = null,
    val thumbnailUrl: String? = null,
    val dialogState: ClientDialogState = ClientDialogState.Closed,
) {
    fun toState(): ClientState =
        if (displayId == null) {
            ClientState.Loading
        } else {
            ClientState.Loaded(
                title = title,
                description = description,
                groupNumber = groupNumber,
                groupSize = groupSize,
                thumbnailUrl = thumbnailUrl,
                displayId = displayId,
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
