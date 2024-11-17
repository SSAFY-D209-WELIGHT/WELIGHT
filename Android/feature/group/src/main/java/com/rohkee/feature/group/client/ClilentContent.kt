package com.rohkee.feature.group.client

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rohkee.core.ui.component.appbar.GradientAppBar
import com.rohkee.core.ui.component.display.editor.CustomDisplay
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.group.GroupBottomBar
import com.rohkee.core.ui.component.group.GroupBottomBarState
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground
import com.rohkee.feature.group.dialog.CheerDialog

private const val TAG = "ClientContent"

@Composable
fun ClientContent(
    modifier: Modifier = Modifier,
    state: ClientState,
    onIntent: (ClientIntent) -> Unit = {},
) {
    if (state is ClientState.Loaded && state.dialogState is ClientDialogState.StartCheer) {
        CheerDialog(
            displayId = state.displays[state.groupNumber - 1],
            offset = state.dialogState.offset,
            interval = state.dialogState.interval,
            onDismiss = { onIntent(ClientIntent.CheerDialog.Cancel) },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Box {
            when (state) {
                ClientState.Loading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .animateGradientBackground(
                                    startColor = AppColor.Background,
                                    endColor = AppColor.OnSurface,
                                ),
                    )
                }

                is ClientState.Loaded -> {
                    CustomDisplay(
                        modifier = Modifier.fillMaxSize(),
                        imageState = state.imageState,
                        textState = state.textState,
                        backgroundState = state.backgroundState,
                    )
                    GroupBottomBar(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(bottom = innerPadding.calculateBottomPadding()),
                        state =
                            GroupBottomBarState(
                                title = state.title,
                                description = state.description,
                                groupNumber = state.groupNumber,
                                groupSize = state.displays.size,
                            ),
                        onGroupChange = { onIntent(ClientIntent.ChangeGroup(it)) },
                    )
                }

                is ClientState.Cheering -> {
                    CheeringContent(state = state)
                }
            }

            if (state !is ClientState.Cheering) {
                GradientAppBar(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(top = innerPadding.calculateTopPadding()),
                    onClick = { onIntent(ClientIntent.ExitPage) },
                ) {}
            }
        }
    }
}

@Composable
private fun CheeringContent(
    modifier: Modifier = Modifier,
    state: ClientState.Cheering,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        CustomDisplay(
            modifier = modifier.fillMaxSize(),
            imageState = state.imageState,
            textState = state.textState,
            backgroundState = state.backgroundState,
        )

        if (state.interval > 0f) {
            val infiniteTransition = rememberInfiniteTransition(label = "flicker animation")

            val animatedValue by infiniteTransition.animateFloat(
                initialValue = state.offset,
                targetValue = if (state.offset < 1f) 1f else 0f,
                animationSpec =
                    infiniteRepeatable(
                        animation =
                            tween(
                                durationMillis = state.interval.toInt(),
                                easing = LinearEasing,
                            ),
                        repeatMode = RepeatMode.Reverse,
                    ),
                label = "animate offset",
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(color = Color.Black.copy(alpha = animatedValue)),
            )
        }
    }
}

@Preview
@Composable
private fun ClientContentPreview() {
    ClientContent(
        state =
            ClientState.Loaded(
                title = "제목",
                description = "설명",
                groupNumber = 1,
                displays = listOf(1L, 2L, 3L),
                dialogState = ClientDialogState.Closed,
                imageState = DisplayImageState(),
                textState = DisplayTextState(),
                backgroundState = DisplayBackgroundState(),
            ),
    )
}

@Preview
@Composable
fun CheeringContentPreview() {
    CheeringContent(
        state =
            ClientState.Cheering(
                offset = 0.5f,
                interval = 1000f,
                imageState = DisplayImageState(),
                textState = DisplayTextState(),
                backgroundState = DisplayBackgroundState(),
            ),
    )
}
