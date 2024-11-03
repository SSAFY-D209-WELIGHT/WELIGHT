package com.rohkee.core.ui.component.display.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.model.CustomColor
import com.rohkee.core.ui.model.getBrush
import com.rohkee.core.ui.theme.AppColor

@Composable
fun ColorRow(
    modifier: Modifier = Modifier,
    selectedColor: CustomColor? = null,
    onColorSelected: (CustomColor) -> Unit = {},
    onSelectCustomColor: () -> Unit = {},
) {
    val options =
        remember {
            listOf(
                CustomColor.Single(Color.Red),
                CustomColor.Single(Color.Yellow),
                CustomColor.Single(Color.Green),
                CustomColor.Single(Color.Blue),
                CustomColor.Single(Color.Cyan),
                CustomColor.Single(Color.Magenta),
            )
        }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        for (color in options) {
            ColorChip(
                color = color,
                isSelected = color == selectedColor,
                onClick = { onColorSelected(it!!) },
            )
        }
        ColorChip(
            color = selectedColor,
            isSelected = selectedColor != null,
            onClick = { onSelectCustomColor() },
        )
    }
}

@Composable
fun ColorChip(
    modifier: Modifier = Modifier,
    color: CustomColor?,
    isSelected: Boolean,
    onClick: (color: CustomColor?) -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .size(24.dp)
                .then(
                    if (color == null) {
                        Modifier.background(
                            brush =
                                Brush.sweepGradient(
                                    listOf(
                                        Color.Red,
                                        Color.Green,
                                        Color.Blue,
                                        Color.Yellow,
                                        Color.Red,
                                    ),
                                ),
                            shape = CircleShape,
                        )
                    } else {
                        when(color) {
                            is CustomColor.Single -> Modifier.background(color.color, shape = CircleShape)
                            is CustomColor.Gradient -> {
                                Modifier.background(brush = color.getBrush(), shape = CircleShape)
                            }
                        }
                    },
                ).clickable { onClick(color) }
                .then(
                    if (isSelected) {
                        Modifier.border(width = 2.dp, color = AppColor.Active, shape = CircleShape)
                    } else {
                        Modifier
                    },
                ),
    ) {
        if (isSelected) {
            Icon(
                modifier = Modifier.align(Alignment.Center).padding(4.dp),
                imageVector = Icons.Default.Check,
                contentDescription = "selected",
                tint = AppColor.Active,
            )
        }
    }
}

@Preview
@Composable
private fun ColorRowPreview() {
    ColorRow()
}

@Preview
@Composable
private fun ColorChipPreview() {
    ColorChip(color = CustomColor.Single(Color.Red), isSelected = true)
}

@Preview
@Composable
private fun ColorChipNullPreview() {
    ColorChip(color = null, isSelected = true)
}
