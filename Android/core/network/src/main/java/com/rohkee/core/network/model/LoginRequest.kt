package com.rohkee.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("userId")
    val userId: String,
    @SerialName("userNickname")
    val userNickname: String,
    @SerialName("userProfileImg")
    val userProfileImg: String,
    @SerialName("userLogin")
    val userLogin: String = "Google"
)
