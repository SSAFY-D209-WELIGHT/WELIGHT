package com.rohkee.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rohkee.demo.login.GoogleSignInHandler
import com.rohkee.demo.login.LoginScreen
import com.rohkee.demo.login.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val loginViewModel: LoginViewModel = viewModel()
                val launcher =
                    rememberLauncherForActivityResult(
                        contract = GoogleSignInHandler(this),
                        onResult = { task ->
                            loginViewModel.handleSignInResult(task) { account ->
                                Log.d("MainActivity", "User logged in: ${account.displayName}")
                            }
                        },
                    )

                // ViewModel 초기화
                loginViewModel.initGoogleSignIn(
                    context = this,
                    launcher = launcher,
                    onSignInSuccess = { account ->
                        Log.d("MainActivity", "Sign in successful")
                        Log.d("MainActivity", "Email: ${account.email}")
                        Log.d("MainActivity", "Display Name: ${account.displayName}")
                    },
                )

                LoginScreen(
                    onGoogleSignInSuccess = { account ->
                        account?.let {
                            Log.d("MainActivity", "User logged in: ${it.displayName}")
                        }
                    },
                    onGoogleLoginClick = {
                        if (loginViewModel.checkGooglePlayServices(this)) {
                            loginViewModel.launchSignIn()
                        }
                    },
                )
            }
        }
    }
}
