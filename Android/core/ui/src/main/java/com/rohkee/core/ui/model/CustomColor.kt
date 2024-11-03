package com.rohkee.core.ui.model

import androidx.compose.foundation.background
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

enum class ColorType {
    Horizontal,
    Vertical,
    Radial,
}

@Immutable
sealed interface CustomColor {
    data class Single(
        val color: Color,
    ) : CustomColor

    data class Gradient(
        val colors: List<Color>,
        val type: ColorType = ColorType.Horizontal,
    ) : CustomColor
}

fun CustomColor.Single.toGradient(): CustomColor.Gradient = CustomColor.Gradient(listOf(this.color))

fun CustomColor.Gradient.toSingle(): CustomColor.Single = CustomColor.Single(this.colors.first())

fun CustomColor.Gradient.getBrush(): Brush =
    when (this.type) {
        ColorType.Horizontal -> horizontalGradient(this.colors)
        ColorType.Vertical -> verticalGradient(this.colors)
        ColorType.Radial -> radialGradient(this.colors)
    }

fun Modifier.background(
    color: CustomColor,
    shape: Shape = RectangleShape,
): Modifier =
    when (color) {
        is CustomColor.Single -> this.background(color = color.color, shape = shape)
        is CustomColor.Gradient -> this.background(color.getBrush(), shape = shape)
    }
