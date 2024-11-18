package com.rohkee.core.ui.component.display.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.R
import com.rohkee.core.ui.model.CustomColor
import com.rohkee.core.ui.theme.AppColor
import kotlinx.collections.immutable.persistentListOf

private enum class TextOptions(
    val icon: Int,
) {
    Color(R.drawable.ic_color_paint),
    Font(R.drawable.ic_text_font),
}

@Composable
fun TextToolBar(
    modifier: Modifier = Modifier,
    state: DisplayTextState,
    onClose: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSelectColor: (CustomColor) -> Unit = {},
    onSelectCustomColor: (CustomColor) -> Unit = {},
    onSelectFont: (FontFamily) -> Unit = {},
    onTextChange: () -> Unit = {},
) {
    val (selected, setSelected) = remember { mutableStateOf(TextOptions.Color) }

    Column(
        modifier = modifier,
    ) {
        OptionsToolBar(
            modifier = Modifier.padding(8.dp),
            title = "텍스트",
            onClose = onClose,
        ) {
            OptionsButton(
                containerColor = AppColor.Contrast,
            ) {
                ColorChip(
                    modifier = Modifier.size(20.dp),
                    color = state.color,
                    isSelected = false,
                    onClick =
                        { setSelected(TextOptions.Color) },
                )
            }
            OptionsButton(
                icon = painterResource(TextOptions.Font.icon),
            ) { setSelected(TextOptions.Font) }
            OptionsButton(
                icon = painterResource(R.drawable.ic_text_edit),
            ) { onTextChange() }
            VerticalDivider(modifier = Modifier.height(32.dp), color = AppColor.Contrast)
            OptionsButton(
                icon = rememberVectorPainter(Icons.Outlined.Delete),
                optionsColor =
                    OptionsButtonDefault.color.copy(
                        contentColor = AppColor.Warning,
                    ),
            ) { onDelete() }
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        AppColor.SurfaceTransparent,
                                        AppColor.BackgroundTransparent,
                                    ),
                            ),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    ).padding(horizontal = 16.dp),
        ) {
            when (selected) {
                TextOptions.Color ->
                    ColorRow(
                        modifier = Modifier.align(Alignment.Center),
                        selectedColor = state.color,
                        additionalColors = persistentListOf(CustomColor.Single(color = Color.Black)),
                        onColorSelected = onSelectColor,
                        onSelectCustomColor = { onSelectCustomColor(state.color) },
                    )

                TextOptions.Font ->
                    FontRow(
                        modifier = Modifier.align(Alignment.Center),
                        selectedFont = state.font,
                        onFontSelected = onSelectFont,
                    )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TextToolBarPreview() {
    TextToolBar(
        state =
            DisplayTextState(
                text = "text",
            ),
    )
}
