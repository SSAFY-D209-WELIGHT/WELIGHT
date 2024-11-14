package com.rohkee.feature.board
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.repository.SortType
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rohkee.feature.board.model.Board
import androidx.paging.PagingData
import com.rohkee.core.network.model.DisplayResponse
import kotlinx.coroutines.flow.Flow

data class BoardContent(
    val boards: Flow<PagingData<DisplayResponse.Short>> = Flow { PagingData.empty() },
    val searchQuery: String = "",
    val isSearchVisible: Boolean = false,
    val sortType: SortType = SortType.LATEST
)
//data class BoardContent(
//    val boards: List<Board> = emptyList(),
//    val searchQuery: String = "",
//    val isSearchVisible: Boolean = false
//)
//
