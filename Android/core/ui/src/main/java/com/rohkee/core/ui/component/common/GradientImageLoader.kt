package com.rohkee.core.ui.component.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground

@Composable
fun GradientImageLoader(
    modifier: Modifier = Modifier,
    imageSource: Any?,
) {
    var imageState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = imageSource,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            onLoading = { imageState = it },
            onSuccess = { imageState = it },
            onError = { imageState = it },
        )
        when (imageState) {
            is AsyncImagePainter.State.Success -> {}
            else -> {
                Box(
                    modifier =
                        Modifier.fillMaxSize().animateGradientBackground(
                            startColor = AppColor.LoadLight,
                            endColor = AppColor.LoadDark,
                        ),
                ) {
                }
            }
        }
    }
}
