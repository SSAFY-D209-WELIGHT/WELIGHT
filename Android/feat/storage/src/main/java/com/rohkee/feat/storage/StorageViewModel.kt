package com.rohkee.feat.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor() : ViewModel() {
    val storageEvent = MutableSharedFlow<StorageEvent>()

    private val _storageState = MutableStateFlow<StorageState>(StorageState.Loading)
    val storageState: StateFlow<StorageState> =
        _storageState
            .onStart {
                // TODO: Load initial data
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
        }
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
