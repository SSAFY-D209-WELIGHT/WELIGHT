package com.rohkee.core.ui.component.display.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderRow(
    modifier: Modifier,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Slider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        colors =
            SliderDefaults.colors(
                activeTrackColor = AppColor.Active,
                inactiveTrackColor = AppColor.Active,
            ),
        thumb = {
            Spacer(
                modifier =
                    Modifier
                        .size(16.dp)
                        .background(color = AppColor.OnContrast, shape = CircleShape)
                        .border(color = AppColor.Contrast, width = 2.dp, shape = CircleShape)
                        .shadow(4.dp, shape = CircleShape),
            )
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0)
@Composable
private fun SliderRowPreview() {
    var value by remember { mutableFloatStateOf(0.5f) }

    SliderRow(
        modifier = Modifier,
        value = value,
        onValueChange = { value = it },
    )
}
