
// BoardViewModel.kt
package com.rohkee.feature.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.repository.SortType
import com.rohkee.feature.board.data.BoardDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val displayRepository: DisplayRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<BoardUiState>(BoardUiState.Loading)
    val uiState: StateFlow<BoardUiState> = _uiState.asStateFlow()

    init {
        loadBoards()
    }

    fun handleIntent(intent: BoardIntent) {
        when (intent) {
            is BoardIntent.LoadBoards -> loadBoards()
            is BoardIntent.SearchBoards -> updateSearch(intent.query)
            is BoardIntent.ToggleSearch -> toggleSearch()
            is BoardIntent.CloseSearch -> closeSearch()
        }
    }

    private fun loadBoards() {
        viewModelScope.launch {
            try {
                val boardsFlow = displayRepository.getDisplayList(SortType.LATEST)
                _uiState.value = BoardUiState.Success(
                    BoardContent(boards = boardsFlow)
                )
            } catch (e: Exception) {
                _uiState.value = BoardUiState.Error("Failed to load boards")
            }
        }
    }

    private fun updateSearch(query: String) {
        val currentState = _uiState.value
        if (currentState is BoardUiState.Success) {
            _uiState.value = BoardUiState.Success(
                currentState.content.copy(searchQuery = query)
            )
        }
    }

    private fun toggleSearch() {
        val currentState = _uiState.value
        if (currentState is BoardUiState.Success) {
            _uiState.value = BoardUiState.Success(
                currentState.content.copy(isSearchVisible = !currentState.content.isSearchVisible)
            )
        }
    }

    private fun closeSearch() {
        val currentState = _uiState.value
        if (currentState is BoardUiState.Success) {
            _uiState.value = BoardUiState.Success(
                currentState.content.copy(
                    isSearchVisible = false,
                    searchQuery = ""
                )
            )
        }
    }
}


// BoardViewModel.kt
//@HiltViewModel
//class BoardViewModel @Inject constructor(
//    private val displayRepository: DisplayRepository,
//) : ViewModel() {
//    private val dataSource = BoardDataSource()
//
//    private val _uiState = MutableStateFlow<BoardUiState>(BoardUiState.Loading)
//    val uiState: StateFlow<BoardUiState> = _uiState.asStateFlow()
//
//    init {
//        loadBoards()
//    }
//
//    fun handleIntent(intent: BoardIntent) {
//        when (intent) {
//            is BoardIntent.LoadBoards -> loadBoards()
//            is BoardIntent.SearchBoards -> updateSearch(intent.query)
//            is BoardIntent.ToggleSearch -> toggleSearch()
//            is BoardIntent.CloseSearch -> closeSearch()
//        }
//    }
//
//    private fun loadBoards() {
//        viewModelScope.launch {
//            try {
//                val boards = dataSource.loadBoards()
//                _uiState.value = BoardUiState.Success(
//                    BoardContent(boards = boards)
//                )
//            } catch (e: Exception) {
//                _uiState.value = BoardUiState.Error("Failed to load boards")
//            }
//        }
//    }
//
//    private fun updateSearch(query: String) {
//        val currentState = _uiState.value
//        if (currentState is BoardUiState.Success) {
//            _uiState.value = BoardUiState.Success(
//                currentState.content.copy(searchQuery = query)
//            )
//        }
//    }
//
//    private fun toggleSearch() {
//        val currentState = _uiState.value
//        if (currentState is BoardUiState.Success) {
//            _uiState.value = BoardUiState.Success(
//                currentState.content.copy(isSearchVisible = !currentState.content.isSearchVisible)
//            )
//        }
//    }
//
//    private fun closeSearch() {
//        val currentState = _uiState.value
//        if (currentState is BoardUiState.Success) {
//            _uiState.value = BoardUiState.Success(
//                currentState.content.copy(
//                    isSearchVisible = false,
//                    searchQuery = ""
//                )
//            )
//        }
//    }
//}
