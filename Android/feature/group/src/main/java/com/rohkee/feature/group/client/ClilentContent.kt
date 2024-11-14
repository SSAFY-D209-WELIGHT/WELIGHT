package com.rohkee.feature.group.client

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rohkee.core.ui.component.appbar.GradientAppBar
import com.rohkee.core.ui.component.display.detail.DetailDisplay
import com.rohkee.core.ui.component.group.GroupBottomBar
import com.rohkee.core.ui.component.group.GroupBottomBarState
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground

@Composable
fun ClientContent(
    modifier: Modifier = Modifier,
    state: ClientState,
    onIntent: (ClientIntent) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Box {
            when (state) {
                ClientState.Loading -> {
                    Box(
                        modifier =
                            Modifier.fillMaxSize().animateGradientBackground(
                                startColor = AppColor.Background,
                                endColor = AppColor.Surface,
                            ),
                    )
                }

                is ClientState.Loaded -> {
                    DetailDisplay(
                        modifier = Modifier,
                        imageSource = state.thumbnailUrl,
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
                                groupId = state.groupId,
                                groupSize = state.groupSize,
                            ),
                        onGroupChange = { onIntent(ClientIntent.ChangeGroup(it)) },
                    )
                }
            }

            GradientAppBar(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = innerPadding.calculateTopPadding()),
            ) {}
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
                groupId = 1,
                groupSize = 5,
                thumbnailUrl = null,
                displayId = null,
            ),
    )
}
