package com.rohkee.core.ui.component.group

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedBlocker(
    modifier: Modifier = Modifier,
    interval: Float,
    offset: Float,
) {
    if (interval > 0f) {
        val infiniteTransition = rememberInfiniteTransition(label = "flicker animation")

        val animatedValue by infiniteTransition.animateFloat(
            initialValue = offset,
            targetValue = if (offset < 1f) 1f else 0f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = interval.toInt(),
                            easing = LinearEasing,
                        ),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "animate offset",
        )

        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = animatedValue)),
        )
    }
}
