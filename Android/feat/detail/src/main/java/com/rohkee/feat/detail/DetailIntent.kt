package com.rohkee.feat.detail

sealed interface DetailIntent {
    data class Load(
        val displayId: Long,
    ) : DetailIntent
}
