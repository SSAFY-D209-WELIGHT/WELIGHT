package com.rohkee.feat.mypage.cheer_record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheerRecordContent(
    modifier: Modifier = Modifier,
    state: CheerRecordUIState,
    onRefresh: () -> Unit = {},
) {
    when (state) {
        is CheerRecordUIState.Error -> {
            // TODO 에러 처리
        }

        is CheerRecordUIState.Loaded -> {
            PullToRefreshBox(
                onRefresh = { onRefresh() },
                modifier = Modifier.fillMaxSize(),
                isRefreshing = state.isRefreshing,
            ) {
                if (state.cheerRecords.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "응원내역이 없습니다",
                            style = Pretendard.Medium24,
                            color = AppColor.OnBackgroundTransparent,
                        )
                    }
                } else {
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
                }
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
