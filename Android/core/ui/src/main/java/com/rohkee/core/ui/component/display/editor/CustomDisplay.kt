package com.rohkee.core.ui.component.display.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import coil.compose.AsyncImage
import com.rohkee.core.ui.component.common.TransformableBox
import com.rohkee.core.ui.model.CustomColor
import com.rohkee.core.ui.model.background
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class DisplayImageState(
    val imageSource: Any?,
    val color: CustomColor?,
    val scale: Float = 1f,
    val rotationDegree: Float = 0f,
    val offsetPercentX: Float = 0f,
    val offsetPercentY: Float = 0f,
)

@Immutable
data class DisplayTextState(
    val textInfo: String,
    val color: CustomColor?,
    val font: FontFamily,
    val scale: Float = 1f,
    val rotationDegree: Float = 0f,
    val offsetPercentX: Float = 0f,
    val offsetPercentY: Float = 0f,
)

@Immutable
data class DisplayBackgroundState(
    val color: CustomColor,
    val brightness: Float,
)

@Composable
fun CustomDisplay(
    modifier: Modifier = Modifier,
    editable: Boolean = false,
    backgroundState: DisplayBackgroundState,
    imageState: DisplayImageState,
    textState: DisplayTextState,
    onImageTransformed: (DisplayImageState) -> Unit = {},
    onTextTransformed: (DisplayTextState) -> Unit = {},
) {
    Box(
        modifier = modifier.background(color = backgroundState.color),
    ) {
        TransformableBox(
            modifier = Modifier,
            scale = imageState.scale,
            rotation = imageState.rotationDegree,
            offset = Offset(imageState.offsetPercentX, imageState.offsetPercentY),
            onTransfrm =
                if (editable) {
                    { scale, rotation, offset ->
                        onImageTransformed(
                            imageState.copy(
                                scale = scale,
                                rotationDegree = rotation,
                                offsetPercentX = offset.x,
                                offsetPercentY = offset.y,
                            ),
                        )
                    }
                } else {
                    null
                },
        ) {
            DisplayImage(
                modifier = Modifier,
                editorDisplayImageState = imageState,
            )
        }
        TransformableBox(
            modifier = Modifier,
            scale = textState.scale,
            rotation = textState.rotationDegree,
            offset = Offset(textState.offsetPercentX, textState.offsetPercentY),
            onTransfrm =
                if (editable) {
                    { scale, rotation, offset ->
                        onTextTransformed(
                            textState.copy(
                                scale = scale,
                                rotationDegree = rotation,
                                offsetPercentX = offset.x,
                                offsetPercentY = offset.y,
                            ),
                        )
                    }
                } else {
                    null
                },
        ) {
            DisplayText(
                modifier = Modifier,
                editorTextState = textState,
            )
        }
    }
}

@Composable
fun DisplayImage(
    modifier: Modifier = Modifier,
    editorDisplayImageState: DisplayImageState,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    AsyncImage(
        modifier =
            modifier
                .offset(
                    x = editorDisplayImageState.offsetPercentX * screenWidth,
                    y = editorDisplayImageState.offsetPercentY * screenHeight,
                ).rotate(editorDisplayImageState.rotationDegree),
        model = editorDisplayImageState.imageSource,
        contentDescription = "image",
    )
}

@Composable
fun DisplayText(
    modifier: Modifier = Modifier,
    editorTextState: DisplayTextState,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Text(
        modifier =
            modifier
                .offset(
                    x = editorTextState.offsetPercentX * screenWidth,
                    y = editorTextState.offsetPercentY * screenHeight,
                ).rotate(editorTextState.rotationDegree),
        text = editorTextState.textInfo,
        fontFamily = editorTextState.font,
    )
}

@Preview(showBackground = true)
@Composable
private fun DisplayPreview() {
    CustomDisplay(
        imageState =
            DisplayImageState(
                imageSource = null,
                rotationDegree = 0f,
                color = null,
            ),
        textState =
            DisplayTextState(
                textInfo = "text",
                rotationDegree = 90f,
                offsetPercentX = 0.5f,
                offsetPercentY = 0.5f,
                color = null,
                font = FontFamily.Default,
            ),
        backgroundState =
            DisplayBackgroundState(
                color = CustomColor.Gradient(persistentListOf(Color.White, Color.Black)),
                brightness = 0f,
            ),
    )
}
