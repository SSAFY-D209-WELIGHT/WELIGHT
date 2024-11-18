package com.rohkee.core.network.api

import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.model.LoginRequest
import com.rohkee.core.network.model.TokenHolder
import com.rohkee.core.network.model.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApi {
    // /user/login/social
    @POST("/api/user/login/social")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): Response<TokenHolder>

    // /user/info
    @GET("/api/user/info")
    suspend fun getUserInfo(): Response<UserResponse>

    // /user/nickname
    @PATCH("user/nickname")
    suspend fun updateNickname(
        @Query("nickname") nickname: String,
    ): Response<Unit>

    // /user/img
    @Multipart
    @PATCH("user/img")
    suspend fun updateProfileImage(
        @Part image: MultipartBody.Part,
    ): Response<Unit>
}
