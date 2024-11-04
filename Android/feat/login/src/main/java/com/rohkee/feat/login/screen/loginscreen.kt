package com.rohkee.feat.login.screen

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
import com.rohkee.demo.R

@Composable
fun loginScreen(
    modifier: Modifier = Modifier,
    onGoogleLoginClick: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Black, // 백그라운드 컬러
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Welight 로고
            val logoPainter: Painter = painterResource(id = R.drawable.welight_logo)
            Image(
                painter = logoPainter,
                contentDescription = "Welight Logo",
                contentScale = ContentScale.Fit,
                modifier =
                    Modifier
                        .height(100.dp)
                        .padding(bottom = 100.dp),
            )
        }

        // 구글 로그인 버튼
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
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
                        .height(48.dp)
                        .width(250.dp)
                        .clickable { onGoogleLoginClick() },
            )
        }
    }
}
