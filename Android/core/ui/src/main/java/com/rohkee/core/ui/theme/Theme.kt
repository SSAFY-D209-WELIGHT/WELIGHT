package com.rohkee.core.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

private val ColorScheme =
    darkColorScheme(
        primary = AppColor.Surface,
        secondary = AppColor.Convex,
        tertiary = AppColor.Contrast,
    )

@Composable
fun WeLightTheme(content: @Composable () -> Unit) {
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = (view.context as Activity).window
//            window.statusBarColor = AppColor.BackgroundTransparent.toArgb()
//            window.navigationBarColor = AppColor.BackgroundTransparent.toArgb()
//        }
//    }
    val context = LocalContext.current as ComponentActivity

    DisposableEffect(Unit) {
        context.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AppColor.BackgroundTransparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(AppColor.BackgroundTransparent.toArgb()),
        )

        onDispose {  }
    }

    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content,
    )
}
