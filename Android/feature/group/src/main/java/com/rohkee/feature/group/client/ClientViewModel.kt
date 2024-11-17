package com.rohkee.feature.group.client

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rohkee.core.datastore.repository.DataStoreRepository
import com.rohkee.core.websocket.SocketRequest
import com.rohkee.core.websocket.SocketResponse
import com.rohkee.core.websocket.User
import com.rohkee.core.websocket.WebSocketClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

private const val TAG = "ClientViewModel"

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dataStoreRepository: DataStoreRepository,
    private val webSocketClient: WebSocketClient,
) : ViewModel() {
    private val roomId = savedStateHandle.toRoute<ClientRoute>().roomId

    private val clientStateHolder = MutableStateFlow<ClientData>(ClientData(roomId))

    val clientState: StateFlow<ClientState> =
        clientStateHolder
            .onStart {
                initialize()
            }.map { data ->
                data.toState()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ClientState.Loading,
            )

    val clientEvent = MutableSharedFlow<ClientEvent>()

    private var callback: Job? = null

    fun onIntent(intent: ClientIntent) {
        when (intent) {
            // TODO : handle intent
            is ClientIntent.ExitPage -> {
                webSocketClient.emit(SocketRequest.LeaveRoom(clientStateHolder.value.roomId))
                emitEvent(ClientEvent.ExitPage)
            }

            is ClientIntent.ChangeGroup -> {
                webSocketClient.emit(
                    SocketRequest.ChangeGroup(
                        roomId = clientStateHolder.value.roomId,
                        groupNumber = intent.groupNumber,
                    ),
                )
            }
        }
    }

    private fun emitEvent(event: ClientEvent) {
        viewModelScope.launch {
            clientEvent.emit(event)
        }
    }

    private suspend fun initialize() {
        webSocketClient.initializeSocket(
            onConnect = {
                Log.d(TAG, "initialize: connected")
                callback?.cancel()
                callback =
                    viewModelScope.launch {
                        webSocketClient.socketEventCallbacks().collect {
                            Log.d(TAG, "initialize: $it")
                            handleResponse(it)
                        }
                    }
                joinRoom()
            },
            onDisconnect = {
                // TODO : 연결 종료
                Log.d(TAG, "initialize: disconnected")
                callback?.cancel()
                emitEvent(ClientEvent.ExitPage)
            },
            onConnectionError = {
                // TODO : 연결 에러
                Log.d(TAG, "initialize: connection error")
                callback?.cancel()
                emitEvent(ClientEvent.ExitPage)
            },
        )
    }

    private fun handleResponse(response: SocketResponse) {
        when (response) {
            is SocketResponse.DisplayControl ->
                onDisplayControl(
                    displayId = response.displayId,
                    offset = response.offset,
                    interval = response.interval,
                )

            SocketResponse.Error -> {
                // TODO : Error handling
            }

            is SocketResponse.GroupChange -> onGroupChanged(response.groupNumber)

            is SocketResponse.RoomCreate -> {} // 클라이언트는 처리할 필요 없음
            is SocketResponse.RoomDisplayChange ->
                onDisplayChanged(
                    displays = response.displays.map { it.displayId },
                )

            is SocketResponse.RoomInfo ->
                onRoomInfoChanged(
                    title = response.title,
                    description = response.description,
                    participants = response.clientCount,
                )

            is SocketResponse.RoomJoin ->
                onRoomJoined(
                    title = response.title,
                    description = response.description,
                    clientCount = response.clientCount,
                    groupNumber = response.groupNumber,
                    display = response.displays.first().displayId,
                    groupSize = response.displays.size,
                )

            is SocketResponse.CheerEnd -> onCheerEnd()

            is SocketResponse.CheerStart -> onCheerStart()
            is SocketResponse.RoomClose -> onRoomClose()
        }
    }

    private fun joinRoom() {
        viewModelScope.launch {
            dataStoreRepository.getUserId()?.let { userId ->
                webSocketClient.emit(
                    SocketRequest.JoinRoom(
                        roomId = clientStateHolder.value.roomId,
                        user = User(userId),
                    ),
                )
            }
        }
    }

    private fun onRoomClose() {
        emitEvent(ClientEvent.ExitPage)
    }

    private fun onCheerStart() {
        clientStateHolder.update {
            it.copy(
                dialogState =
                    ClientDialogState.StartCheer(
                        displayId = it.displayId!!,
                        offset = 0f,
                        interval = 0f,
                    ),
            )
        }
    }

    private fun onCheerEnd() {
        clientStateHolder.update { it.copy(dialogState = ClientDialogState.Closed) }
    }

    private fun onDisplayChanged(displays: List<Long>) {
        if (clientStateHolder.value.groupNumber >= displays.size) {
            clientStateHolder.update {
                it.copy(groupNumber = displays.size, displayId = displays.last())
            }
        } else {
            clientStateHolder.update {
                it.copy(displayId = displays[clientStateHolder.value.groupNumber - 1])
            }
        }
    }

    private fun onRoomJoined(
        title: String,
        description: String,
        clientCount: Int,
        groupNumber: Int,
        groupSize: Int,
        display: Long,
    ) {
        clientStateHolder.update {
            it.copy(
                title = title,
                description = description,
                participants = clientCount,
                groupNumber = groupNumber,
                displayId = display,
                groupSize = groupSize,
            )
        }
    }

    private fun onDisplayControl(
        displayId: Long,
        offset: Float,
        interval: Float,
    ) {
        if (clientStateHolder.value.dialogState is ClientDialogState.StartCheer) {
            clientStateHolder.update {
                it.copy(
                    dialogState =
                        ClientDialogState.StartCheer(
                            displayId = displayId,
                            offset = offset,
                            interval = interval,
                        ),
                )
            }
        }
    }

    private fun onGroupChanged(groupNumber: Int) {
        clientStateHolder.update {
            it.copy(groupNumber = groupNumber)
        }
    }

    private fun onRoomInfoChanged(
        title: String,
        description: String,
        participants: Int,
    ) {
        clientStateHolder.update {
            it.copy(
                title = title,
                description = description,
                participants = participants,
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketClient.closeSocket()
    }
}
