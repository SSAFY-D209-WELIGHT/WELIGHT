package com.rohkee.feature.group.dialog

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rohkee.core.ui.component.storage.DisplayCard
import com.rohkee.core.ui.component.storage.DisplayCardState
import com.rohkee.core.ui.component.storage.InfiniteHorizontalPager
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun DisplaySelectionDialog(
    modifier: Modifier = Modifier,
    selectionDialogViewModel: SelectionDialogViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onConfirm: (displayId: Long, thumbnailUrl: String) -> Unit,
) {
    val dialogUIState = selectionDialogViewModel.state.collectAsStateWithLifecycle()

    selectionDialogViewModel.dialogEvent.collectWithLifecycle {
        when (it) {
            is SelectionDialogEvent.ExitPage -> onDismiss()
            is SelectionDialogEvent.SelectedDisplay -> onConfirm(it.displayId, "")
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        when (val state = dialogUIState.value) {
            SelectionDialogState.Loading -> LoadingContent(modifier = modifier)
            is SelectionDialogState.Loaded ->
                LoadedContent(
                    modifier = modifier,
                    state = state,
                    onIntent = selectionDialogViewModel::onIntent,
                )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    InfiniteHorizontalPager(
        modifier = modifier,
        pageCount = 3,
    ) {
        DisplayCard(state = DisplayCardState(cardId = 0))
    }
}

@Composable
private fun LoadedContent(
    modifier: Modifier = Modifier,
    state: SelectionDialogState.Loaded,
    onIntent: (SelectionDialogIntent) -> Unit = {},
) {
    val displayList = state.displayListFlow.collectAsLazyPagingItems()

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
    ) {
        items(displayList.itemCount) { index ->
            displayList[index]?.let { item ->
                DisplayCard(
                    modifier =
                        Modifier
                            .aspectRatio(0.5f)
                            .clip(RoundedCornerShape(4.dp)),
                    state = item,
                    onCardSelected = { onIntent(SelectionDialogIntent.SelectDisplay(displayId = item.cardId)) },
                )
            }
        }
        if (displayList.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentSize(),
                )
            }
        }
    }

//    InfiniteHorizontalPager(
//        modifier = modifier,
//        pageCount = displayList.itemCount,
//    ) { index ->
//        displayList[index]?.let { item ->
//            DisplayCard(
//                state = item,
//                onCardSelected = { onIntent(SelectionDialogIntent.SelectDisplay(displayId = item.cardId)) },
//            )
//        }
//    }
}
