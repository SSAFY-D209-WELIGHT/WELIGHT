package com.rohkee.feat.mypage.like_record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.rohkee.core.ui.component.storage.DisplayCard
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun LikeRecordScreen(
    modifier: Modifier = Modifier,
    viewModel: LikeRecordViewModel = hiltViewModel(),
) {
    val likedDisplaysState by viewModel.likedDisplaysState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = likedDisplaysState) {
            is LikedDisplaysState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is LikedDisplaysState.Loaded -> {
                val lazyPagingItems = state.displayListFlow.collectAsLazyPagingItems()

                if (lazyPagingItems.itemCount == 0) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "좋아요한 디스플레이가 없습니다",
                        style = Pretendard.Medium24,
                        color = AppColor.OnBackgroundTransparent,
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
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
                                )
                            }
                        }
                    }
                }
            }

            is LikedDisplaysState.Error -> {
                // TODO : 에러 처리
            }
        }
    }
}
