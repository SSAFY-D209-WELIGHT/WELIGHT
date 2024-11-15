package com.rohkee.core.network.repositoryImpl

import android.content.Context
import android.net.Uri
import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.api.UserApi
import com.rohkee.core.network.apiHandler
import com.rohkee.core.network.model.LoginRequest
import com.rohkee.core.network.model.TokenHolder
import com.rohkee.core.network.model.UserResponse
import com.rohkee.core.network.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    @ApplicationContext private val context: Context, // Context 주입 추가
) : UserRepository {
    override suspend fun login(
        userId: String,
        userNickname: String,
        userProfileImg: String,
        userLogin: String,
    ): ApiResponse<TokenHolder> {
        val response =
            apiHandler {
                userApi.login(LoginRequest(userId, userNickname, userProfileImg, userLogin))
            }
        return response
    }

    override suspend fun getUserInfo(): ApiResponse<UserResponse> {
        val response = apiHandler { userApi.getUserInfo() }
        return response
    }

    override suspend fun updateNickname(nickname: String): ApiResponse<Unit> = apiHandler { userApi.updateNickname(nickname) }

    override suspend fun updateProfileImage(imageUri: Uri): ApiResponse<Unit> =
        try {
            val file = getFileFromUri(context, imageUri)
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            apiHandler { userApi.updateProfileImage(body) }
        } catch (e: Exception) {
            ApiResponse.Error(
                errorCode = 500,
                errorMessage = e.message ?: "이미지 업로드 실패",
            )
        }

    private fun getFileFromUri(
        context: Context,
        uri: Uri,
    ): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "profile_image_${System.currentTimeMillis()}")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}
