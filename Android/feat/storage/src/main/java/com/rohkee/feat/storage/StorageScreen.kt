package com.rohkee.feat.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.screen.storage.StorageContent
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun StorageScreen(
    modifier: Modifier = Modifier,
    storageViewModel: StorageViewModel = hiltViewModel(),
    onNavigateToDisplayDetail: (Long) -> Unit,
    onNavigateToCreateNewDisplay: () -> Unit,
) {
    val storageUIState by storageViewModel.storageState.collectAsStateWithLifecycle()

    storageViewModel.storageEvent.collectWithLifecycle {
        when (it) {
            is StorageEvent.OpenDisplay -> onNavigateToDisplayDetail(it.id)
            is StorageEvent.CreateNewDisplay -> onNavigateToCreateNewDisplay()
            else -> {}
        }
    }

    StorageContent(
        state = storageUIState,
        onIntent = storageViewModel::onIntent,
    )
}
