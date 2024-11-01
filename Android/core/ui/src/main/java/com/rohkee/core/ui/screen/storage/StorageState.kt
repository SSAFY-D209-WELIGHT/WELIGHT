package com.rohkee.core.ui.screen.storage

import com.rohkee.core.ui.component.storage.DisplayCardState

/**
 * 보관함 화면 상태
 */
sealed interface StorageState {
    /**
     * 로딩중
     */
    data object Loading : StorageState

    /**
     * 로딩 완료
     * 저장된 응원도구가 존재
     */
    data class Loaded(
        val displayList: List<DisplayCardState>,
    ) : StorageState

    /**
     * 저장된 응원도구 데이터 없음
     */
    data object NoData : StorageState

    /**
     * 에러
     */
    data class Error(
        val message: String,
    ) : StorageState
}
