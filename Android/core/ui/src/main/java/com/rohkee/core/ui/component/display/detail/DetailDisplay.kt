package com.rohkee.core.ui.component.display.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun DetailDisplay(
    modifier: Modifier = Modifier,
    imageSource: Any?,
) {
    AsyncImage(
        modifier = modifier.fillMaxSize(),
        model = imageSource,
        contentDescription = "display",
        contentScale = ContentScale.Fit,
    )
}
