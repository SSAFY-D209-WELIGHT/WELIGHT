package com.rohkee.demo.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.rohkee.demo.BuildConfig

class GoogleApiContract : ActivityResultContract<Void?, Task<GoogleSignInAccount>?>() {

    override fun createIntent(context: Context, input: Void?): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_OAUTH_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()

        // GoogleSignInClient 생성 확인을 위한 로그
        Log.d("GoogleApiContract", "Client ID: ${BuildConfig.GOOGLE_OAUTH_CLIENT_ID}")

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        Log.d("GoogleApiContract", "GoogleSignInClient 생성됨: $googleSignInClient")
        Log.d("GoogleApiContract", "GoogleSignIn Intent: ${googleSignInClient.signInIntent}")

        return googleSignInClient.signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
        Log.d("GoogleApiContract", "Result Code: $resultCode")

        return when (resultCode) {
            Activity.RESULT_OK -> {
                Log.d("GoogleApiContract", "Login success, retrieving account")
                GoogleSignIn.getSignedInAccountFromIntent(intent)
            }
            Activity.RESULT_CANCELED -> {
                Log.e("GoogleApiContract", "Login canceled by user or failed to complete.")
                null
            }
            else -> {
                Log.e("GoogleApiContract", "Unknown error with result code: $resultCode")
                null
            }
        }
    }
}
