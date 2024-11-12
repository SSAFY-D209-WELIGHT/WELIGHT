package com.rohkee.feature.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.repository.SortType
import com.rohkee.core.ui.component.storage.DisplayCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val displayRepository: DisplayRepository,
) : ViewModel() {
    val storageEvent = MutableSharedFlow<StorageEvent>()

    private val _storageState = MutableStateFlow<StorageState>(StorageState.Loading)
    val storageState: StateFlow<StorageState> =
        _storageState
            .onStart {
                loadData()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = StorageState.Loading,
            )

    fun onIntent(intent: StorageIntent) {
        when (intent) {
            is StorageIntent.SelectDisplay -> {
                viewModelScope.launch {
                    storageEvent.emit(StorageEvent.OpenDisplay(intent.displayId))
                }
            }

            is StorageIntent.CreateNewDisplay -> {
                viewModelScope.launch {
                    storageEvent.emit(StorageEvent.CreateNewDisplay)
                }
            }
            is StorageIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    displayRepository.favoriteDisplay(intent.displayId)
                }
            }
        }
    }

    private suspend fun loadData() {
        _storageState.emit(
            StorageState.Loaded(
                displayListFlow =
                displayRepository
                    .getMyDisplayList(
                        sort = SortType.LATEST,
                    ).distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .map { pageData ->
                        pageData.map { display ->
                            DisplayCardState(
                                cardId = display.id,
                                imageSource = display.thumbnailUrl,
                            )
                        }
                    },
            ),
        )
    }
}

sealed interface StorageEvent {
    data class OpenDisplay(
        val id: Long,
    ) : StorageEvent

    data object CreateNewDisplay : StorageEvent

    data class ShowSnackBar(
        val message: String,
    ) : StorageEvent
}
