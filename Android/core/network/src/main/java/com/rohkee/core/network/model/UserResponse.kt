package com.rohkee.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// /{
//  "userUid": 9,
//  "userNickname": "Seungsoo Kim",
//  "userProfileImg": "null",
//  "userLogin": "Google",
//  "userIsAdmin": false,
//  "userSignupDate": "2024-11-13T01:33:28"
// }

@Serializable
data class UserResponse(
    @SerialName("userUid")
    val userId: Long,
    @SerialName("userNickname")
    val userNickname: String,
    @SerialName("userProfileImg")
    val userProfileImg: String,
)
