package com.rohkee.core.network.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisplayRequest @OptIn(ExperimentalSerializationApi::class) constructor(
    @SerialName("displayName")
    val title: String,
    @SerialName("displayThumbnailUrl")
    val thumbnailUrl: String,
    @SerialName("displayIsPosted")
    @EncodeDefault
    val posted: Boolean = false,
    @SerialName("tags")
    @EncodeDefault
    val tags: List<String> = emptyList(),
    @SerialName("images")
    val images: List<DisplayImage> = emptyList(),
    @SerialName("texts")
    val texts: List<DisplayText> = emptyList(),
    @SerialName("background")
    val background: DisplayBackground,
)