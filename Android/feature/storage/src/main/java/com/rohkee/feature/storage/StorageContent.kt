package com.rohkee.feature.storage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rohkee.core.ui.component.appbar.LogoAppBar
import com.rohkee.core.ui.component.storage.CreateDisplayButton
import com.rohkee.core.ui.component.storage.DisplayCard
import com.rohkee.core.ui.component.storage.DisplayCardState
import com.rohkee.core.ui.component.storage.InfiniteHorizontalPager
import com.rohkee.core.ui.component.storage.NoContentCard
import com.rohkee.core.ui.component.storage.RatioHorizontalPager

/**
 * 보관함 화면
 */
@Composable
fun StorageContent(
    modifier: Modifier = Modifier,
    state: StorageState,
    onIntent: (StorageIntent) -> Unit = {},
) {
    Column(
        modifier = modifier.padding(bottom = 16.dp),
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

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    InfiniteHorizontalPager(
        modifier = modifier,
        pageCount = 3,
    ) {
        DisplayCard(state = DisplayCardState(cardId = 0))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadedContent(
    modifier: Modifier = Modifier,
    state: StorageState.Loaded,
    onIntent: (StorageIntent) -> Unit = {},
) {
    val displayList = state.displayListFlow.collectAsLazyPagingItems()

    if (displayList.loadState.refresh is LoadState.Loading ||
        displayList.loadState.append is LoadState.Loading
    ) {
        return LoadingContent(modifier = modifier)
    } else if (displayList.itemCount == 0) {
        return NoContent(modifier = modifier)
    }

    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = displayList.loadState.refresh is LoadState.Loading,
        onRefresh = { displayList.refresh() },
    ){
        RatioHorizontalPager(
            pageCount = displayList.itemCount,
        ) { index ->
            displayList[index]?.let { item ->
                // DisplayCardWithFavorite(
                DisplayCard(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                    state = item,
                    onCardSelected = { onIntent(StorageIntent.SelectDisplay(displayId = item.cardId)) },
                    // onFavoriteSelected = { onIntent(StorageIntent.ToggleFavorite(displayId = item.cardId)) },
                )
            }
        }
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

@Preview
@Composable
private fun StorageLoadedPreview() {
    val pager =
        remember {
            Pager(
                PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = true,
                    maxSize = 200,
                ),
            ) {
                object : androidx.paging.PagingSource<Int, DisplayCardState>() {
                    override fun getRefreshKey(state: PagingState<Int, DisplayCardState>): Int? = state.anchorPosition

                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DisplayCardState> {
                        val currentPage = params.key ?: 1

                        return LoadResult.Page(
                            data =
                                List(10) { index ->
                                    DisplayCardState(
                                        cardId = index.toLong(),
                                        imageSource = null,
                                    )
                                },
                            prevKey = if (currentPage == 1) null else currentPage - 1,
                            nextKey = currentPage + 1,
                        )
                    }
                }
            }
        }

    StorageContent(
        state =
            StorageState.Loaded(
                displayListFlow = pager.flow,
            ),
    )
}
