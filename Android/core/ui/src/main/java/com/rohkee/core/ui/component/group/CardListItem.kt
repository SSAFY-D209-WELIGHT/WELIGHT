package com.rohkee.core.ui.component.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun CardListItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    number: Int,
    onJoinClick: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .background(color = AppColor.Surface, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(3f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = title,
                style = Pretendard.SemiBold20,
                color = AppColor.OnSurface,
            )
            Text(
                text = description,
                style = Pretendard.Regular14,
                color = AppColor.OnSurface,
            )
        }
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .background(
                        color = AppColor.Contrast,
                        shape = RoundedCornerShape(4.dp),
                    ).padding(vertical = 8.dp, horizontal = 16.dp)
                    .clickable { onJoinClick() },
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("함께하기", style = Pretendard.SemiBold16, color = AppColor.OnContrast)
                GroupSizeChip(
                    modifier = Modifier.fillMaxWidth(),
                    number = number,
                )
            }
        }
    }
}

@Preview
@Composable
fun CardListItemPreview() {
    CardListItem(number = 3, title = "제목", description = "설명")
}
