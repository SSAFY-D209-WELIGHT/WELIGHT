package com.rohkee.core.ui.component

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalView(modifier: Modifier = Modifier) {
    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = { 10 },
        )

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        pageSize = PageSize.Fixed(100.dp)
    ) { page ->
        Text("$page")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HorizontalViewPreview() {
    HorizontalView()
}
