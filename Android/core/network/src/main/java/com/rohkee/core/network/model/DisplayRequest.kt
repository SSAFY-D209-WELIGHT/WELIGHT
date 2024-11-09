package com.rohkee.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisplayRequest(
    @SerialName("displayName")
    val title: String,
    @SerialName("displayThumbnailUrl")
    val thumbnailUrl: String,
    @SerialName("displayIsPosted")
    val posted: Boolean = false,
    @SerialName("tags")
    val tags: List<String> = emptyList(),
    @SerialName("images")
    val images: List<DisplayImage> = emptyList(),
    @SerialName("texts")
    val texts: List<DisplayText> = emptyList(),
    @SerialName("background")
    val background: DisplayBackground,
)