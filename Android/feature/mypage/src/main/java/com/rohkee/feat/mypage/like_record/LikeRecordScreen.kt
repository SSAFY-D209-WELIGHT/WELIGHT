package com.rohkee.feat.mypage.like_record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rohkee.core.ui.component.storage.DisplayCard
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard
import kotlinx.coroutines.flow.flow

@Composable
fun LikeRecordScreen(
    modifier: Modifier = Modifier,
    viewModel: LikeRecordViewModel = hiltViewModel(),
    onNavigateToDisplayDetail: (displayId: Long) -> Unit = {},
) {
    val likedDisplaysState by viewModel.likedDisplaysState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = likedDisplaysState) {
            is LikedDisplaysState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is LikedDisplaysState.Loaded -> {
                LoadedContent(
                    state = state,
                    onCardSelected = onNavigateToDisplayDetail,
                )
            }

            is LikedDisplaysState.Error -> {
                // TODO : 에러 처리
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadedContent(
    modifier: Modifier = Modifier,
    state: LikedDisplaysState.Loaded,
    onCardSelected: (displayId: Long) -> Unit = {},
) {
    val lazyPagingItems = state.displayListFlow.collectAsLazyPagingItems()

    PullToRefreshBox(
        isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading,
        onRefresh = { lazyPagingItems.refresh() },
        modifier = Modifier.fillMaxSize(),
    ) {
        if (lazyPagingItems.itemCount == 0) {
            Box(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "좋아요한 디스플레이가 없습니다",
                    style = Pretendard.Medium24,
                    color = AppColor.OnBackgroundTransparent,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier =
                    Modifier
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                state = rememberLazyGridState(), // 스크롤 상태 관리
                contentPadding = PaddingValues(vertical = 8.dp), // 스크롤 여백
            ) {
                items(
                    count = lazyPagingItems.itemCount,
                    key = { index -> lazyPagingItems[index]?.cardId ?: index },
                    contentType = { "image" },
                ) { index ->
                    val item = lazyPagingItems[index]
                    if (item != null) {
                        DisplayCard(
                            modifier =
                                Modifier
                                    .aspectRatio(0.5f)
                                    .clip(RoundedCornerShape(4.dp)),
                            state = item,
                            onCardSelected = { onCardSelected(item.cardId) },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LikeRecordScreenPreview() {
    LoadedContent(
        state =
            LikedDisplaysState.Loaded(
                displayListFlow = flow {},
            ),
    )
}
