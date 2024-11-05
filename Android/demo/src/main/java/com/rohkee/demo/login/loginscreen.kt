package com.rohkee.demo.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.rohkee.demo.R

@Composable
fun loginScreen(
    modifier: Modifier = Modifier,
    onGoogleSignInSuccess: (GoogleSignInAccount) -> Unit,
) {
    // `googleSignInLauncher` 설정
    val googleSignInLauncher =
        rememberLauncherForActivityResult(
            contract = GoogleApiContract(),
            onResult = { task ->
                try {
                    // Google 로그인 성공 시 계정 정보를 가져옴
                    val account = task?.getResult(ApiException::class.java)
                    Log.d("LoginScreen", "${task?.exception}")
                    if (account != null) {
                        onGoogleSignInSuccess(account) // 계정 정보 전달
                    }
                } catch (e: ApiException) {
                    // 로그인 실패 처리
                    Log.e("LoginScreen", "Google sign-in failed", e)
                }
            },
        )

    // UI 설정
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Black,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f)) // 상단 공간 확보

            // 로고 이미지
            val logoPainter: Painter = painterResource(id = R.drawable.welight_logo)
            Image(
                painter = logoPainter,
                contentDescription = "WeLight Logo",
                contentScale = ContentScale.Fit,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(125.dp),
            )

            Spacer(modifier = Modifier.weight(1f)) // 로고와 하단 버튼 사이의 여백

            // Google 로그인 버튼을 하단에 고정
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                val googleButtonPainter: Painter = painterResource(id = R.drawable.google_login)
                Image(
                    painter = googleButtonPainter,
                    contentDescription = "Sign in with Google",
                    contentScale = ContentScale.Fit,
                    modifier =
                        Modifier
                            .height(54.dp)
                            .width(250.dp)
                            .clickable { googleSignInLauncher.launch(null) },
                )
            }
        }
    }
}
