package com.rohkee.core.ui.component.display

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun TitleRow(
    modifier: Modifier = Modifier,
    title: String,
    editable: Boolean = true,
    onEditClick: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { onEditClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Text(
            text = title.ifEmpty { "이름을 입력해주세요." },
            style = Pretendard.Medium24,
            color = if (title.isNotEmpty()) AppColor.OnSurface else AppColor.OnBackgroundTransparent,
            modifier = Modifier.weight(1f),
        )
        if (editable) {
            Icon(
                modifier = Modifier,
                imageVector = Icons.Rounded.Edit,
                tint = AppColor.OnSurface,
                contentDescription = "Edit",
            )
        }
    }
}
