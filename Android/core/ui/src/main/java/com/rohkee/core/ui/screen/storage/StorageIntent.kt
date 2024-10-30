package com.rohkee.core.ui.screen.storage

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
     * 새로운 응원도구 생성
     */
    data object CreateNewDisplay : StorageIntent
}
