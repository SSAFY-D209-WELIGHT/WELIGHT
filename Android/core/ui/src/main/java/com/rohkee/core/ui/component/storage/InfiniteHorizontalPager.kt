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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor

@Composable
fun InfiniteHorizontalPager(
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    pageCount: Int,
    pageRatio: Float = 0.7f,
    onPageChanged: (Int) -> Unit = {},
    itemContent: @Composable (index: Int) -> Unit,
) {
    if (pageCount == 0) return

    val config = LocalConfiguration.current
    val width = remember { (config.screenWidthDp * pageRatio).dp }
    val padding = remember { (config.screenWidthDp * (1 - pageRatio) / 2f).dp }

    val pagerState =
        rememberPagerState(
            initialPage = initialPage + Int.MAX_VALUE / 2 - ((Int.MAX_VALUE / 2) % pageCount),
            pageCount = { Int.MAX_VALUE },
        )

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageChanged(page)
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        pageSpacing = 8.dp,
        pageSize = PageSize.Fixed(width),
        contentPadding = PaddingValues(horizontal = padding),
    ) { page ->
        val actualPage = page % pageCount
        itemContent(actualPage)
    }
}

@Composable
fun RatioHorizontalPager(
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    pageCount: Int,
    pageRatio: Float = 0.7f,
    onPageChanged: (Int) -> Unit = {},
    itemContent: @Composable (index: Int) -> Unit,
) {
    if (pageCount == 0) return

    val config = LocalConfiguration.current
    val width = remember { (config.screenWidthDp * pageRatio).dp }
    val padding = remember { (config.screenWidthDp * (1 - pageRatio) / 2f).dp }

    val pagerState =
        rememberPagerState(
            initialPage = initialPage,
            pageCount = { pageCount },
        )

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageChanged(page)
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        pageSpacing = 8.dp,
        pageSize = PageSize.Fixed(width),
        contentPadding = PaddingValues(horizontal = padding),
    ) { page ->
        itemContent(page)
    }
}

@Composable
fun PagerIndicator(
    modifier: Modifier = Modifier,
    pageCount: Int,
    currentPage: Int,
) {
    Row(
        modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(pageCount) { index ->
            val color =
                if (currentPage % pageCount == index) AppColor.Active else AppColor.Inactive
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HorizontalViewPreview() {
    InfiniteHorizontalPager(
        pageCount = 0,
        itemContent = {},
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HorizontalViewWithItemPreview() {
    val list =
        remember {
            listOf(
                DisplayCardState(cardId = 0),
                DisplayCardState(cardId = 1),
                DisplayCardState(cardId = 2),
                DisplayCardState(cardId = 3),
            )
        }

    InfiniteHorizontalPager(
        pageCount = list.size,
        itemContent = { index ->
            Box {
                DisplayCard(state = list[index])
                Text(
                    text = "$index",
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                    color = AppColor.OnSurface,
                )
            }
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HorizontalViewWithIndicatorPreview() {
    val list =
        remember {
            listOf(
                DisplayCardState(cardId = 0),
                DisplayCardState(cardId = 1),
                DisplayCardState(cardId = 2),
                DisplayCardState(cardId = 3),
            )
        }

    var currentPage by remember { mutableIntStateOf(0) }

    Column {
        InfiniteHorizontalPager(
            modifier = Modifier.weight(1f),
            initialPage = 0,
            pageCount = list.size,
            onPageChanged = { currentPage = it },
            itemContent = { index ->
                Box {
                    DisplayCard(state = list[index])
                    Text(
                        text = "$index",
                        modifier =
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                        color = AppColor.OnSurface,
                    )
                }
            },
        )
        PagerIndicator(pageCount = list.size, currentPage = currentPage)
    }
}
