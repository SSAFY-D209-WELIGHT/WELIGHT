package com.rohkee.core.ui.component.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagRow(
    modifier: Modifier = Modifier,
    tags: List<String>,
) {
    if (tags.isNotEmpty()) {
        FlowRow(
            modifier =
                modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (tag in tags) {
                Tag(tag = tag)
            }
        }
    } else {
        Text(
            modifier =
                modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            text = "#태그를_입력해주세요.",
            style = Pretendard.SemiBold16,
            color = AppColor.OnBackgroundTransparent,
        )
    }
}

@Composable
private fun Tag(
    modifier: Modifier = Modifier,
    tag: String,
) {
    Text(
        modifier = modifier,
        text = "#$tag",
        style = Pretendard.SemiBold16,
        color = AppColor.Convex,
    )
}
