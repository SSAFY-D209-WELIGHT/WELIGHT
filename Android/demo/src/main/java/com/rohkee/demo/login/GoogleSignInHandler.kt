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

// Google 로그인 관련 모든 작업을 관리하는 단일 클래스
class GoogleSignInHandler(
    private val context: Context,
) : ActivityResultContract<Void?, Task<GoogleSignInAccount>?>() {
    init {
        Log.d("GoogleSignInHandler", "Initializing with package name: ${context.packageName}")
    }

    private val googleSignInClient by lazy {
        val gso =
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build()

        Log.d("GoogleSignInHandler", "GSO created with options: $gso")

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
