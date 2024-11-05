package com.rohkee.core.ui.component.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun PlainButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors =
            ButtonColors(
                containerColor = AppColor.Convex,
                contentColor = AppColor.OnConvex,
                disabledContainerColor = AppColor.Inactive,
                disabledContentColor = AppColor.Surface,
            ),
    ) {
        Text(
            text = text,
            style = Pretendard.SemiBold16,
        )
    }
}

@Composable
fun OutlinedButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border =
            BorderStroke(
                width = 2.dp,
                color = if (enabled) AppColor.Convex else AppColor.Inactive,
            ),
        colors =
            ButtonColors(
                containerColor = AppColor.Surface,
                contentColor = AppColor.Convex,
                disabledContainerColor = AppColor.Surface,
                disabledContentColor = AppColor.Inactive,
            ),
    ) {
        Text(
            text = text,
            style = Pretendard.SemiBold16,
        )
    }
}

@Composable
fun WarningButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    icon: ImageVector,
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors =
            ButtonColors(
                containerColor = AppColor.Warning,
                contentColor = AppColor.OnSurface,
                disabledContainerColor = AppColor.Warning.copy(alpha = 0.5f),
                disabledContentColor = AppColor.Inactive,
            ),
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = icon,
                contentDescription = "",
            )
            Text(
                modifier = Modifier,
                text = text,
                style = Pretendard.SemiBold16,
            )
        }
    }
}

@Preview
@Composable
private fun PlainButtonPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PlainButton(text = "테스트")
        PlainButton(text = "테스트", enabled = false)
    }
}

@Preview
@Composable
private fun OutlinedButtonPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(text = "테스트")
        OutlinedButton(text = "테스트", enabled = false)
    }
}

@Preview
@Composable
private fun WarningButtonPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        WarningButton(text = "테스트", icon = Icons.Default.Warning)
        WarningButton(text = "테스트", icon = Icons.Default.Warning, enabled = false)
    }
}
