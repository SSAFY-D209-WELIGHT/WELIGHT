package com.rohkee.feat.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.model.UserResponse
import com.rohkee.core.network.repository.UserRepository
import com.rohkee.feat.mypage.cheer_record.CheerRecordScreen
import com.rohkee.feat.mypage.like_record.LikeRecordScreen
import kotlinx.coroutines.launch

@Composable
fun MypageScreen(userRepository: UserRepository) {
    var selectedTab by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var userInfo by remember {
        mutableStateOf(
            UserResponse(
                userId = 1L,
                userNickname = "곽대건",
                userProfileImg = "https://cdn.pixabay.com/photo/2024/02/17/00/18/cat-8578562_1280.jpg",
            ),
        )
    }

    // 프로필 정보 받아오기
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            when (val response = userRepository.getUserInfo()) {
                is ApiResponse.Success -> {
                    response.body?.let { userInfo = it }
                }

                is ApiResponse.Error -> {
                    error = response.errorMessage
                }
            }
        }
    }

    // 프로필 수정 다이얼로그
    if (showEditDialog) {
        EditProfileDialog(
            userInfo = userInfo,
            onDismiss = { showEditDialog = false },
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black),
    ) {
        // Profile and Stats Section
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Profile Image and Nickname
            Box(
                modifier =
                    Modifier
                        .width(150.dp),
            ) {
                Image(
                    painter = rememberImagePainter(data = userInfo.userProfileImg),
                    contentDescription = "Profile Image",
                    modifier =
                        Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )

                Row(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .padding(top = 160.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = userInfo.userNickname,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Image(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit Profile",
                        modifier =
                            Modifier
                                .padding(start = 8.dp)
                                .size(20.dp)
                                .clickable { showEditDialog = true },
                    )
                }
            }

            // Stats Section
            Box(
                modifier =
                    Modifier
                        .padding(start = 30.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    StatRow("참여 응원수", "4")
                    StatRow("응원 보관함", "5")
                    StatRow("즐겨찾기 수", "2")
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Black,
            contentColor = Color.White,
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("응원내역", color = if (selectedTab == 0) Color.White else Color.Gray) },
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("좋아요", color = if (selectedTab == 1) Color.White else Color.Gray) },
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> CheerRecordScreen()
            1 -> LikeRecordScreen()
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
