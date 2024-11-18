package com.rohkee.core.ui.component.display.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.pretendardFamily

@Composable
fun FontRow(
    modifier: Modifier = Modifier,
    selectedFont: FontFamily? = null,
    onFontSelected: (FontFamily) -> Unit,
) {
    val options =
        remember {
            listOf(
                pretendardFamily,
                FontFamily.Default,
                FontFamily.SansSerif,
                FontFamily.Serif,
            )
        }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        for (font in options) {
            FontChip(
                font = font,
                isSelected = font == selectedFont,
                onClick = { onFontSelected(it) },
            )
        }
    }
}

@Composable
fun FontChip(
    modifier: Modifier = Modifier,
    font: FontFamily,
    isSelected: Boolean,
    onClick: (FontFamily) -> Unit,
) {
    Box(
        modifier =
            modifier
                .size(24.dp)
                .background(color = AppColor.Surface, shape = CircleShape)
                .clickable { onClick(font) }
                .then(
                    if (isSelected) {
                        Modifier.border(width = 2.dp, color = AppColor.Active, shape = CircleShape)
                    } else {
                        Modifier
                    },
                ),
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center).fillMaxSize(),
            text = "A",
            fontSize = 20.sp,
            fontFamily = font,
            textAlign = TextAlign.Center,
            color = AppColor.OnSurface,
        )
    }
}

@Preview
@Composable
private fun FontRowPreview() {
    val (selectedFont, setFont) = remember { mutableStateOf<FontFamily>(FontFamily.Default) }

    FontRow(selectedFont = selectedFont, onFontSelected = setFont)
}

@Preview
@Composable
private fun FontChipPreview() {
    FontChip(font = FontFamily.Default, isSelected = true, onClick = {})
}
