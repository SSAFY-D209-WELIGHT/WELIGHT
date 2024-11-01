package com.rohkee.core.network.repository

import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.model.TokenHolder

interface UserRepository {
    suspend fun login(
        provider: String,
        accessToken: String,
    ): ApiResponse<TokenHolder>
}
