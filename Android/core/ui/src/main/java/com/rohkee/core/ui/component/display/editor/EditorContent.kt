package com.rohkee.core.ui.component.display.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

@Composable
fun EditorContent(modifier: Modifier = Modifier) {
    val backgroundBrush by remember { mutableStateOf<Brush>(Brush.verticalGradient(colors = listOf())) }

    Box(
        modifier = Modifier.background(brush = backgroundBrush),
    ) {
    }
}
