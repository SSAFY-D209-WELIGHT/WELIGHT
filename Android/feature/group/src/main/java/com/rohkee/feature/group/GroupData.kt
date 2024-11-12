package com.rohkee.feature.group

import com.rohkee.core.ui.component.group.CardListItemState
import kotlinx.collections.immutable.toPersistentList

data class GroupData(
    private val isValid: Boolean = false,
    val currentPostion: LatLng = LatLng(0.0, 0.0),
    val list: List<RoomData> = emptyList(),
) {
    fun toState() =
        if (isValid) {
            GroupState.Loaded(
                cardList =
                    list
                        .map {
                            CardListItemState(
                                id = it.roomId,
                                title = it.title,
                                description = it.description,
                                number = it.participants,
                            )
                        }.toPersistentList(),
            )
        } else {
            GroupState.Loading
        }

    fun invalidate() = copy(isValid = false)
    fun validate() = copy(isValid = true)
}

data class LatLng(
    val latitude: Double,
    val longitude: Double,
)

data class RoomData(
    val roomId: Long,
    val title: String,
    val description: String,
    val participants: Int,
)
