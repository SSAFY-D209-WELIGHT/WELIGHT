package com.rohkee.core.ui.component.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard
import com.rohkee.core.ui.util.animateGradientBackground

@Immutable
data class CardListItemState(
    val id: Long,
    val title: String,
    val description: String,
    val number: Int,
)

@Composable
fun CardListItem(
    modifier: Modifier = Modifier,
    state: CardListItemState,
    onJoinClick: (id: Long) -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .height(96.dp)
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
                text = state.title,
                style = Pretendard.SemiBold20,
                color = AppColor.OnSurface,
            )
            Text(
                text = state.description,
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
                    .clickable { onJoinClick(state.id) },
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("함께하기", style = Pretendard.SemiBold16, color = AppColor.OnContrast)
                GroupSizeChip(
                    modifier = Modifier.fillMaxWidth(),
                    number = state.number,
                )
            }
        }
    }
}

@Composable
fun LoadingListItem(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .height(96.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(3f).padding(end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Box(
                modifier =
                    Modifier.fillMaxWidth().weight(3f).animateGradientBackground(
                        startColor = AppColor.LoadLight,
                        endColor = AppColor.LoadDark,
                        shape = RoundedCornerShape(4.dp)
                    ),
            )
            Box(
                modifier =
                    Modifier
                        .padding(end = 16.dp)
                        .fillMaxWidth()
                        .weight(2f)
                        .animateGradientBackground(
                            startColor = AppColor.LoadLight,
                            endColor = AppColor.LoadDark,
                            shape = RoundedCornerShape(4.dp)
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .padding(end = 32.dp)
                        .fillMaxWidth()
                        .weight(2f)
                        .animateGradientBackground(
                            startColor = AppColor.LoadLight,
                            endColor = AppColor.LoadDark,
                            shape = RoundedCornerShape(4.dp)
                        ),
            )
        }
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .animateGradientBackground(
                        startColor = AppColor.LoadLight,
                        endColor = AppColor.LoadDark,
                        shape = RoundedCornerShape(4.dp)
                    ),
        ) {
        }
    }
}

@Preview
@Composable
private fun CardListItemPreview() {
    CardListItem(state = CardListItemState(1, "title", "description", 10))
}

@Preview
@Composable
private fun LoadingListItemPreview() {
    LoadingListItem()
}
