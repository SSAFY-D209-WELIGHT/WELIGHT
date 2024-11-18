package com.rohkee.core.ui.component.appbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun TitleAppBar(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable (RowScope.() -> Unit) = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(color = AppColor.Background)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = Pretendard.SemiBold24, color = AppColor.OnBackground)
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun TitleAppBarPreview() {
    TitleAppBar(title = "Title") {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = Icons.Filled.Search,
            contentDescription = "검색",
            tint = Color.Gray,
        )
    }
}
