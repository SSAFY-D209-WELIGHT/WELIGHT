package com.rohkee.feat.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.rohkee.core.network.model.CheerRecord

@Composable
fun CheerRecordScreen(viewModel: CheerRecordViewModel = hiltViewModel()) {
    val cheerRecords by viewModel.cheerRecords.collectAsState()

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(cheerRecords) { record ->
            CheerRecordCard(record)
        }
    }
}

@Composable
private fun CheerRecordCard(record: CheerRecord) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 썸네일 이미지
        Image(
            painter =
                rememberImagePainter(
                    data = record.displays.firstOrNull()?.thumbnailUrl ?: "",
                    builder = {
                        crossfade(true)
                    },
                ),
            contentDescription = "Thumbnail",
            modifier =
                Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .padding(start = 10.dp)
                    .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
        )

        // 정보 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), // Fill vertical space
            verticalArrangement = Arrangement.Center, // Center items vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center items horizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp)) // Add top space

            Text(
                text = record.participationDate,
                color = Color.White,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(10.dp)) // Add space between items

            Text(
                text = record.cheerroomName,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp)) // Add space between items

            Text(
                text = "${record.participantCount}명 참여 중",
                color = Color.LightGray,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(10.dp)) // Add space between items

            Text(
                text = record.memo,
                color = Color.LightGray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(10.dp)) // Add bottom space
        }
    }
}
