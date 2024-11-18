package com.rohkee.feature.group.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rohkee.core.ui.component.appbar.GradientAppBar
import com.rohkee.core.ui.component.storage.DisplayCard
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard
import com.rohkee.core.ui.util.collectWithLifecycle
import kotlinx.coroutines.flow.flow

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
            is SelectionDialogEvent.SelectedDisplay -> onConfirm(it.displayId, it.thumbnailUrl)
        }
    }

    DialogContent(
        modifier = modifier,
        state = dialogUIState.value,
        onDismiss = onDismiss,
        onIntent = selectionDialogViewModel::onIntent,
    )
}

@Composable
private fun DialogContent(
    modifier: Modifier = Modifier,
    state: SelectionDialogState,
    onDismiss: () -> Unit,
    onIntent: (SelectionDialogIntent) -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(color = AppColor.Background),
        ) {
            GradientAppBar(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                onClick = onDismiss,
            ) {}
            when (state) {
                SelectionDialogState.Loading -> LoadingContent(modifier = modifier.weight(1f))
                is SelectionDialogState.Loaded ->
                    LoadedContent(
                        modifier = modifier.weight(1f),
                        state = state,
                        onIntent = onIntent,
                    )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(3),
    ) {
        items(3) {
            DisplayCard(modifier = Modifier.aspectRatio(0.5f))
        }
    }
}

@Composable
private fun NoContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "보관함에 디스플레이가 없습니다.",
            color = AppColor.OnBackground,
            style = Pretendard.SemiBold24,
        )
    }
}

@Composable
private fun LoadedContent(
    modifier: Modifier = Modifier,
    state: SelectionDialogState.Loaded,
    onIntent: (SelectionDialogIntent) -> Unit = {},
) {
    val displayList = state.displayListFlow.collectAsLazyPagingItems()

    if (displayList.loadState.refresh is LoadState.Loading ||
        displayList.loadState.append is LoadState.Loading
    ) {
        return LoadingContent(modifier = modifier)
    } else if (displayList.itemCount == 0) {
        return NoContent(modifier = modifier)
    }

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(displayList.itemCount) { index ->
            displayList[index]?.let { item ->
                DisplayCard(
                    modifier =
                        Modifier
                            .aspectRatio(0.5f)
                            .clip(RoundedCornerShape(4.dp)),
                    state = item,
                    onCardSelected = {
                        onIntent(
                            SelectionDialogIntent.SelectDisplay(
                                displayId = item.cardId,
                                thumbnailUrl = item.imageSource ?: "",
                            ),
                        )
                    },
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
                    color = AppColor.OverSurface,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectionDialogPreview() {
    DialogContent(
        onDismiss = {},
        state =
            SelectionDialogState.Loaded(
                displayListFlow = flow {},
            ),
        onIntent = {},
    )
}
