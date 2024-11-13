package com.rohkee.core.network.model


data class CheerRecord(
    val participationDate: String, // 참여 날짜
    val cheerroomName: String, // 공연명
    val participantCount: Int, // 참여 인원
    val memo: String, // 메모
    val displays: List<Display>, // 디스플레이 정보
)

data class Display(
    val displayUid: Int,
    val displayName: String,
    val thumbnailUrl: String,
    val usedAt: String,
)
