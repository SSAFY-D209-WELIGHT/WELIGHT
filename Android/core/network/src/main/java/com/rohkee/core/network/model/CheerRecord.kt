package com.rohkee.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheerRecord(
    @SerialName("participationData")
    val participationDate: String, // 참여 날짜
    @SerialName("cheerroomName")
    val cheerRoomName: String, // 공연명
    @SerialName("participantCount")
    val participantCount: Int, // 참여 인원
    @SerialName("memo")
    val memo: String, // 메모
    @SerialName("displays")
    val displays: List<DisplayRecord>, // 디스플레이 정보
)

@Serializable
data class DisplayRecord(
    @SerialName("displayUid")
    val displayUid: Int,
    @SerialName("displayName")
    val displayName: String,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerialName("usedAt")
    val usedAt: String,
)
