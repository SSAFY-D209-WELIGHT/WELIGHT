package com.rohkee.core.ui.component.display.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.rohkee.core.ui.component.common.TransformableBox
import com.rohkee.core.ui.model.CustomColor
import com.rohkee.core.ui.model.background
import com.rohkee.core.ui.theme.pretendardFamily
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class DisplayImageState(
    val isSelected: Boolean = false,
    val imageSource: Any? = null,
    val color: CustomColor = CustomColor.Single(color = Color.Transparent),
    val scale: Float = 1f,
    val rotationDegree: Float = 0f,
    val offsetPercentX: Float = 0f,
    val offsetPercentY: Float = 0f,
)

@Immutable
data class DisplayTextState(
    val isSelected: Boolean = false,
    val text: String = "",
    val color: CustomColor = CustomColor.Single(color = Color.Black),
    val font: FontFamily = pretendardFamily,
    val scale: Float = 1f,
    val rotationDegree: Float = 0f,
    val offsetPercentX: Float = 0f,
    val offsetPercentY: Float = 0f,
)

@Immutable
data class DisplayBackgroundState(
    val color: CustomColor = CustomColor.Single(color = Color.Black),
    val brightness: Float = 1f,
)

@Composable
fun CustomDisplay(
    modifier: Modifier = Modifier,
    editable: Boolean = false,
    backgroundState: DisplayBackgroundState,
    imageState: DisplayImageState,
    textState: DisplayTextState,
    onImageSelected: () -> Unit = {},
    onTextSelected: () -> Unit = {},
    onImageTransformed: (DisplayImageState) -> Unit = {},
    onTextTransformed: (DisplayTextState) -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = backgroundState.color),
    ) {
        // 이미지
        TransformableBox(
            scale = imageState.scale,
            rotation = imageState.rotationDegree,
            offset = Offset(imageState.offsetPercentX, imageState.offsetPercentY),
            selected = imageState.isSelected,
            onSelect = { if (editable) onImageSelected() },
            onTransfrm =
                { scale, rotation, offset ->
                    onImageTransformed(
                        imageState.copy(
                            scale = scale,
                            rotationDegree = rotation,
                            offsetPercentX = offset.x,
                            offsetPercentY = offset.y,
                        ),
                    )
                },
        ) { childMod ->
            DisplayImage(
                modifier = childMod.align(Alignment.Center),
                state = imageState,
            )
        }
        TransformableBox(
            scale = textState.scale,
            rotation = textState.rotationDegree,
            offset = Offset(textState.offsetPercentX, textState.offsetPercentY),
            selected = textState.isSelected,
            onSelect = { if (editable) onTextSelected() },
            onTransfrm =
                { scale, rotation, offset ->
                    onTextTransformed(
                        textState.copy(
                            scale = scale,
                            rotationDegree = rotation,
                            offsetPercentX = offset.x,
                            offsetPercentY = offset.y,
                        ),
                    )
                },
        ) { childMod ->
            DisplayText(
                modifier = childMod.align(Alignment.Center),
                editorTextState = textState,
            )
        }
    }
}

@Composable
fun DisplayImage(
    modifier: Modifier = Modifier,
    state: DisplayImageState,
) {
    AsyncImage(
        modifier =
            modifier,
        model = state.imageSource,
        contentScale = ContentScale.Inside,
        contentDescription = "image",
        colorFilter =
            ColorFilter.tint(
                color =
                    when (state.color) {
                        is CustomColor.Single -> state.color.color
                        is CustomColor.Gradient -> state.color.colors.first()
                    },
                blendMode = BlendMode.Plus,
            ),
    )
}

@Composable
fun DisplayText(
    modifier: Modifier = Modifier,
    editorTextState: DisplayTextState,
) {
    Text(
        modifier =
            modifier,
        text = editorTextState.text,
        fontFamily = editorTextState.font
    )
}

@Preview(showBackground = true)
@Composable
private fun DisplayPreview() {
    CustomDisplay(
        imageState =
            DisplayImageState(),
        textState =
            DisplayTextState(
                text = "text",
            ),
        backgroundState =
            DisplayBackgroundState(
                color = CustomColor.Gradient(persistentListOf(Color.White, Color.Black)),
                brightness = 0f,
            ),
    )
}
