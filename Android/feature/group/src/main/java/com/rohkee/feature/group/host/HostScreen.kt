package com.rohkee.feature.group.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun HostScreen(
    modifier: Modifier = Modifier,
    hostViewModel: HostViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit = {},
    onStartCheer: () -> Unit = {},
) {
    val hostUIState by hostViewModel.hostState.collectAsStateWithLifecycle()

    hostViewModel.hostEvent.collectWithLifecycle { event ->
        when (event) {
            HostEvent.ExitPage -> onPopBackStack()
        }
    }

    HostContent(
        modifier = modifier,
        state = hostUIState,
        onIntent = hostViewModel::onIntent,
    )
}
