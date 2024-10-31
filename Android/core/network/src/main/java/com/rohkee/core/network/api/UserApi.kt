package com.rohkee.core.network.api

import com.rohkee.core.network.ResponseBody
import com.rohkee.core.network.model.LoginRequest
import com.rohkee.core.network.model.TokenHolder
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    // /user/login
    @POST("/user/login")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): Response<ResponseBody<TokenHolder>>
}
