package com.rohkee.feat.storage

/**
 * 보관함 액션
 */
interface StorageIntent {
    /**
     * 응원도구 선택
     */
    data class SelectDisplay(
        val displayId: Long,
    ) : StorageIntent

    /**
     * 응원도구 즐겨찾기 토글
     */
    data class ToggleFavorite(
        val displayId: Long,
    ) : StorageIntent

    /**
     * 새로운 응원도구 생성
     */
    data object CreateNewDisplay : StorageIntent
}
