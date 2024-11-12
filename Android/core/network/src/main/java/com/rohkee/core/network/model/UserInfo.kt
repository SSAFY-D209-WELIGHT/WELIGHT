package com.rohkee.core.network.model

data class UserInfo(
    val userId: Int,
    val userNickname: String,
    val userProfileImg: String,
    val userLogin: String,
    val userIsAdmin: Boolean,
    val userSignupDate: String
)