package com.rohkee.feat.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.rohkee.core.ui.theme.AppColor

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onGoogleSignInSuccess: (GoogleSignInAccount) -> Unit,
    onGoogleLoginClick: () -> Unit,
    onShowSnackbar: (String) -> Unit,
) {
    var tapped by remember { mutableStateOf(0) }

    LaunchedEffect(tapped) {
        if(tapped >= 3)
            onShowSnackbar("version : 1.0.0")
    }

    // UI 설정
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColor.Background,
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
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
                        .height(125.dp)
                        .pointerInput(Unit) {
                            detectTapGestures { tapped += 1 }
                        },
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
                            .clickable { onGoogleLoginClick() },
                    // Google 로그인 시작
                )
            }
        }
    }
}
