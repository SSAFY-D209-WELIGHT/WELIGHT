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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.rohkee.core.ui.component.storage.DisplayCard
import com.rohkee.core.ui.theme.AppColor

@Composable
fun LikeRecordScreen(viewModel: LikeRecordViewModel = hiltViewModel()) {
    val likedDisplaysState by viewModel.likedDisplaysState.collectAsState()

    when (val state = likedDisplaysState) {
        is LikedDisplaysState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        is LikedDisplaysState.Loaded -> {
            val lazyPagingItems = state.displayListFlow.collectAsLazyPagingItems()

            if (lazyPagingItems.itemCount == 0) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "좋아요한 디스플레이가 없습니다",
                        color = AppColor.OnBackground
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
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
                                modifier = Modifier.aspectRatio(0.5f),
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
