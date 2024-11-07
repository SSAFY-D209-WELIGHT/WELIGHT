package com.rohkee.welight.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Login

@Serializable
data object Home

@Serializable
data class DisplayDetail(
    val displayId: Long
)

@Serializable
data class DisplayEditor(
    val displayId: Long? = null,
)
