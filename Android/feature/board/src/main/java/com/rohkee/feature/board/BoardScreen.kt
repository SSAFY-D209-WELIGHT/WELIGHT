// BoardScreen.kt
package com.rohkee.feature.board


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rohkee.feature.board.model.Board

// BoardScreen.kt
@Composable
fun BoardScreen(
    viewModel: BoardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is BoardUiState.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize()
            )
        }
        is BoardUiState.Success -> {
            BoardContent(
                content = state.content,
                onIntent = viewModel::handleIntent
            )
        }
        is BoardUiState.Error -> {
            Text(
                text = state.message,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoardContent(
    content: BoardContent,
    onIntent: (BoardIntent) -> Unit
) {
    Column {
        Box {
            if (!content.isSearchVisible) {
                TopAppBar(
                    title = { Text(text = "게시판") },
                    actions = {
                        IconButton(onClick = {
                            onIntent(BoardIntent.ToggleSearch)
                        }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Filled.Search,
                                contentDescription = "검색",
                                tint = Color.Gray
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        TextField(
                            value = content.searchQuery,
                            onValueChange = {
                                onIntent(BoardIntent.SearchBoards(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("검색") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
//                            colors = TextFieldDefaults.textFieldColors(
//                                containerColor = Color.Transparent
//                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onIntent(BoardIntent.CloseSearch)
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = "뒤로가기"
                            )
                        }
                    }
                )
            }
        }

        BoardGrid(
            boardList = filterBoards(content.boards, content.searchQuery),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun BoardGrid(
    boardList: List<Board>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        items(boardList) { board ->
            BoardCard(
                board = board,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun BoardCard(
    board: Board,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            Image(
                painter = painterResource(board.imageResourceId),
                contentDescription = stringResource(board.stringResourceId),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private fun filterBoards(
    boards: List<Board>,
    query: String
): List<Board> {
    return if (query.isEmpty()) {
        boards
    } else {
        boards.filter { board ->
            true // Placeholder for actual string resource filtering
        }
    }
}