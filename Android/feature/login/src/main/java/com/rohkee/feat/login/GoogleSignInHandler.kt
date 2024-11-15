package com.rohkee.feat.login

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

// Google 로그인 프로세스 핸들러
class GoogleSignInHandler(
    private val context: Context,
) : ActivityResultContract<Void?, Task<GoogleSignInAccount>?>() {
    // 초기화 시 패키지 이름 로깅
    init {
        Log.d("GoogleSignInHandler", "Initializing with package name: ${context.packageName}")
    }

    // Google Sign-In 클라이언트 설정
    private val googleSignInClient by lazy {
        // Google Sign-In 옵션 설정
        val gso =
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // 이메일 요청
                .requestProfile() // 프로필 정보 요청
                .requestId() // 사용자 ID 요청
                .build()

        Log.d("GoogleSignInHandler", "GSO created with options: $gso")

        // 이전 로그인 상태를 초기화 하고 새로운 클라이언트 반환
        GoogleSignIn.getClient(context, gso).apply {
            signOut().addOnCompleteListener {
                Log.d("GoogleSignInHandler", "Previous sign-in state cleared")
            }
        }
    }

    // 인텐트 생성
    override fun createIntent(
        context: Context,
        input: Void?,
    ): Intent =
        try {
            val signInIntent = googleSignInClient.signInIntent
            signInIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            Log.d(
                "GoogleSignInHandler",
                "Created sign-in intent for package: ${context.packageName}",
            )
            signInIntent
        } catch (e: Exception) {
            Log.e("GoogleSignInHandler", "Error creating sign-in intent", e)
            throw e
        }

    // 결과 처리
    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): Task<GoogleSignInAccount>? {
        Log.d("GoogleSignInHandler", "Parsing result - Result Code: $resultCode")
        Log.d("GoogleSignInHandler", "Intent: $intent")
        Log.d("GoogleSignInHandler", "Intent extras: ${intent?.extras}")

        return when (resultCode) {
            Activity.RESULT_OK -> {
                try {
                    // 로그인 성공 시 계정 정보 반환
                    GoogleSignIn.getSignedInAccountFromIntent(intent).apply {
                        addOnSuccessListener { account ->
                            Log.d("GoogleSignInHandler", "Sign in successful: ${account.email}")
                        }
                        addOnFailureListener { e ->
                            if (e is ApiException) {
                                Log.e(
                                    "GoogleSignInHandler",
                                    "Sign in failed with status: ${e.statusCode}",
                                )
                                Log.e("GoogleSignInHandler", "Error message: ${e.message}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("GoogleSignInHandler", "Error parsing result", e)
                    null
                }
            }

            Activity.RESULT_CANCELED -> {
                Log.d("GoogleSignInHandler", "Sign in cancelled by user")
                null
            }

            else -> {
                Log.e("GoogleSignInHandler", "Unknown result code: $resultCode")
                null
            }
        }
    }
}
