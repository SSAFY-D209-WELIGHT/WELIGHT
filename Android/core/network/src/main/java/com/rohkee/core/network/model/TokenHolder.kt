package com.rohkee.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenHolder(
    val accessToken: String,
    val refreshToken: String,
)
