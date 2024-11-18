package com.rohkee.feature.group.host

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.util.collectWithLifecycle
import com.rohkee.feature.group.util.MultiplePermissionHandler

@Composable
fun HostScreen(
    modifier: Modifier = Modifier,
    hostViewModel: HostViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit = {},
    onPopBackStack: () -> Unit = {},
    onStartCheer: (id: Long) -> Unit = {},
) {
    val hostUIState by hostViewModel.hostState.collectAsStateWithLifecycle()

    hostViewModel.hostEvent.collectWithLifecycle { event ->
        when (event) {
            HostEvent.ExitPage -> onPopBackStack()
            is HostEvent.StartCheer -> onStartCheer(event.roomId)
        }
    }

    MultiplePermissionHandler(
        permissions =
        listOf(
            Manifest.permission.RECORD_AUDIO,
        ),
    ) { result ->
        if (result.all { it.value }) {
            hostViewModel.onIntent(HostIntent.Permission.Granted)
        } else {
            hostViewModel.onIntent(HostIntent.Permission.Rejected)
        }
    }

    HostContent(
        modifier = modifier,
        state = hostUIState,
        onIntent = hostViewModel::onIntent,
    )
}
