package com.rohkee.feature.group

import androidx.compose.runtime.Immutable
import com.rohkee.core.ui.component.group.CardListItemState
import kotlinx.collections.immutable.PersistentList

sealed interface GroupState {
    @Immutable
    data object Loading : GroupState

    @Immutable
    data class Loaded(
        val cardList: PersistentList<CardListItemState>,
    ) : GroupState
}
