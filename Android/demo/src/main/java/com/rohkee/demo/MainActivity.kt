package com.rohkee.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.rohkee.demo.login.loginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // `loginScreen`을 호출할 때 `onGoogleSignInSuccess` 콜백 정의
                loginScreen(
                    onGoogleSignInSuccess = { account ->
                        // 로그인 성공 시 Google 계정 정보를 로그에 출력
                        account?.let {
                            // account가 null이 아닐 때만 실행
                            Log.d("MainActivity", "User logged in: ${it.displayName}")
                            // 추가로 필요한 작업이 있다면 여기에 작성
                        }
                    },
                )
            }
        }
    }
}
// onGoogleLoginClick = { startGoogleSignIn() },
