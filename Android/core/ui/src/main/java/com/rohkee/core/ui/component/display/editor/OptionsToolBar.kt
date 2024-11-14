package com.rohkee.core.ui.component.display.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun OptionsToolBar(
    modifier: Modifier = Modifier,
    title: String,
    onClose: () -> Unit = {},
    options: @Composable (RowScope.() -> Unit) = {},
) {
    Row(
        modifier =
            modifier
                .background(color = AppColor.SurfaceTransparent, shape = CircleShape)
                .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.padding(2.dp).size(24.dp).clickable { onClose() },
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "close tool bar",
            tint = AppColor.OnSurface,
        )
        Text(
            text = title,
            modifier = Modifier,
            color = AppColor.OnSurface,
            style = Pretendard.SemiBold16,
        )
        Spacer(modifier = Modifier)
        options()
    }
}

@Composable
fun OptionsButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    optionsColor: OptionsColor = OptionsButtonDefault.color,
    contentDescription: String? = null,
    onClick: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .background(color = optionsColor.containerColor, shape = CircleShape)
                .padding(6.dp)
                .clickable { onClick() },
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center).size(16.dp),
            painter = icon,
            contentDescription = contentDescription,
            tint = optionsColor.contentColor,
        )
    }
}

object OptionsButtonDefault {
    val color =
        OptionsColor(
            containerColor = AppColor.Contrast,
            contentColor = AppColor.OnContrast,
        )
}

@Immutable
data class OptionsColor(
    val containerColor: Color,
    val contentColor: Color,
)

@Preview
@Composable
private fun OptionsToolBarPreview() {
    OptionsToolBar(title = "색상") {
        OptionsButton(icon = rememberVectorPainter(Icons.Default.KeyboardArrowDown))
        VerticalDivider(modifier = Modifier.height(28.dp))
        OptionsButton(
            icon = rememberVectorPainter(Icons.Outlined.Delete),
            optionsColor =
                OptionsButtonDefault.color.copy(
                    contentColor = AppColor.Warning,
                ),
        )
    }
}

@Preview
@Composable
private fun OptionsButtonPreview() {
    OptionsButton(icon = rememberVectorPainter(Icons.Default.KeyboardArrowDown))
}
