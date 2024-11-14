
// BoardState.kt
package com.rohkee.feature.board

import androidx.paging.PagingData
import com.rohkee.core.network.model.DisplayResponse
import kotlinx.coroutines.flow.Flow
sealed class BoardUiState {
    object Loading : BoardUiState()
    data class Success(
        val content: BoardContent
    ) : BoardUiState()
    data class Error(val message: String) : BoardUiState()
}