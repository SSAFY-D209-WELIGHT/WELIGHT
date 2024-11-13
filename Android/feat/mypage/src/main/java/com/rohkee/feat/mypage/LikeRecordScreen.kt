package com.rohkee.feat.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.rohkee.core.network.model.Display

@Composable
fun LikeRecordScreen(viewModel: LikeRecordViewModel = viewModel()) {
    val likedDisplays by viewModel.likedDisplays.collectAsState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val gridItemSize = (configuration.screenWidthDp.dp - 32.dp - 16.dp) / 3 // 전체 너비에서 패딩 제외하고 3등분

    when {
        likedDisplays.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "좋아요한 디스플레이가 없습니다",
                    color = Color.White,
                )
            }
        }

        else -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(screenHeight - 200.dp) // 상단 프로필 영역과 탭 높이를 제외
                        .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(likedDisplays) { display ->
                    LikedDisplayItem(
                        display = display,
                        modifier = Modifier.size(gridItemSize),
                    )
                }
            }
        }
    }
}

@Composable
private fun LikedDisplayItem(
    display: Display,
    modifier: Modifier = Modifier,
) {
    Image(
        painter =
            rememberImagePainter(
                data = display.thumbnailUrl,
                builder = {
                    crossfade(true)
                },
            ),
        contentDescription = null,
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray),
        // 이미지 로딩 전 배경색
        contentScale = ContentScale.Crop,
    )
}
