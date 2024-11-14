package com.rohkee.feat.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.ui.component.storage.DisplayCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LikedDisplaysState {
    data object Loading : LikedDisplaysState

    data class Loaded(
        val displayListFlow: Flow<PagingData<DisplayCardState>>,
    ) : LikedDisplaysState

    data class Error(
        val message: String,
    ) : LikedDisplaysState
}

@HiltViewModel
class LikeRecordViewModel @Inject constructor(
    private val displayRepository: DisplayRepository,
) : ViewModel() {
    private val _likedDisplaysState =
        MutableStateFlow<LikedDisplaysState>(LikedDisplaysState.Loading)
    val likedDisplaysState: StateFlow<LikedDisplaysState> = _likedDisplaysState.asStateFlow()

    init {
        loadDummyDisplays()
    }

//    private fun loadLikedDisplays() {
//        viewModelScope.launch {
//            try {
//                _likedDisplaysState.emit(
//                    LikedDisplaysState.Loaded(
//                        displayListFlow =
//                            displayRepository
//                                .getLikedDisplayList()
//                                .map { pagingData ->
//                                    pagingData.map { display ->
//                                        DisplayCardState(
//                                            cardId = display.id,
//                                            imageSource = display.thumbnailUrl,
//                                        )
//                                    }
//                                }.cachedIn(viewModelScope),
//                    ),
//                )
//            } catch (e: Exception) {
//                _likedDisplaysState.emit(LikedDisplaysState.Error(e.message ?: "Unknown error"))
//            }
//        }
//    }

    fun loadDummyDisplays() {
        viewModelScope.launch {
            _likedDisplaysState.emit(LikedDisplaysState.Loading) // 로딩 상태로 전환
            kotlinx.coroutines.delay(500) // 로딩 상태 시뮬레이션

            // 더미 데이터 생성
            val dummyData = getDummyDisplays()

            // 성공 상태로 전환
            val displayListFlow =
                kotlinx.coroutines.flow.flow {
                    emit(PagingData.from(dummyData))
                }
            _likedDisplaysState.emit(LikedDisplaysState.Loaded(displayListFlow))
        }
    }

    // 테스트용 더미 데이터는 Repository 레이어로 이동하는 것을 추천드립니다
    private fun getDummyDisplays(): List<DisplayCardState> =
        List(20) { index ->
            DisplayCardState(
                cardId = index.toLong(),
                imageSource = "https://picsum.photos/200/${300 + index}",
            )
        }
}
