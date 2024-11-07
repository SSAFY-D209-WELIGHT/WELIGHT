package com.rohkee.feat.login

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException

// 로그인 관련 비즈니스 로직 처리
class LoginViewModel : ViewModel() {
    private lateinit var googleSignInHandler: GoogleSignInHandler
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Void?>

    // Google Sign-In 컴포넌트를 초기화
    fun initGoogleSignIn(
        context: Context,
        launcher: ActivityResultLauncher<Void?>,
        onSignInSuccess: (GoogleSignInAccount) -> Unit,
    ) {
        googleSignInHandler = GoogleSignInHandler(context)
        googleSignInLauncher = launcher
    }

    // Google Play Services의 사용 가능 여부를 확인
    fun checkGooglePlayServices(activity: Activity): Boolean {
        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(activity)
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e("LoginViewModel", "Google Play Services not available: $resultCode")
            availability.getErrorDialog(activity, resultCode, 1)?.show()
            return false
        }
        return true
    }

    // Google Sign-In 결과를 처리
    fun handleSignInResult(
        task: com.google.android.gms.tasks.Task<GoogleSignInAccount>?,
        onSuccess: (GoogleSignInAccount) -> Unit,
    ) {
        if (task == null) {
            Log.e("LoginViewModel", "Sign-in task is null")
            return
        }

        task
            .addOnSuccessListener { account ->
                Log.d("LoginViewModel", "Sign in successful")
                Log.d("LoginViewModel", "Email: ${account.email}")
                Log.d("LoginViewModel", "Id: ${account.id}")
                Log.d("LoginViewModel", "PhotoUrl: ${account.photoUrl}")
                onSuccess(account)
            }.addOnFailureListener { e ->
                if (e is ApiException) {
                    Log.e("LoginViewModel", "Sign in failed with status: ${e.statusCode}")
                    Log.e("LoginViewModel", "Error message: ${e.message}")
                } else {
                    Log.e("LoginViewModel", "Sign in failed", e)
                }
            }
    }

    // Google Sign-In 프로세스를 시작
    fun launchSignIn() {
        try {
            googleSignInLauncher.launch(null)
            Log.d("LoginViewModel", "Launching sign in")
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error launching sign in", e)
        }
    }
}
