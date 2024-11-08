package com.rohkee.core.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val ColorScheme =
    darkColorScheme(
        primary = AppColor.Surface,
        secondary = AppColor.Convex,
        tertiary = AppColor.Contrast,
    )

@Composable
fun WeLightTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppColor.BackgroundTransparent.toArgb()
            window.navigationBarColor = AppColor.BackgroundTransparent.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content,
    )
}
