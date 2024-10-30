package com.rohkee.core.ui.screen.storage

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 보관함 화면
 */
@Composable
fun StorageContent(
    modifier: Modifier = Modifier,
    state: StorageState,
    onIntent: (StorageIntent) -> Unit = {},
) {
    Column(modifier = modifier) {
        when (state) {
            is StorageState.Loading -> {
            }

            is StorageState.Loaded -> {
            }

            is StorageState.NoData -> {
            }

            is StorageState.Error -> {
            }
        }
    }
}

