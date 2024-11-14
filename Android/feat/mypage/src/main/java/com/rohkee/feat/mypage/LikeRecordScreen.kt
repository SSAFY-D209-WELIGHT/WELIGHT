package com.rohkee.feat.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.rohkee.core.ui.component.storage.DisplayCardState

@Composable
fun LikeRecordScreen(viewModel: LikeRecordViewModel = hiltViewModel()) {
    val likedDisplaysState by viewModel.likedDisplaysState.collectAsState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val gridItemWidth = (configuration.screenWidthDp.dp - 32.dp - 16.dp) / 3
    val gridItemHeight = (screenHeight - 300.dp - 16.dp) / 2

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
                        color = Color.White,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(screenHeight - 200.dp)
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
                            LikedDisplayItem(
                                displayCard = item,
                                modifier = Modifier.size(gridItemWidth, gridItemHeight),
                            )
                        }
                    }
                }
            }
        }

        is LikedDisplaysState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.message,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun LikedDisplayItem(
    displayCard: DisplayCardState,
    modifier: Modifier = Modifier,
) {
    Image(
        painter =
            rememberAsyncImagePainter(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(displayCard.imageSource)
                        .crossfade(true)
                        .size(Size.ORIGINAL) // 이미지 크기 최적화
                        .memoryCacheKey(displayCard.cardId.toString()) // 캐싱 키 지정
                        .build(),
            ),
        contentDescription = null,
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray),
        contentScale = ContentScale.Crop,
    )
}
