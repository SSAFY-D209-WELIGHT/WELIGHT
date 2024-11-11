package com.rohkee.feature.storage

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun StorageScreen(
    modifier: Modifier = Modifier,
    storageViewModel: StorageViewModel = hiltViewModel(),
    onNavigateToDisplayDetail: (Long) -> Unit,
    onNavigateToCreateNewDisplay: () -> Unit,
) {
    val context = LocalContext.current as ComponentActivity
    val storageUIState by storageViewModel.storageState.collectAsStateWithLifecycle()

    storageViewModel.storageEvent.collectWithLifecycle {
        when (it) {
            is StorageEvent.OpenDisplay -> onNavigateToDisplayDetail(it.id)
            is StorageEvent.CreateNewDisplay -> onNavigateToCreateNewDisplay()
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        context.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AppColor.Background.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(AppColor.BackgroundTransparent.toArgb()),
        )
    }

    StorageContent(
        modifier = modifier,
        state = storageUIState,
        onIntent = storageViewModel::onIntent,
    )
}
