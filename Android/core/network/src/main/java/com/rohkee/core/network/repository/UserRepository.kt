package com.rohkee.core.network.repository

import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.model.TokenHolder

interface UserRepository {
    suspend fun login(
        userId: String,
        userNickname: String,
        userProfileImg: String,
        userLogin: String = "Google",
    ): ApiResponse<TokenHolder>
}
