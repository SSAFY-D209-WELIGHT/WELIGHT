package com.rohkee.core.ui.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.animateGradientBackground(
    startColor: Color,
    endColor: Color,
    animationDuration: Int = 3000,
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "linear gradient transition")

    var size by remember { mutableStateOf(IntSize.Zero) }
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = animationDuration,
                        easing = LinearEasing,
                    ),
            ),
        label = "animate offset",
    )

    return this
        .onSizeChanged { size = it }
        .background(
            brush =
                Brush.linearGradient(
                    colors =
                        listOf(
                            startColor,
                            endColor,
                        ),
                    start = Offset(offset * (size.width * 2), 0f),
                    end = Offset((offset - 1) * (size.width * 2), 0f),
                    tileMode = TileMode.Mirror,
                ),
        )
}

fun Modifier.dashedBorder(
    shape: Shape,
    width: Dp = 1.dp,
    color: Color = Color.Black,
    dashWidth: Float = 10f,
    gapWidth: Float = 10f,
) = this.drawWithContent {
    drawContent()

    val outline: Outline = shape.createOutline(
        size = Size(size.width, size.height),
        layoutDirection = LayoutDirection.Ltr,
        density = this
    )

    val path = Path().apply {
        when (outline) {
            is Outline.Rectangle -> {
                addRect(outline.rect)
            }
            is Outline.Rounded -> {
                addRoundRect(outline.roundRect)
            }
            is Outline.Generic -> {
                addPath(outline.path)
            }
        }
    }

    val strokeWidth = width.toPx()
    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(dashWidth, gapWidth),
        phase = 0f
    )

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            pathEffect = pathEffect
        )
    )
}

@Preview
@Composable
fun AnimatedGradientBackgroundPreview() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .animateGradientBackground(
                    startColor = Color.Gray,
                    endColor = Color.DarkGray,
                ),
    ) {
    }
}

@Preview
@Composable
fun DashedBorderPreview() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .dashedBorder(
                    shape = RectangleShape,
                    width = 2.dp,
                    color = Color.Red,
                ),
    )
}