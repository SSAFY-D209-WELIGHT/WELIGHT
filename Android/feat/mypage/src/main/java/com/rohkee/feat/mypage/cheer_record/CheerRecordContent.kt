package com.rohkee.feat.mypage.cheer_record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CheerRecordContent(
    modifier: Modifier = Modifier,
    state: CheerRecordUIState,
) {
    when (state) {
        is CheerRecordUIState.Error -> {
            // TODO 에러 처리
        }

        is CheerRecordUIState.Loaded ->
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(state.cheerRecords) { record ->
                    CheerRecordCard(state = record)
                }
            }

        CheerRecordUIState.Loading -> {
            // TODO 로딩 처리
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
