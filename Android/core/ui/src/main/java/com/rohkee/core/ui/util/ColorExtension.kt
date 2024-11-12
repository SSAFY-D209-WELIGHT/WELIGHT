package com.rohkee.core.ui.util

import androidx.compose.ui.graphics.Color

fun Color.toHexString(): String {
    val alpha = (this.alpha * 255).toInt()
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()

    return String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
}

fun String.toComposeColor(): Color {
    val cleanHex = this.removePrefix("#")

    return when (cleanHex.length) {
        8 -> { // With alpha (#AARRGGBB)
            Color(
                alpha = cleanHex.substring(0, 2).toInt(16) / 255f,
                red = cleanHex.substring(2, 4).toInt(16) / 255f,
                green = cleanHex.substring(4, 6).toInt(16) / 255f,
                blue = cleanHex.substring(6, 8).toInt(16) / 255f,
            )
        }

        6 -> { // Without alpha (#RRGGBB)
            Color(
                red = cleanHex.substring(0, 2).toInt(16) / 255f,
                green = cleanHex.substring(2, 4).toInt(16) / 255f,
                blue = cleanHex.substring(4, 6).toInt(16) / 255f,
            )
        }

        else -> throw IllegalArgumentException("Invalid hex color string")
    }
}
