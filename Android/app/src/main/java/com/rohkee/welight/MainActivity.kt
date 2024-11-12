package com.rohkee.welight

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.rohkee.core.datastore.repository.DataStoreRepository
import com.rohkee.core.ui.theme.WeLightTheme
import com.rohkee.feat.login.LoginRoute
import com.rohkee.feat.login.LoginState
import com.rohkee.welight.navigation.MainNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("MainActivity", "onCreate 호출됨")

        setContent {
            WeLightTheme {
                var isLoggedIn by remember { mutableStateOf(false) }

                // 앱 시작시 토큰 확인
                LaunchedEffect(Unit) {
                    Log.d("MainActivity", "LaunchedEffect 시작")
                    checkLoginStatus { loggedIn ->
                        isLoggedIn = loggedIn
                        Log.d("MainActivity", "로그인 상태 설정됨: $loggedIn")
                    }
                }

                // 로그인 상태에 따라 적절한 화면 표시
                if (isLoggedIn) {
                    MainNavigation()
                } else {
                    LoginRoute(
                        onLoginSuccess = {
                            isLoggedIn = true
                        },
                    )
                }
            }
        }
    }

    private fun checkLoginStatus(onResult: (Boolean) -> Unit) {
        lifecycleScope.launch {
            try {
                val token = dataStoreRepository.getAccessToken()
                Log.d("MainActivity", "불러온 토큰: $token")
                onResult(token != null && token.isNotEmpty())
            } catch (e: Exception) {
                Log.e("MainActivity", "Error checking token", e)
                onResult(false)
            }
        }
    }
}
