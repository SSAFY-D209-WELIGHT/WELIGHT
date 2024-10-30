package com.rohkee.core.ui.component.storage

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor

@Composable
fun GradientLoadingCard(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .background(color = AppColor.Surface)

    ) {

    }
}

@Composable
fun AnimatedLoadingBox(
    startColor: Color,
    endColor: Color,
    animationDuration: Int = 1500,
    boxSize: Int = 128
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Animate offset from 0f to 1f continuously
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = LinearEasing
            )
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(boxSize.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            startColor,
                            endColor
                        ),
                        start = Offset(offset * (boxSize.dp.value * 2), 0f),
                        end = Offset((offset - 1) * (boxSize.dp.value * 2), 0f),
                        tileMode = TileMode.Mirror
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedLoadingBox(
            startColor = AppColor.Active,
            endColor = AppColor.Inactive
        )
    }
}