package com.rohkee.feat.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.component.common.GradientImageLoader
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard
import com.rohkee.feat.mypage.cheer_record.CheerRecordScreen
import com.rohkee.feat.mypage.like_record.LikeRecordScreen

@Immutable
sealed interface MypageUIState {
    data object Loading : MypageUIState

    data class Loaded(
        val userName: String,
        val userProfileImg: String,
    ) : MypageUIState

    data class Error(
        val message: String?,
    ) : MypageUIState
}

@Composable
fun MypageScreen(
    modifier: Modifier = Modifier,
    mypageViewModel: MypageViewModel = hiltViewModel(),
    onNavigateToDisplayDetail: (displayId: Long) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val mypageUIState by mypageViewModel.mypageUIState.collectAsStateWithLifecycle()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.Black),
    ) {
        Box(
            modifier = Modifier.background(color = AppColor.Surface).height(144.dp).padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (mypageUIState) {
                is MypageUIState.Error -> {
                    // TODO : 에러 처리
                }

                is MypageUIState.Loaded -> {
                    val loaded = mypageUIState as MypageUIState.Loaded
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        GradientImageLoader(
                            modifier =
                                Modifier
                                    .size(80.dp)
                                    .clip(shape = CircleShape)
                                    .border(2.dp, color = AppColor.OnSurface, shape = CircleShape),
                            imageSource = loaded.userProfileImg,
                        )
                        Text(
                            text = loaded.userName,
                            style = Pretendard.Medium20,
                            color = AppColor.OnBackground,
                        )
                    }
                }

                MypageUIState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppColor.OnBackground,
                    )
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = AppColor.Background,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AppColor.OnSurface,
                    )
                }
            },
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "응원내역",
                        style = if (selectedTab == 0) Pretendard.SemiBold16 else Pretendard.Medium16,
                        color = if (selectedTab == 0) AppColor.OnSurface else AppColor.Inactive,
                    )
                },
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "좋아요",
                        style = if (selectedTab == 1) Pretendard.SemiBold16 else Pretendard.Medium16,
                        color = if (selectedTab == 1) AppColor.OnSurface else AppColor.Inactive,
                    )
                },
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> CheerRecordScreen()
            1 -> LikeRecordScreen(
                onNavigateToDisplayDetail = onNavigateToDisplayDetail
            )
        }
    }
}
