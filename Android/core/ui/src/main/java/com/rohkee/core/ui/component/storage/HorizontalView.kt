package com.rohkee.core.ui.component.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor

@Composable
fun HorizontalView(
    modifier: Modifier = Modifier,
    list: List<DisplayCardState> = emptyList(),
) {
    val config = LocalConfiguration.current
    val width = remember { (config.screenWidthDp * 0.7).dp }
    val padding = remember { (config.screenWidthDp * 0.15).dp }

    if (list.isEmpty()) {
    } else {
        val pagerState =
            rememberPagerState(
                initialPage = Int.MAX_VALUE / 2 - ((Int.MAX_VALUE / 2) % list.size),
                pageCount = { Int.MAX_VALUE },
            )

        Column {
            HorizontalPager(
                modifier = modifier.weight(1f),
                state = pagerState,
                pageSpacing = 8.dp,
                pageSize = PageSize.Fixed(width),
                contentPadding = PaddingValues(horizontal = padding),
            ) { page ->
                val actualPage = page % list.size
                Box {
                    DisplayCard(state = list[actualPage])
                    Text(
                        text = "$actualPage",
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                        color = AppColor.OnSurface,
                    )
                }
            }
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(list.size) { iteration ->
                    val color =
                        if (pagerState.currentPage % list.size == iteration) AppColor.Active else AppColor.Inactive
                    Box(
                        modifier =
                            Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HorizontalViewPreview() {
    HorizontalView()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HorizontalViewWithItemPreview() {
    HorizontalView(
        list =
            listOf(
                DisplayCardState(cardId = 0),
                DisplayCardState(cardId = 1),
                DisplayCardState(cardId = 2),
            ),
    )
}
