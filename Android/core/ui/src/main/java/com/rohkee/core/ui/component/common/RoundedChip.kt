package com.rohkee.core.ui.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun RoundedChip(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    text: String,
    onclick: () -> Unit = {},
) {
    val backgroundColor by
        remember(isSelected) { mutableStateOf(if (isSelected) AppColor.Contrast else AppColor.SurfaceTransparent) }
    val fontColor by
        remember(isSelected) { mutableStateOf(if (isSelected) AppColor.OnContrast else AppColor.OnSurface) }

    Box(
        modifier =
            modifier
                .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
                .clickable { onclick() },
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center).padding(vertical = 6.dp, horizontal = 8.dp),
            text = text,
            style = Pretendard.SemiBold12,
            color = fontColor,
        )
    }
}

@Preview
@Composable
private fun RoundedChipPreview() {
    RoundedChip(isSelected = true, text = "테스트")
}

@Preview(showBackground = true)
@Composable
private fun RoundedChipPreviewUnselected() {
    RoundedChip(isSelected = false, text = "테스트")
}
