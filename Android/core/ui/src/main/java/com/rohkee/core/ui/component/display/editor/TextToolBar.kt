package com.rohkee.core.ui.component.display.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.component.common.ButtonType
import com.rohkee.core.ui.component.common.ChipGroup
import com.rohkee.core.ui.component.common.CommonCircleButton
import com.rohkee.core.ui.theme.AppColor
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class EditorTextState(
    val textState: DisplayTextState,
    val onClose: () -> Unit = {},
    val onSelectColor: (Color) -> Unit = {},
    val onSelectCustomColor: () -> Unit = {},
    val onSelectFont: (FontFamily) -> Unit = {},
    val onRotate: (Float) -> Unit = {},
    val onDelete: () -> Unit = {},
)

@Composable
fun TextToolBar(
    modifier: Modifier = Modifier,
    state: EditorTextState,
) {
    val options = remember { persistentListOf("색상", "폰트", "회전") }
    val (selected, setSelected) = remember { mutableStateOf(options[0]) }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            CommonCircleButton(icon = Icons.Rounded.Close, onClick = state.onClose)
            ChipGroup(
                list = options,
                selected = selected,
                onChipSelected = setSelected,
            )
            CommonCircleButton(
                icon = Icons.Default.Delete,
                buttonType = ButtonType.Warning,
                onClick = state.onDelete,
            )
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
                "색상" ->
                    ColorRow(
                        modifier = Modifier.align(Alignment.Center),
                        selectedColor = state.textState.color,
                        onColorSelected = state.onSelectColor,
                        onSelectCustomColor = state.onSelectCustomColor,
                    )

                "폰트" ->
                    FontRow(
                        modifier = Modifier.align(Alignment.Center),
                        selectedFont = state.textState.font,
                        onFontSelected = state.onSelectFont,
                    )

                "회전" ->
                    SliderRow(
                        modifier = Modifier.align(Alignment.Center),
                        value = state.textState.rotation,
                        onValueChange = state.onRotate,
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
            EditorTextState(
                DisplayTextState(
                    textInfo = "text",
                    rotation = 0f,
                    color = null,
                    font = FontFamily.Default,
                ),
            ),
    )
}
