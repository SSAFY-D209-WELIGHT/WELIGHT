package com.rohkee.demo.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.rohkee.demo.BuildConfig

// Google 로그인 관련 모든 작업을 관리하는 단일 클래스
class GoogleSignInHandler(
    private val context: Context,
) : ActivityResultContract<Void?, Task<GoogleSignInAccount>?>() {
    private val googleSignInClient by lazy {
        val gso =
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_OAUTH_CLIENT_ID)
                .requestEmail()
                .build()
        GoogleSignIn.getClient(context, gso)
    }

    // 인텐트 생성
    override fun createIntent(
        context: Context,
        input: Void?,
    ): Intent {
        Log.d("GoogleSignInHandler", "Client ID: ${BuildConfig.GOOGLE_OAUTH_CLIENT_ID}")
        val signInIntent =
            googleSignInClient.signInIntent.apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        Log.d("GoogleSignInHandler", "Sign-In Intent: $signInIntent")
        return signInIntent
    }

    // 결과 처리
    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): Task<GoogleSignInAccount>? {
        Log.d("GoogleSignInHandler", "Result Code: $resultCode")
        return if (resultCode == Activity.RESULT_OK) {
            try {
                GoogleSignIn.getSignedInAccountFromIntent(intent)
            } catch (e: ApiException) {
                Log.e("GoogleSignInHandler", "Failed to get Google SignIn Account", e)
                null
            }
        } else {
            Log.e("GoogleSignInHandler", "Login canceled by user or failed to complete.")
            null
        }
    }

    // 로그인 시작 인텐트를 반환
    fun getSignInIntent(): Intent = googleSignInClient.signInIntent
}
