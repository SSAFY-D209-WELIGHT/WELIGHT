// BoardViewModel.kt
package com.rohkee.feature.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.rohkee.core.datastore.repository.DataStoreRepository
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.repository.SortType
import com.rohkee.core.ui.component.storage.DisplayCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val displayRepository: DisplayRepository,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {
    private var userId: Long? = null

    private val _uiState = MutableStateFlow<BoardState>(BoardState.Loading)
    val uiState: StateFlow<BoardState> = _uiState.asStateFlow()

    val boardEvent = MutableSharedFlow<BoardEvent>()

    private var searchJob: Job? = null

    init {
        loadBoards()
        viewModelScope.launch {
            userId = dataStoreRepository.getUserId()
        }
    }

    fun onIntent(intent: BoardIntent) {
        when (intent) {
            is BoardIntent.LoadBoards -> loadBoards()
            is BoardIntent.SearchBoards -> updateSearch(intent.query)
            is BoardIntent.ToggleSearch -> toggleSearch()
            is BoardIntent.CloseSearch -> closeSearch()
            is BoardIntent.SelectBoardItem -> emitEvent(BoardEvent.OpenBoardDisplayItem(intent.displayId))
        }
    }

    private fun emitEvent(event: BoardEvent) =
        viewModelScope.launch {
            viewModelScope.launch {
                boardEvent.emit(event)
            }
        }

    private fun loadBoards() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    BoardState.Loaded(
                        boards =
                            displayRepository.getDisplayList(SortType.LATEST).map { page ->
                                page.map { data ->
                                    DisplayCardState(
                                        cardId = data.id,
                                        imageSource = data.thumbnailUrl,
                                    )
                                }
                            },
                    )
                }
            } catch (e: Exception) {
                _uiState.update { BoardState.Error("Failed to load boards") }
            }
        }
    }

    private fun updateSearch(query: String) {
        val currentState = _uiState.value
        if (currentState is BoardState.Loaded) {
            _uiState.update {
                currentState.copy(
                    searchQuery = query,
                )
            }
        }

        searchJob?.cancel()
        if (userId == null || query.isEmpty()) return
        searchJob =
            viewModelScope.launch {
                delay(1000)
                val state = _uiState.value
                if (state is BoardState.Loaded) {
                    _uiState.update {
                        state.copy(
                            boards =
                                displayRepository
                                    .searchDisplayList(userId = userId!!, keyword = query)
                                    .map { page ->
                                        page.map { data ->
                                            DisplayCardState(
                                                cardId = data.id,
                                                imageSource = data.thumbnailUrl,
                                            )
                                        }
                                    },
                        )
                    }
                }
            }
    }

    private var temp: Flow<PagingData<DisplayCardState>>? = null

    private fun toggleSearch() {
        val currentState = _uiState.value
        if (currentState is BoardState.Loaded) {
            _uiState.update {
                currentState.copy(
                    isSearchVisible = !currentState.isSearchVisible,
                )
            }
            temp = currentState.boards
        }
    }

    private fun closeSearch() {
        val currentState = _uiState.value

        if (currentState is BoardState.Loaded) {
            if (currentState.boards == temp) {
                _uiState.update {
                    currentState.copy(
                        isSearchVisible = false,
                        searchQuery = "",
                    )
                }
            } else {
                loadBoards()
            }
        }
    }
}
