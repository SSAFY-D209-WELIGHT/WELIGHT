package com.rohkee.core.network.repositoryImpl

import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.api.UserApi
import com.rohkee.core.network.apiHandler
import com.rohkee.core.network.model.LoginRequest
import com.rohkee.core.network.model.TokenHolder
import com.rohkee.core.network.repository.UserRepository
import com.rohkee.core.network.util.simplify
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
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
}
