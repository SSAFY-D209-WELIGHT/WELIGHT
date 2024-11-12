package com.rohkee.feat.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
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

@Composable
fun MypageScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Column(
    modifier = Modifier
    .fillMaxSize()
    .background(Color.Black)
    )
    {
        // Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "9:30",
                color = Color.White,
                fontSize = 12.sp
            )
        }

        // Profile Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter = painterResource(id = R.drawable.profile_placeholder),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Edit Profile",
                    modifier = Modifier
                        .background(Color.Black, CircleShape)
                        .padding(4.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "NICKNAME",
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

    @Composable
    private fun TabButton(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick)
        ) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 12.dp)
            )
            if (isSelected) {
                Divider(
                    color = Color.White,
                    thickness = 2.dp
                )
            }
        }
    }

    @Composable
    private fun EventCard(event: Event) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
        ) {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(100.dp)
                        .background(Color(0xFF660000), RoundedCornerShape(8.dp))
                )

                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = event.date,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${event.participants}명 참여 주최자",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.description,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    data class Event(
        val date: String,
        val title: String,
        val participants: Int,
        val description: String
    )

    private fun getEventList() = listOf(
        Event(
            date = "2024.03.29 7PM",
            title = "윤하 20주년 콘",
            participants = 8,
            description = "윤하는 최고야"
        ),
        Event(
            date = "2024.06.24 7PM",
            title = "아디오스 오디오",
            participants = 7,
            description = "이것 멘데이 프로젝트"
        )
    )
}