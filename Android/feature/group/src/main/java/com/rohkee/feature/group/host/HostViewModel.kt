package com.rohkee.feature.group.host

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.websocket.SocketResponse
import com.rohkee.core.websocket.WebSocketClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostViewModel @Inject constructor(
    private val webSocketClient: WebSocketClient,
) : ViewModel() {
    private val hostStateHolder = MutableStateFlow<HostData>(HostData())

    val hostState: StateFlow<HostState> =
        hostStateHolder
            .onStart {
                initialize()
            }.map { data ->
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
            is HostIntent.Creation.Confirm -> createRoom(intent.title, intent.description)

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

    private suspend fun initialize() {
        webSocketClient.initializeSocket(
            onConnect = {
                Log.d("TAG", "initialize: connected")
            },
            onDisconnect = {
                // TODO : 연결 종료
                emitEvent(HostEvent.ExitPage)
            },
            onConnectionError = {
                // TODO : 연결 에러
                emitEvent(HostEvent.ExitPage)
            },
        )
        webSocketClient.setupSocketEvents().collect {
        }
    }

    private fun handleResponse(response: SocketResponse) {
        when (response) {
            SocketResponse.CheerEnd -> TODO()
            SocketResponse.CheerStart -> TODO()
            is SocketResponse.DisplayControl -> TODO()
            SocketResponse.Error -> TODO()
            is SocketResponse.GroupSelect -> TODO()
            is SocketResponse.RoomCreate -> TODO()
            is SocketResponse.RoomDisplayChange -> TODO()
            is SocketResponse.RoomInfoReceive -> TODO()
            is SocketResponse.RoomJoin -> TODO()
        }
    }

    private fun createRoom(
        title: String,
        description: String,
    ) {
        webSocketClient

        val roomId = 1L // TODO 방 id

        hostStateHolder.update {
            it.copy(
                roomId = roomId,
                title = intent.title,
                description = intent.description,
            )
        }
    }

    private fun addDisplayGroup(
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

    override fun onCleared() {
        super.onCleared()
        webSocketClient.closeSocket()
    }
}
