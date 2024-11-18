package com.rohkee.core.ui.component.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun CreateDisplayButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = AppColor.Surface,
                    shape = RoundedCornerShape(16.dp),
                ).clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = AppColor.Contrast,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = AppColor.OnContrast,
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "응원 디스플레이 만들기",
                textAlign = TextAlign.Center,
                color = AppColor.OnSurface,
                style = Pretendard.Medium20,
            )
        }
    }
}

@Preview
@Composable
private fun CreateDisplayButtonPreview() {
    CreateDisplayButton()
}
