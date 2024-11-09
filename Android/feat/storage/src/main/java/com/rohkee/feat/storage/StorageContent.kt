package com.rohkee.feat.storage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.component.appbar.LogoAppBar
import com.rohkee.core.ui.component.storage.CreateDisplayButton
import com.rohkee.core.ui.component.storage.DisplayCard
import com.rohkee.core.ui.component.storage.DisplayCardState
import com.rohkee.core.ui.component.storage.InfiniteHorizontalPager
import com.rohkee.core.ui.component.storage.NoContentCard

/**
 * 보관함 화면
 */
@Composable
fun StorageContent(
    modifier: Modifier = Modifier,
    state: StorageState,
    onIntent: (StorageIntent) -> Unit = {},
) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LogoAppBar()
            when (state) {
                is StorageState.Loading -> {
                    LoadingContent(modifier = Modifier.weight(1f))
                }

                is StorageState.Loaded -> {
                    LoadedContent(
                        modifier = Modifier.weight(1f),
                        state = state,
                        onIntent = onIntent,
                    )
                }

                is StorageState.NoData -> {
                    NoContent(modifier = Modifier.weight(1f))
                }

                is StorageState.Error -> {
                    LoadingContent(modifier = Modifier.weight(1f))
                }
            }
            CreateDisplayButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                onClick = { onIntent(StorageIntent.CreateNewDisplay) },
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
    state: StorageState.Loaded,
    onIntent: (StorageIntent) -> Unit = {},
) {
    InfiniteHorizontalPager(
        modifier = modifier,
        pageCount = 3,
    ) { index ->
        DisplayCard(
            state = state.displayList[index],
            onCardSelected = { onIntent(StorageIntent.SelectDisplay(displayId = state.displayList[index].cardId)) },
        )
    }
}

@Composable
private fun NoContent(modifier: Modifier = Modifier) {
    InfiniteHorizontalPager(
        modifier = modifier,
        pageCount = 3,
    ) {
        NoContentCard(modifier = modifier)
    }
}

@Preview
@Composable
private fun StorageContentPreview() {
    StorageContent(state = StorageState.Loading)
}

@Preview
@Composable
private fun StorageNoContentPreview() {
    StorageContent(state = StorageState.NoData)
}