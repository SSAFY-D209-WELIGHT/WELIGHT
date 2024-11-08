package com.rohkee.core.ui.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor

@Immutable
sealed interface ButtonType {
    @Immutable
    data object Transparent : ButtonType

    @Immutable
    data object Warning : ButtonType
}

@Composable
fun CommonCircleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    icon: ImageVector,
    contentDescription: String? = null,
    buttonType: ButtonType = ButtonType.Transparent,
) {
    CommonCircleButton(
        modifier = modifier,
        onClick = onClick,
        painter = rememberVectorPainter(image = icon),
        contentDescription = contentDescription,
        buttonType = buttonType,
    )
}

@Composable
fun CommonCircleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    painter: Painter,
    contentDescription: String? = null,
    buttonType: ButtonType = ButtonType.Transparent,
) {
    val backgroundColor =
        remember(buttonType) {
            when (buttonType) {
                ButtonType.Transparent -> AppColor.SurfaceTransparent
                ButtonType.Warning -> AppColor.Contrast
            }
        }
    val contentColor =
        remember(buttonType) {
            when (buttonType) {
                ButtonType.Transparent -> AppColor.OnSurface
                ButtonType.Warning -> AppColor.Warning
            }
        }

    Box(
        modifier =
            modifier
                .background(color = backgroundColor, shape = CircleShape)
                .clickable { onClick() },
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center).padding(8.dp).size(24.dp),
            painter = painter,
            contentDescription = contentDescription,
            tint = contentColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TransparentCircleButtonPreview() {
    CommonCircleButton(icon = Icons.Rounded.Close)
}

@Preview(showBackground = true)
@Composable
private fun WarningCircleButtonPreview() {
    CommonCircleButton(buttonType = ButtonType.Warning, icon = Icons.Rounded.Warning)
}
