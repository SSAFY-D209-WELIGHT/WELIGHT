package com.rohkee.feature.board

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.rohkee.core.ui.component.appbar.TitleAppBar
import com.rohkee.core.ui.component.storage.SmallDisplayCard
import com.rohkee.core.ui.theme.AppColor
import kotlinx.coroutines.flow.flow

@Composable
fun BoardContent(
    modifier: Modifier = Modifier,
    state: BoardState,
    onIntent: (BoardIntent) -> Unit,
) {
    when (state) {
        is BoardState.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
            )
        }

        is BoardState.Loaded -> {
            LoadedContent(
                modifier = modifier,
                state = state,
                onIntent = onIntent,
            )
        }

        is BoardState.Error -> {
            Text(
                text = state.message,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun LoadedContent(
    modifier: Modifier = Modifier,
    state: BoardState.Loaded,
    onIntent: (BoardIntent) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (!state.isSearchVisible) {
            TitleAppBar(
                title = "게시판",
            ) {
                Icon(
                    modifier =
                        Modifier.size(24.dp).clickable {
                            onIntent(BoardIntent.ToggleSearch)
                        },
                    imageVector = Icons.Filled.Search,
                    contentDescription = "검색",
                    tint = AppColor.OnBackground,
                )
            }
        } else {
            TitleAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = "",
            ) {
                TextField(
                    value = state.searchQuery,
                    onValueChange = {
                        onIntent(BoardIntent.SearchBoards(it))
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("응원도구를 검색해보세요") },
                    singleLine = true,
                    colors =
                        TextFieldDefaults
                            .colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ).copy(cursorColor = AppColor.OnBackground),
                )
                Icon(
                    modifier = Modifier.clickable { onIntent(BoardIntent.CloseSearch) },
                    imageVector = Icons.Default.Close,
                    contentDescription = "뒤로가기",
                    tint = AppColor.OnBackground,
                )
            }
        }

        val boardItems = state.boards.collectAsLazyPagingItems()

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(
                count = boardItems.itemCount,
                key = { index -> boardItems[index]?.cardId ?: index },
            ) { index ->
                boardItems[index]?.let { board ->
                    SmallDisplayCard(
                        modifier =
                            Modifier
                                .aspectRatio(0.5f),
                        state = board,
                        onCardSelected = { onIntent(BoardIntent.SelectBoardItem(board.cardId)) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun BoardContentPreview() {
    BoardContent(
        state = BoardState.Loading,
        onIntent = {},
    )
}

@Preview
@Composable
private fun LoadedContentPreview() {
    LoadedContent(
        state =
            BoardState.Loaded(
                boards = flow {},
                isSearchVisible = false,
                searchQuery = "",
            ),
        onIntent = {},
    )
}

@Preview
@Composable
private fun LoadedContentSearchPreview() {
    LoadedContent(
        state =
            BoardState.Loaded(
                boards = flow {},
                isSearchVisible = true,
                searchQuery = "",
            ),
        onIntent = {},
    )
}
