package com.rohkee.feature.group.client

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val roomId = savedStateHandle.toRoute<ClientRoute>().roomId

    private val clientStateHolder = MutableStateFlow<ClientData>(ClientData(roomId))

    val clientState: StateFlow<ClientState> =
        clientStateHolder
            .onStart {
                loadData()
            }.map { data ->
                data.toState()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ClientState.Loading,
            )

    val clientEvent = MutableSharedFlow<ClientEvent>()

    fun onIntent(intent: ClientIntent) {
        when (intent) {
            // TODO : handle intent
            else -> {}
        }
    }

    private fun emitEvent(event: ClientEvent) {
        viewModelScope.launch {
            clientEvent.emit(event)
        }
    }

    private suspend fun loadData() {
        // TODO: load data from server
    }
}
