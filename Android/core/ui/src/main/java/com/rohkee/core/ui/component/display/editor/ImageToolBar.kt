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
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.component.common.ButtonType
import com.rohkee.core.ui.component.common.ChipGroup
import com.rohkee.core.ui.component.common.CommonCircleButton
import com.rohkee.core.ui.model.CustomColor
import com.rohkee.core.ui.theme.AppColor
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class EditorImageState(
    val imageState: DisplayImageState,
    val onClose: () -> Unit = {},
    val onDelete: () -> Unit = {},
    val onSelectColor: (CustomColor) -> Unit = {},
    val onSelectCustomColor: () -> Unit = {},
    val onRotate: (Float) -> Unit = {},
    val onChangeImage: () -> Unit = {},
)

@Composable
fun ImageToolBar(
    modifier: Modifier = Modifier,
    state: EditorImageState,
) {
    val options = remember { persistentListOf("색상", "회전", "교체") }
    val (selected, setSelected) = remember { mutableStateOf(options[0]) }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            CommonCircleButton(icon = Icons.Rounded.Close, onClick = state.onClose)
            ChipGroup(
                list = options,
                selected = selected,
                onChipSelected = { value ->
                    if (value == "교체") {
                        state.onChangeImage()
                    } else {
                        setSelected(value)
                    }
                },
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
                        selectedColor = state.imageState.color,
                        onColorSelected = state.onSelectColor,
                        onSelectCustomColor = state.onSelectCustomColor,
                    )

                "회전" ->
                    SliderRow(
                        modifier = Modifier.align(Alignment.Center),
                        value = state.imageState.rotation,
                        onValueChange = state.onRotate,
                    )
            }
        }
    }
}
