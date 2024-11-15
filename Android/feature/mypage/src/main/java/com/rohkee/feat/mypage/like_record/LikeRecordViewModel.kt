package com.rohkee.feat.mypage.like_record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.ui.component.storage.DisplayCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
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
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _likedDisplaysState.update {
                LikedDisplaysState.Loaded(
                    displayListFlow =
                        displayRepository
                            .getLikedDisplayList()
                            .distinctUntilChanged()
                            .cachedIn(viewModelScope)
                            .map { pageData ->
                                pageData.map { display ->
                                    DisplayCardState(
                                        cardId = display.id,
                                        imageSource = display.thumbnailUrl,
                                    )
                                }
                            },
                )
            }
        }
    }
}
