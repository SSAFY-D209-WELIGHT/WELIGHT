package com.rohkee.feat.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.rohkee.core.network.repository.UserRepository
import com.rohkee.core.network.model.UserInfo
import kotlinx.coroutines.launch

@Composable
fun MypageScreen(userRepository: UserRepository) {
    var selectedTab by remember { mutableStateOf(0) }
    var userInfo by remember { mutableStateOf<UserInfo?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch user info on first composition
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val response = userRepository.getUserInfo()
            if (response is ApiResponse.Success) {
                userInfo = response.body
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {}

        // Profile Picture and Nickname
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter = rememberImagePainter(data = userInfo?.userProfileImg ?: ""),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = userInfo?.userNickname ?: "NICKNAME",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Stats Section
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            StatRow("참여 응원수", "4")
            StatRow("응원 보관함", "5")
            StatRow("즐겨찾기 수", "2")
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.White)
        Text(text = value, color = Color.White)
    }
}

