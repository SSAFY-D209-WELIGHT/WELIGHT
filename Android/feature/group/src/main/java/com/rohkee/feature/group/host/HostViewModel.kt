package com.rohkee.feature.group.host

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.datastore.repository.DataStoreRepository
import com.rohkee.core.websocket.Display
import com.rohkee.core.websocket.Location
import com.rohkee.core.websocket.SocketRequest
import com.rohkee.core.websocket.SocketResponse
import com.rohkee.core.websocket.User
import com.rohkee.core.websocket.WebSocketClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
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
    private val dataStoreRepository: DataStoreRepository,
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
                initialValue =
                    HostState.Creation(
                        title = "",
                        description = "",
                        list = persistentListOf(),
                        dialogState = DialogState.Closed,
                    ),
            )

    val hostEvent = MutableSharedFlow<HostEvent>()

    fun onIntent(intent: HostIntent) {
        when (intent) {
            HostIntent.Control.AddDisplayGroup -> hostStateHolder.update { it.copy(dialogState = DialogState.SelectDisplay) }

            is HostIntent.Control.ChangeEffect ->
                hostStateHolder.update {
                    it.copy(effect = intent.effect)
                }

            HostIntent.Control.Exit -> emitEvent(HostEvent.ExitPage)
            HostIntent.Control.StartCheer -> emitEvent(HostEvent.StartCheer(hostStateHolder.value.roomId))
            HostIntent.Creation.Cancel -> emitEvent(HostEvent.ExitPage)
            is HostIntent.Creation.CreateRoom ->
                createRoom(
                    latitude = intent.latitude,
                    longitude = intent.longitude,
                )

            HostIntent.Dialog.Cancel -> hostStateHolder.update { it.copy(dialogState = DialogState.Closed) }
            is HostIntent.Dialog.SelectDisplay ->
                addDisplayGroup(
                    intent.displayId,
                    intent.thumbnailUrl,
                )

            HostIntent.Creation.AddDisplay -> {
                hostStateHolder.update { it.copy(dialogState = DialogState.SelectDisplay) }
            }

            is HostIntent.Creation.UpdateDescription -> hostStateHolder.update { it.copy(description = intent.description) }
            is HostIntent.Creation.UpdateTitle -> hostStateHolder.update { it.copy(title = intent.title) }
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

        viewModelScope.launch {
            webSocketClient.socketEventCallbacks().collect {
                Log.d("TAG", "initialize: $it")
                handleResponse(it)
            }
        }
    }

    private fun handleResponse(response: SocketResponse) {
        when (response) {
            is SocketResponse.DisplayControl -> TODO()
            SocketResponse.Error -> TODO()
            is SocketResponse.GroupChange -> TODO()
            is SocketResponse.RoomCreate -> onRoomCreate(response)
            is SocketResponse.RoomDisplayChange -> TODO()
            is SocketResponse.RoomInfo -> TODO()
            is SocketResponse.RoomJoin -> TODO()
            is SocketResponse.CheerEnd -> TODO()
            is SocketResponse.CheerStart -> TODO()
            is SocketResponse.RoomClose -> TODO()
        }
    }

    private fun createRoom(
        latitude: Double,
        longitude: Double,
    ) {
        viewModelScope.launch {
            dataStoreRepository.getUserId()?.let { userId ->
                val data = hostStateHolder.value

                if (data.list.isEmpty()) return@launch

                webSocketClient.emit(
                    SocketRequest.CreateRoom(
                        title = data.title,
                        description = data.description,
                        location = Location(latitude = latitude, longitude = longitude),
                        displays = data.list.map { Display.Group(it.displayId) },
                        user = User(id = userId),
                    ),
                )
            }
        }
    }

    private fun onRoomCreate(response: SocketResponse.RoomCreate) {
        hostStateHolder.update {
            it.copy(
                roomId = response.roomId,
                title = response.title,
                description = response.description,
                clients = response.clientCount,
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
                dialogState = DialogState.Closed
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketClient.emit(SocketRequest.CloseRoom(hostStateHolder.value.roomId))
        webSocketClient.closeSocket()
    }
}
