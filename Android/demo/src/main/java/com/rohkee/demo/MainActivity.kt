package com.rohkee.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.rohkee.demo.login.GoogleSignInHandler
import com.rohkee.demo.login.LoginScreen

class MainActivity : ComponentActivity() {
    private lateinit var googleSignInHandler: GoogleSignInHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // GoogleSignInHandler 초기화
        googleSignInHandler = GoogleSignInHandler(this)

        // 로그인 런처 설정
        val googleSignInLauncher =
            registerForActivityResult(googleSignInHandler) { task ->
                if (task != null) {
                    try {
                        val account = task.getResult(ApiException::class.java)
                        Log.d("MainActivity", "account info : $account")
                        onGoogleSignInSuccess(account)
                    } catch (e: ApiException) {
                        Log.e("MainActivity", "Google sign-in failed", e)
                    }
                } else {
                    Log.e("MainActivity", "Google sign-in canceled or failed.")
                }
            }

        setContent {
            MaterialTheme {
                LoginScreen(
                    onGoogleSignInSuccess = { account ->
                        account?.let {
                            Log.d("MainActivity", "User logged in: ${it.displayName}")
                        }
                    },
                    onGoogleLoginClick = {
                        val signInIntent = googleSignInHandler.getSignInIntent()
                        googleSignInLauncher.launch(null)
                    },
                )
            }
        }
    }

    private fun onGoogleSignInSuccess(account: GoogleSignInAccount?) {
        account?.let {
            Log.d("MainActivity", "User logged in: ${it.displayName}")
        }
    }
}
