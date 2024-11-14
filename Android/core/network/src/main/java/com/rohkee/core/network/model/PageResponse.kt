package com.rohkee.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageResponse<T>(
    @SerialName("currentPage")
    val currentPage: Int,
    @SerialName("displays")
    val displays: List<T>,
)

@Serializable
data class PageSearchResponse<T>(
    @SerialName("content")
    val content: List<T>,
)
