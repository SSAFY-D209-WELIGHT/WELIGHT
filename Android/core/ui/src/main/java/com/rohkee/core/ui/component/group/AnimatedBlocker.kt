package com.rohkee.core.ui.component.group

import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlin.random.Random

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
        // Create an Animatable for alpha value (from 0f to 1f)
        val alphaValue = remember { Animatable(offset) }

        var currentInterval by remember(interval) { mutableStateOf(interval.toInt()) }

        LaunchedEffect(currentInterval) {
            while (true) {
                // Animate from 1f to 0f (fade out)
                alphaValue.animateTo(
                    targetValue = 0f,
                    animationSpec =
                        tween(
                            durationMillis = currentInterval / 2,
                            easing = LinearEasing,
                        ),
                )
                // Animate from 0f to 1f (fade in)
                alphaValue.animateTo(
                    targetValue = 1f,
                    animationSpec =
                        tween(
                            durationMillis = currentInterval / 2,
                            easing = LinearEasing,
                        ),
                )
            }
        }

        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = alphaValue.value)),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedBlockerPreview() {
    var interval by remember { mutableStateOf(200f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            interval = 100f + Random.nextFloat() * 900f
        }
    }

    AnimatedBlocker(
        interval = interval,
        offset = 0.5f,
    )
}
