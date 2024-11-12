package com.rohkee.feature.group.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun ClientScreen(
    modifier: Modifier = Modifier,
    clientViewModel: ClientViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit = {},
    onStartCheer: (id: Long) -> Unit = {},
) {
    val clientUiState by clientViewModel.clientState.collectAsStateWithLifecycle()

    clientViewModel.clientEvent.collectWithLifecycle { event ->
        when (event) {
            ClientEvent.ExitPage -> onPopBackStack()
        }
    }

    ClientContent(
        modifier = modifier,
        state = clientUiState,
        onIntent = clientViewModel::onIntent,
    )
}
