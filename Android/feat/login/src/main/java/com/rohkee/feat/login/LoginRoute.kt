package com.rohkee.feat.login

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit
) {
    // 현재 컨텍스트와 액티비티 참조 가져오기
    val context = LocalContext.current
    val activity = context as Activity
    val loginViewModel: LoginViewModel = hiltViewModel()

    // 로그인 상태 수집
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()

    // 로그인 상태 변화 감지
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> onLoginSuccess()
            else -> {}
        }
    }

    MaterialTheme {
        // ViewModel 및 로그인 런처 초기화
        val launcher =
            rememberLauncherForActivityResult(
                contract = GoogleSignInHandler(context),
                onResult = { task ->
                    loginViewModel.handleSignInResult(task) { account ->
                        Log.d("MainActivity", "User logged in: ${account.displayName}")
                    }
                },
            )

        // ViewModel의 Google Sign-In 초기화
        loginViewModel.initGoogleSignIn(
            context = context,
            launcher = launcher,
            onSignInSuccess = { account ->
                Log.d("LoginRoute", "Sign in successful")
                Log.d("LoginRoute", "Email: ${account.email}")
                Log.d("LoginRoute", "Display Name: ${account.displayName}")
            },
        )

        // 로그인 화면 UI 표시
        LoginScreen(
            onGoogleSignInSuccess = { account ->
                account?.let {
                    Log.d("LoginRoute", "User logged in: ${it.displayName}")
                }
            },
            onGoogleLoginClick = {
                // Google Play Services 확인 후 로그인 시도
                if (loginViewModel.checkGooglePlayServices(activity)) {
                    loginViewModel.launchSignIn()
                }
            },
        )
    }
}