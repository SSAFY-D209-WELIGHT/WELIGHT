package com.rohkee.feat.mypage.cheer_record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.component.storage.DisplayCard
import com.rohkee.core.ui.component.storage.DisplayCardState
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Immutable
data class CheerRecordCardState(
    val participationDate: String, // 참여 날짜
    val cheerRoomName: String, // 공연명
    val participantCount: Int, // 참여 인원
    val memo: String, // 메모
    val thumbnailUrl: String?,
)

@Composable
fun CheerRecordCard(
    modifier: Modifier = Modifier,
    state: CheerRecordCardState,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(color = AppColor.Surface, RoundedCornerShape(8.dp))
                .padding(8.dp),
    ) {
        DisplayCard(
            modifier = Modifier.aspectRatio(0.5f),
            state = DisplayCardState(imageSource = state.thumbnailUrl),
        )

        // 정보 영역
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            // Fill vertical space
            verticalArrangement =
                Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterVertically,
                ),
            // Center items vertically
            horizontalAlignment = Alignment.CenterHorizontally, // Center items horizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp)) // Add top space

            Text(
                text = state.participationDate,
                style = Pretendard.Regular14,
                color = AppColor.OnSurface,
            )
            Text(
                text = state.cheerRoomName,
                style = Pretendard.SemiBold16,
                color = AppColor.OnSurface,
            )
            Text(
                text = "${state.participantCount}명 참여 중",
                style = Pretendard.Regular14,
                color = AppColor.OverSurface,
            )
            Text(
                text = state.memo,
                style = Pretendard.Regular14,
                color = AppColor.OverSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
