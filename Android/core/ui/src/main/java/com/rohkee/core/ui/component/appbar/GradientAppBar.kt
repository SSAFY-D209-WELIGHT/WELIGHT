package com.rohkee.core.ui.component.appbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun GradientAppBar(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = Icons.Rounded.Close,
    onClick: () -> Unit = {},
    content: @Composable (RowScope.() -> Unit),
) {
    Row(
        modifier =
            modifier.height(52.dp).background(
                brush =
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                AppColor.BackgroundTransparent,
                                AppColor.SurfaceTransparent,
                            ),
                    ),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            modifier = Modifier.size(32.dp).clickable { onClick() },
            imageVector = imageVector,
            contentDescription = "Close",
            tint = AppColor.OnBackground,
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.wrapContentHeight()) {
            content()
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
fun SavableAppBar(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
) {
    GradientAppBar(
        modifier = modifier,
        onClick = onCloseClick,
    ) {
        Text(
            "저장",
            modifier = Modifier.clickable { onSaveClick() },
            style = Pretendard.Medium16,
            color = AppColor.OnBackground,
        )
    }
}

@Composable
fun ConfirmAppBar(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) {
    GradientAppBar(
        modifier = modifier,
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        onClick = onCloseClick,
    ) {
        Text(
            "완료",
            modifier = Modifier.clickable { onConfirmClick() },
            style = Pretendard.Medium16,
            color = AppColor.OnBackground,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GradientAppBarPreview() {
    GradientAppBar {}
}

@Preview(showBackground = true)
@Composable
private fun SavableAppBarPreview() {
    SavableAppBar()
}

@Preview(showBackground = true)
@Composable
private fun ConfirmAppBarPreview() {
    ConfirmAppBar()
}
