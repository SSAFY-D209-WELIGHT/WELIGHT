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
            HostIntent.Control.AddDisplayGroup -> emitEvent(HostEvent.ChooseDisplayForNewGroup)
            is HostIntent.Control.ChangeEffect ->
                hostStateHolder.update {
                    it.copy(effect = intent.effect)
                }

            HostIntent.Control.Exit -> emitEvent(HostEvent.ExitPage)
            HostIntent.Control.StartCheer -> emitEvent(HostEvent.StartCheer(hostStateHolder.value.roomId))
            HostIntent.Creation.Cancel -> emitEvent(HostEvent.ExitPage)
            is HostIntent.Creation.Confirm -> {
                // TODO : 방 생성

                val roomId = 1L // TODO 방 id

                hostStateHolder.update {
                    it.copy(
                        roomId = roomId,
                        title = intent.title,
                        description = intent.description,
                    )
                }
            }

            HostIntent.Dialog.Cancel -> hostStateHolder.update { it.copy(dialogState = DialogState.Closed) }
            is HostIntent.Dialog.SelectDisplay ->
                addDisplayGroup(
                    intent.displayId,
                    intent.thumbnailUrl,
                )
        }
    }

    private fun emitEvent(event: HostEvent) {
        viewModelScope.launch {
            hostEvent.emit(event)
        }
    }

    fun addDisplayGroup(
        displayId: Long,
        thumbnailUrl: String,
    ) {
        // TODO 그룹 추가
        hostStateHolder.update {
            it.copy(
                list =
                    it.list +
                        GroupDisplayData(
                            displayId,
                            thumbnailUrl,
                        ),
            )
        }
    }
}
