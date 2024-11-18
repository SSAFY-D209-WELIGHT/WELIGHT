package com.rohkee.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme =
    darkColorScheme(
        primary = AppColor.Surface,
        secondary = AppColor.Convex,
        tertiary = AppColor.Contrast,
    )

@Composable
fun WeLightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content,
    )
}
