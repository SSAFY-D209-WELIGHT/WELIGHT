package com.rohkee.feature.group.dialog

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.component.display.editor.CustomDisplay
import com.rohkee.core.ui.component.group.AnimatedBlocker
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground

@Composable
fun CheerDialog(
    displayId: Long,
    offset: Float,
    interval: Float,
    onDismiss: () -> Unit,
    cheerDialogViewModel: CheerDialogViewModel = hiltViewModel(),
) {
    val state by cheerDialogViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(displayId) { cheerDialogViewModel.loadDisplay(displayId) }

    val context = LocalContext.current as ComponentActivity

    BackHandler {
        onDismiss()
    }

    LaunchedEffect(state) {
        context.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
        )
    }

    DialogContent(
        state = state,
        offset = offset,
        interval = interval,
    )
//    Dialog(
//        onDismissRequest = onDismiss,
//        properties =
//            DialogProperties(
//                dismissOnClickOutside = false,
//                usePlatformDefaultWidth = false,
//                decorFitsSystemWindows = false,
//            ),
//    ) {
//    }
}

@Composable
private fun DialogContent(
    state: CheerDialogState,
    offset: Float,
    interval: Float,
) {
    when (state) {
        CheerDialogState.Loading -> {
            Box(
                modifier =
                    Modifier.fillMaxSize().animateGradientBackground(
                        startColor = AppColor.Background,
                        endColor = AppColor.OverSurface,
                    ),
            )
        }

        is CheerDialogState.Loaded ->
            Box(modifier = Modifier.fillMaxSize()) {
                CustomDisplay(
                    modifier = Modifier.fillMaxSize(),
                    imageState = state.imageState,
                    textState = state.textState,
                    backgroundState = state.backgroundState,
                )
                AnimatedBlocker(interval = interval, offset = offset)
            }
    }
}
