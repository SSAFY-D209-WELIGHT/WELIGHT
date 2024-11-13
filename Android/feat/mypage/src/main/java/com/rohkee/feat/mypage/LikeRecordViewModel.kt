package com.rohkee.feat.mypage

import androidx.lifecycle.ViewModel
import com.rohkee.core.network.model.Display
import com.rohkee.core.network.model.DisplayResponse
import com.rohkee.core.network.repository.DisplayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LikeRecordViewModel @Inject constructor(
    private val displayRepository: DisplayRepository,
) : ViewModel() {
    private val _likedDisplays = MutableStateFlow<List<Display>>(emptyList())
    val likedDisplays: StateFlow<List<Display>> = _likedDisplays.asStateFlow()

    init {
        getDummyDisplays()
    }

//    private fun loadLikedDisplays() {
//        viewModelScope.launch {
//            when (val response = displayRepository.getLikedDisplays(page = 0, size = 20)) {
//                is ApiResponse.Success -> {
//                    _likedDisplays.value = response.body ?: emptyList()
//                }
//
//                is ApiResponse.Error -> {
//                    // 에러 처리
//                }
//            }
//        }
//    }

    private fun getDummyDisplays(): List<DisplayResponse.Liked> =
        List(20) { index ->
            DisplayResponse.Liked(
                id = index.toLong(),
                thumbnailUrl = "https://picsum.photos/200/${300 + index}",
            )
        }
}
