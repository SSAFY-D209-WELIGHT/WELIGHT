package com.rohkee.feature.board

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    viewModel: BoardViewModel = hiltViewModel(),
    onNavigateToDisplayDetail: (displayId: Long) -> Unit = {},
) {
    val context = LocalContext.current as ComponentActivity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.boardEvent.collectWithLifecycle { event ->
        when (event) {
            is BoardEvent.OpenBoardDisplayItem -> onNavigateToDisplayDetail(event.displayId)
        }
    }

    LaunchedEffect(Unit) {
        context.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AppColor.Background.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(AppColor.BackgroundTransparent.toArgb()),
        )
    }

    BoardContent(
        modifier = modifier,
        state = uiState,
        onIntent = viewModel::onIntent,
    )
}

// @Composable
// private fun BoardCard(
//    board: DisplayResponse.Short,
//    modifier: Modifier = Modifier,
// ) {
//    Card(modifier = modifier) {
//        Column {
//            AsyncImage(
//                model = board.thumbnailUrl,
//                contentDescription = null,
//                modifier =
//                    Modifier
//                        .fillMaxWidth()
//                        .height(194.dp),
//                contentScale = ContentScale.Crop,
//            )
//        }
//    }
// }
//
// private fun filterBoards(
//    boards: List<DisplayResponse.Short>,
//    query: String,
// ): List<DisplayResponse.Short> =
//    if (query.isEmpty()) {
//        boards
//    } else {
//        boards.filter { board ->
//            board.title.contains(query, ignoreCase = true)
//        }
//    }

// // BoardScreen.kt
// package com.rohkee.feature.board
//
