package com.rohkee.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface DisplayResponse {
    val id: Long

    @Serializable
    data class Short(
        @SerialName("displayUid")
        override val id: Long,
        @SerialName("displayThumbnail")
        val thumbnailUrl: String,
    ) : DisplayResponse

    @Serializable
    data class Detail(
        @SerialName("displayUid")
        override val id: Long,
        @SerialName("displayThumbnailUrl")
        val thumbnailUrl: String,
        @SerialName("displayName")
        val title: String,
        @SerialName("displayIsPosted")
        val posted: Boolean,
        @SerialName("tags")
        val tags: List<String> = emptyList(),
        @SerialName("owner")
        val isOwner: Boolean,
        @SerialName("likeCount")
        val likes: Int,
        @SerialName("downloadCount")
        val downloads: Int,
        @SerialName("commentCount")
        val comments: Int,
        @SerialName("favorite")
        val favorite: Boolean,
    ) : DisplayResponse

    @Serializable
    data class Editable(
        @SerialName("displayUid")
        override val id: Long,
        @SerialName("displayThumbnailUrl")
        val thumbnailUrl: String,
        @SerialName("displayName")
        val title: String,
        @SerialName("displayIsPosted")
        val posted: Boolean,
        @SerialName("tags")
        val tags: List<String> = emptyList(),
        @SerialName("images")
        val images: List<DisplayImage> = emptyList(),
        @SerialName("texts")
        val texts: List<DisplayText> = emptyList(),
        @SerialName("background")
        val background: DisplayBackground,
    ) : DisplayResponse

    @Serializable
    data class Posted(
        @SerialName("displayUid")
        override val id: Long,
        @SerialName("displayName")
        val title: String,
        @SerialName("message")
        val message: String,
    ) : DisplayResponse
}

@Serializable
data class DisplayImage(
    @SerialName("displayImgUrl")
    val url: String,
    @SerialName("displayImgRotation")
    val rotation: Float,
    @SerialName("displayImgScale")
    val scale: Float,
    @SerialName("displayImgOffsetx")
    val offsetX: Float,
    @SerialName("displayImgOffsety")
    val offsetY: Float,
    @SerialName("displayImgColor")
    val color: String,
)

@Serializable
data class DisplayText(
    @SerialName("displayTextDetail")
    val text: String,
    @SerialName("displayTextColor")
    val color: String,
    @SerialName("displayTextFont")
    val font: String,
    @SerialName("displayTextRotation")
    val rotation: Float,
    @SerialName("displayTextScale")
    val scale: Float,
    @SerialName("displayTextOffsetx")
    val offsetX: Float,
    @SerialName("displayTextOffsety")
    val offsetY: Float,
)

@Serializable
data class DisplayBackground(
    @SerialName("displayBackgroundBrightness")
    val brightness: Float,
    @SerialName("displayColorSolid")
    val isSingle: Boolean,
    @SerialName("displayBackgroundGradationColor1")
    val color1: String,
    @SerialName("displayBackgroundGradationColor2")
    val color2: String,
    @SerialName("displayBackgroundGradationType")
    val type: String,
)