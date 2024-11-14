package com.rohkee.feat.mypage

import androidx.compose.foundation.Image
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
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rohkee.core.network.model.UserResponse

// EditProfileDialog.kt
@Composable
fun EditProfileDialog(
    userInfo: UserResponse,
    onDismiss: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val nickname by viewModel.nickname.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setNickname(userInfo.userNickname)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1C),
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "프로필 수정",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.padding(bottom = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    // 프로필 이미지
                    AsyncImage(
                        model = userInfo.userProfileImg,
                        contentDescription = "Profile Image",
                        modifier =
                            Modifier
                                .size(150.dp)
                                .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )

                    // 카메라 아이콘
                    Image(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Change Photo",
                        modifier =
                            Modifier
                                .size(45.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = (-5).dp, y = (5).dp)
                                .clip(CircleShape)
                                .clickable { },
                    )
                }

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { viewModel.setNickname(it) },
                    label = { Text("닉네임 변경", color = Color.Gray) },
                    singleLine = true,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                        ),
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                )

                error?.let { errorMessage ->
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier
                                .size(24.dp)
                                .padding(top = 8.dp),
                        color = Color.White,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
//                    viewModel.updateProfile()
                    onDismiss()
                },
                enabled = !isLoading,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray,
                    ),
            ) {
                Text(
                    "확인",
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
    )
}
