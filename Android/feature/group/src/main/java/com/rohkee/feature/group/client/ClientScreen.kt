package com.rohkee.feature.group.client

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun ClientScreen(
    modifier: Modifier = Modifier,
    clientViewModel: ClientViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit = {},
) {
    val context = LocalContext.current as ComponentActivity
    val clientUiState by clientViewModel.clientState.collectAsStateWithLifecycle()

    clientViewModel.clientEvent.collectWithLifecycle { event ->
        when (event) {
            ClientEvent.ExitPage -> onPopBackStack()
        }
    }

    LaunchedEffect(Unit) {
        context.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
        )
    }

    ClientContent(
        modifier = modifier,
        state = clientUiState,
        onIntent = clientViewModel::onIntent,
    )
}
