package com.rohkee.feat.mypage.cheer_record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.PersistentList

@Composable
fun CheerRecordScreen(
    modifier: Modifier = Modifier,
    viewModel: CheerRecordViewModel = hiltViewModel(),
) {
    val cheerRecordUIState by viewModel.cheerRecords.collectAsStateWithLifecycle()

    CheerRecordContent(
        modifier = modifier,
        state = cheerRecordUIState,
        onRefresh = viewModel::onRefresh
    )
}


sealed interface CheerRecordUIState {
    data object Loading : CheerRecordUIState

    data class Loaded(
        val isRefreshing: Boolean = false,
        val cheerRecords: PersistentList<CheerRecordCardState>,
    ) : CheerRecordUIState

    data class Error(
        val message: String,
    ) : CheerRecordUIState
}
