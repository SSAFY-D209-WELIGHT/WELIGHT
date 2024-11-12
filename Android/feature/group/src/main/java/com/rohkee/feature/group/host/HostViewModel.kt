package com.rohkee.feature.group.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostViewModel @Inject constructor() : ViewModel() {
    private val hostStateHolder = MutableStateFlow<HostData>(HostData())

    val hostState: StateFlow<HostState> =
        hostStateHolder
            .map { data ->
                data.toState()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HostState.Creation,
            )

    val hostEvent = MutableSharedFlow<HostEvent>()

    fun onIntent(intent: HostIntent) {
        when (intent) {
            HostIntent.Control.AddDisplayGroup -> TODO()
            is HostIntent.Control.ChangeEffect -> TODO()
            HostIntent.Control.Exit -> TODO()
            HostIntent.Control.StartCheer -> TODO()
            HostIntent.Creation.Cancel -> emitEvent(HostEvent.ExitPage)
            is HostIntent.Creation.Confirm -> TODO()
        }
    }

    private fun emitEvent(event: HostEvent) {
        viewModelScope.launch {
            hostEvent.emit(event)
        }
    }

    private fun addDisplayGroup() {

    }
}
