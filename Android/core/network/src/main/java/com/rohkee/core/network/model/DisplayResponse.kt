package com.rohkee.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

sealed interface DisplayResponse {
    val id: Long

    @Serializable
    data class WithFavorite(
        @SerialName("displayUid")
        override val id: Long,
        @SerialName("displayThumbnail")
        val thumbnailUrl: String,
        @SerialName("favorite")
        val favorite: Boolean,
    ) : DisplayResponse

    @Serializable
    data class Simple(
        @SerialName("displayUid")
        override val id: Long,
        @SerialName("displayThumbnail")
        val thumbnailUrl: String,
    ) : DisplayResponse

    @Serializable
    data class Detail(
        @Transient
        override val id: Long = 0,
        @SerialName("creatorUid")
        val authorId: Long,
        @SerialName("creatorName")
        val authorName: String,
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
        @SerialName("stored")
        val stored: Boolean,
        @SerialName("likeCount")
        val likes: Int,
        @SerialName("downloadCount")
        val downloads: Int,
        @SerialName("commentCount")
        val comments: Int,
        @SerialName("liked")
        val liked: Boolean,
        @SerialName("favorite")
        val favorite: Boolean,
    ) : DisplayResponse

    @Serializable
    data class Editable(
        @Transient
        override val id: Long = 0,
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
    data class Search(
        @SerialName("displayUid")
        override val id: Long,
        @SerialName("creatorUid")
        val authorId: Long,
        @SerialName("displayName")
        val title: String,
        @SerialName("displayThumbnailUrl")
        val thumbnailUrl: String,
        @SerialName("displayIsPosted")
        val posted: Boolean,
        @SerialName("displayCreatedAt")
        val createdAt: String,
        @SerialName("displayDownloadCount")
        val downloads: Int,
        @SerialName("displayLikeCount")
        val likes: Int,
        @SerialName("tags")
        val tags: List<String> = emptyList(),
        @SerialName("displayTexts")
        val texts: List<String> = emptyList(),
    ) : DisplayResponse

    @Serializable
    data class Liked(
        @Transient
        override val id: Long = 0,
        @SerialName("message")
        val message: String,
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

    @Serializable
    data class Published(
        @SerialName("displayUid")
        override val id: Long,
        @SerialName("displayIsPosted")
        val published: Boolean,
    ) : DisplayResponse

    @Serializable
    data class Deleted(
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
