package com.rohkee.feat.mypage.cheer_record

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.network.repository.CheerRepository
import com.rohkee.core.network.util.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheerRecordViewModel @Inject constructor(
    private val cheerRepository: CheerRepository,
) : ViewModel() {
    private val _cheerRecords = MutableStateFlow<CheerRecordUIState>(CheerRecordUIState.Loading)
    val cheerRecords: StateFlow<CheerRecordUIState> = _cheerRecords.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        if(_cheerRecords.value is CheerRecordUIState.Loaded) {
            _cheerRecords.update { state ->
                (state as CheerRecordUIState.Loaded).copy(isRefreshing = true)
            }
        }
        viewModelScope.launch {
            cheerRepository.getCheerRecords().handle(
                onSuccess = { records ->
                    _cheerRecords.update {
                        if (records.isNullOrEmpty()) {
                            CheerRecordUIState.Loaded(cheerRecords = persistentListOf())
                        } else {
                            CheerRecordUIState.Loaded(
                                cheerRecords =
                                    records
                                        .map { record ->
                                            CheerRecordCardState(
                                                participationDate = record.participationDate,
                                                cheerRoomName = record.cheerRoomName,
                                                participantCount = record.participantCount,
                                                memo = record.memo ?: "",
                                                thumbnailUrl = record.displays.firstOrNull()?.thumbnailUrl,
                                            )
                                        }.toPersistentList(),
                                isRefreshing = false
                            )
                        }
                    }
                },
                onError = { _, message ->
                    Log.d("TAG", "loadData: $message")
                    // TODO 에러 처리
                },
            )
        }
    }

    fun onRefresh() {
        loadData()
    }
}
