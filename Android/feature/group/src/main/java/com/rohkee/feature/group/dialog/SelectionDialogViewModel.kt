package com.rohkee.feature.group.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.repository.SortType
import com.rohkee.core.ui.component.storage.DisplayCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectionDialogViewModel @Inject constructor(
    private val displayRepository: DisplayRepository,
) : ViewModel() {
    private val _state =
        MutableStateFlow<SelectionDialogState>(SelectionDialogState.Loading)
    val state =
        _state
            .onStart {
                loadData()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SelectionDialogState.Loading,
            )

    val dialogEvent = MutableSharedFlow<SelectionDialogEvent>()

    fun onIntent(intent: SelectionDialogIntent) {
        when (intent) {
            is SelectionDialogIntent.ExitPage -> emitEvent(SelectionDialogEvent.ExitPage)

            is SelectionDialogIntent.SelectDisplay ->
                emitEvent(
                    SelectionDialogEvent.SelectedDisplay(
                        intent.displayId,
                    ),
                )
        }
    }

    private fun emitEvent(event: SelectionDialogEvent) {
        viewModelScope.launch {
            dialogEvent.emit(event)
        }
    }

    private suspend fun loadData() {
        _state.emit(
            SelectionDialogState.Loaded(
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

sealed interface SelectionDialogState {
    data object Loading : SelectionDialogState

    data class Loaded(
        val displayListFlow: Flow<PagingData<DisplayCardState>>,
    ) : SelectionDialogState
}

sealed interface SelectionDialogIntent {
    data object ExitPage : SelectionDialogIntent

    data class SelectDisplay(
        val displayId: Long,
    ) : SelectionDialogIntent
}

sealed interface SelectionDialogEvent {
    data object ExitPage : SelectionDialogEvent

    data class SelectedDisplay(
        val displayId: Long,
    ) : SelectionDialogEvent
}
