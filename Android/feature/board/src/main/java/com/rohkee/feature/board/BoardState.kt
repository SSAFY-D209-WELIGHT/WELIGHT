// BoardState.kt
package com.rohkee.feature.board

import androidx.paging.PagingData
import com.rohkee.core.network.repository.SortType
import com.rohkee.core.ui.component.storage.DisplayCardState
import kotlinx.coroutines.flow.Flow

sealed interface BoardState {
    data object Loading : BoardState

    data class Loaded(
        val boards: Flow<PagingData<DisplayCardState>>,
        val searchQuery: String = "",
        val isSearchVisible: Boolean = false,
        val sortType: SortType = SortType.LATEST,
    ) : BoardState

    data class Error(
        val message: String,
    ) : BoardState
}
