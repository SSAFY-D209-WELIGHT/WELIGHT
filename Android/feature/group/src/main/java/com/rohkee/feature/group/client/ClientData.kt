package com.rohkee.feature.group.client

data class ClientData(
    val roomId: Long,
    val title: String = "",
    val description: String = "",
    val groupId: Long = 1,
    val groupSize: Long = 1,
    val displayId: Long? = null,
    val thumbnailUrl: String? = null,
) {
    fun toState(): ClientState =
        ClientState.Loaded(
            title = title,
            description = description,
            groupId = groupId,
            groupSize = groupSize,
            thumbnailUrl = thumbnailUrl,
            displayId = displayId,
        )
}
