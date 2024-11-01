package com.rohkee.core.ui.component.display.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush


@Composable
fun EditorContent(
    modifier: Modifier = Modifier,
    editorDisplayImageState: DisplayImageState,
    editorTextState: DisplayTextState,
    backgroundState: DisplayBackgroundState,
) {
    val backgroundBrush by remember { mutableStateOf(Brush.verticalGradient(colors = backgroundState.colors)) }

    CustomDisplay(
        modifier = modifier,
        imageState = editorDisplayImageState,
        textState = editorTextState,
        backgroundState = backgroundState,
    )
}
