package com.rohkee.feature.group.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.component.display.editor.CustomDisplay
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

    LaunchedEffect(offset, interval) { cheerDialogViewModel.animate(offset, interval) }

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
    ) {
        DialogContent(
            state = state,
        )
    }
}

@Composable
private fun DialogContent(state: CheerDialogState) {
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
            CustomDisplay(
                modifier = Modifier.fillMaxSize(),
                imageState = state.imageState,
                textState = state.textState,
                backgroundState = state.backgroundState,
            )
    }
}
