package com.rohkee.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val provider: String,
    val accessToken: String,
)
