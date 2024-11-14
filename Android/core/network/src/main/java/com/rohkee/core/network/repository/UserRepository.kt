package com.rohkee.core.network.repository

import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.model.TokenHolder
import com.rohkee.core.network.model.UserResponse

interface UserRepository {
    suspend fun login(
        userId: String,
        userNickname: String,
        userProfileImg: String,
        userLogin: String = "Google",
    ): ApiResponse<TokenHolder>

    suspend fun getUserInfo(): ApiResponse<UserResponse>
}
