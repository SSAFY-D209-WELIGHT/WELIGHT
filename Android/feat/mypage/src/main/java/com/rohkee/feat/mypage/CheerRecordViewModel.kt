package com.rohkee.feat.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.model.CheerRecord
import com.rohkee.core.network.model.Display
import com.rohkee.core.network.repository.CheerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheerRecordViewModel @Inject constructor(
    private val repository: CheerRepository,
) : ViewModel() {
    private val _cheerRecords = MutableStateFlow<List<CheerRecord>>(emptyList())
    val cheerRecords: StateFlow<List<CheerRecord>> = _cheerRecords.asStateFlow()

    init {
        loadDummyData()
    }

    fun loadCheerRecords() {
        viewModelScope.launch {
            try {
                val response = repository.getCheerRecords()
                if (response is ApiResponse.Success) {
                    _cheerRecords.value = response.body ?: emptyList()
                }
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    private fun loadDummyData() {
        _cheerRecords.value =
            listOf(
                CheerRecord(
                    participationDate = "2024-11-13",
                    cheerroomName = "Amazing Cheer Event",
                    participantCount = 123,
                    memo = "This was an awesome event!",
                    displays =
                        listOf(
                            Display(
                                displayUid = 1,
                                displayName = "Main Stage",
                                thumbnailUrl = "https://cdn.pixabay.com/photo/2024/02/17/00/18/cat-8578562_1280.jpg",
                                usedAt = "2024-11-13T05:14:15.950Z",
                            ),
                        ),
                ),
                CheerRecord(
                    participationDate = "2024-11-12",
                    cheerroomName = "Cheer Fest 2024",
                    participantCount = 98,
                    memo = "Great vibes and energy!",
                    displays =
                        listOf(
                            Display(
                                displayUid = 2,
                                displayName = "Side Stage",
                                thumbnailUrl = "https://cdn.pixabay.com/photo/2024/02/17/00/18/cat-8578562_1280.jpg",
                                usedAt = "2024-11-12T08:00:00.000Z",
                            ),
                        ),
                ),
                CheerRecord(
                    participationDate = "2024-11-12",
                    cheerroomName = "Cheer Fest 2024",
                    participantCount = 98,
                    memo = "Great vibes and energy!",
                    displays =
                        listOf(
                            Display(
                                displayUid = 3,
                                displayName = "Side Stage",
                                thumbnailUrl = "https://cdn.pixabay.com/photo/2024/02/17/00/18/cat-8578562_1280.jpg",
                                usedAt = "2024-11-12T08:00:00.000Z",
                            ),
                        ),
                ),
            )
    }
}
