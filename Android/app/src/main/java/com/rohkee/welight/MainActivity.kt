package com.rohkee.welight

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.rohkee.core.ui.theme.WeLightTheme
import com.rohkee.feat.login.LoginRoute
import com.rohkee.welight.navigation.MainNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            WeLightTheme {
                // 로그인 상태 관찰
                val isLoggedIn by mainViewModel.isLoggedIn.observeAsState(initial = null)

                // 로그인 상태에 따라 화면 표시
                // null이 아니면 로그인 상태에 따라 화면 전환
                when (isLoggedIn) {
                    null -> {
                        // 로딩 중일 때 표시할 UI
                        Log.d("MainActivity", "로딩 중...")
                    }

                    true -> {
                        Log.d("MainActivity", "메인 네비게이션으로 이동합니다.")
                        MainNavigation()
                    }

                    false -> {
                        Log.d("MainActivity", "로그인 화면으로 이동합니다.")
                        LoginRoute(
                            onLoginSuccess = {
                                mainViewModel.loginSuccess()
                            },
                        )
                    }
                }
            }
        }
    }
}
