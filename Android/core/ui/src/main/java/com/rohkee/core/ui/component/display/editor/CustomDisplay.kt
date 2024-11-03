package com.rohkee.core.ui.component.display.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.rohkee.core.ui.model.CustomColor
import com.rohkee.core.ui.model.background
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class DisplayImageState(
    val imageSource: Any?,
    val color: CustomColor?,
    val rotation: Float,
)

@Immutable
data class DisplayTextState(
    val textInfo: String,
    val color: CustomColor?,
    val rotation: Float,
    val font: FontFamily,
)

@Immutable
data class DisplayBackgroundState(
    val color: CustomColor,
    val brightness: Float,
)

@Composable
fun CustomDisplay(
    modifier: Modifier = Modifier,
    backgroundState: DisplayBackgroundState,
    imageState: DisplayImageState,
    textState: DisplayTextState,
) {
    Box(
        modifier = modifier.background(color = backgroundState.color),
    ) {
        DisplayImage(
            modifier = Modifier,
            editorDisplayImageState = imageState,
        )
        DisplayText(
            modifier = Modifier.align(Alignment.Center),
            editorTextState = textState,
        )
    }
}

@Composable
fun DisplayImage(
    modifier: Modifier = Modifier,
    editorDisplayImageState: DisplayImageState,
) {
    AsyncImage(
        modifier = modifier.rotate(editorDisplayImageState.rotation),
        model = editorDisplayImageState.imageSource,
        contentDescription = "image",
    )
}

@Composable
fun DisplayText(
    modifier: Modifier = Modifier,
    editorTextState: DisplayTextState,
) {
    Text(
        modifier = modifier.rotate(editorTextState.rotation),
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
                rotation = 0f,
                color = null,
            ),
        textState =
            DisplayTextState(
                textInfo = "text",
                rotation = 0f,
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
