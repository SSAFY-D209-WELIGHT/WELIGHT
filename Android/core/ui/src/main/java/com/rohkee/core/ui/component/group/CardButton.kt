package com.rohkee.core.ui.component.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    description: String,
    onClick: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .background(
                    color = AppColor.Surface,
                    shape = RoundedCornerShape(16.dp),
                ).padding(16.dp)
                .clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .background(color = AppColor.Contrast, shape = CircleShape)
                    .padding(12.dp),
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = icon,
                contentDescription = "icon",
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    style = Pretendard.SemiBold20,
                    color = AppColor.OnSurface,
                )
            }
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    style = Pretendard.Regular14,
                    color = AppColor.OnSurface,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CardButtonPreview() {
    CardButton(
        icon = rememberVectorPainter(Icons.Default.AccountCircle),
        title = "제목",
        description = "설명",
    )
}
