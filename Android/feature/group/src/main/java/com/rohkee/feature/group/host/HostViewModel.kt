package com.rohkee.feature.group.host

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.audio.TempoDetector
import com.rohkee.core.datastore.repository.DataStoreRepository
import com.rohkee.core.websocket.Display
import com.rohkee.core.websocket.Location
import com.rohkee.core.websocket.SocketRequest
import com.rohkee.core.websocket.SocketResponse
import com.rohkee.core.websocket.User
import com.rohkee.core.websocket.WebSocketClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

private const val TAG = "HostViewModel"

@HiltViewModel
class HostViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val webSocketClient: WebSocketClient,
    private val tempoDetector: TempoDetector,
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
                        hostDialogState = HostDialogState.Closed,
                    ),
            )

    val hostEvent = MutableSharedFlow<HostEvent>()

    private var callback: Job? = null

    fun onIntent(intent: HostIntent) {
        when (intent) {
            HostIntent.Control.AddDisplayGroup -> hostStateHolder.update { it.copy(hostDialogState = HostDialogState.SelectDisplay) }

            is HostIntent.Control.ChangeEffect ->
                hostStateHolder.update {
                    it.copy(effect = intent.effect)
                }

            HostIntent.Control.Exit -> emitEvent(HostEvent.ExitPage)
            HostIntent.Control.StartCheer -> startCheer()

            HostIntent.Creation.Cancel -> emitEvent(HostEvent.ExitPage)
            is HostIntent.Creation.CreateRoom ->
                createRoom(
                    latitude = intent.latitude,
                    longitude = intent.longitude,
                )

            HostIntent.SelectionDialog.Cancel -> hostStateHolder.update { it.copy(hostDialogState = HostDialogState.Closed) }
            is HostIntent.SelectionDialog.SelectDisplay ->
                addDisplayGroup(
                    intent.displayId,
                    intent.thumbnailUrl,
                )

            HostIntent.Creation.AddDisplay -> {
                hostStateHolder.update { it.copy(hostDialogState = HostDialogState.SelectDisplay) }
            }

            is HostIntent.Creation.UpdateDescription -> hostStateHolder.update { it.copy(description = intent.description) }
            is HostIntent.Creation.UpdateTitle -> hostStateHolder.update { it.copy(title = intent.title) }
            HostIntent.CheerDialog.Cancel -> endCheer()
            is HostIntent.Control.ChangeInterval -> hostStateHolder.update { it.copy(interval = intent.interval) }
            is HostIntent.Control.ToggleDetect -> hostStateHolder.update { it.copy(doDetect = intent.doDetect) }
            HostIntent.Permission.Granted -> hostStateHolder.update { it.copy(hasPermission = true) }
            HostIntent.Permission.Rejected -> hostStateHolder.update { it.copy(hasPermission = false) }
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
                callback?.cancel()
                callback =
                    viewModelScope.launch {
                        webSocketClient.socketEventCallbacks().collect {
                            Log.d("TAG", "initialize: $it")
                            handleResponse(it)
                        }
                    }
            },
            onDisconnect = {
                // TODO : 연결 종료
                callback?.cancel()
                emitEvent(HostEvent.ExitPage)
            },
            onConnectionError = {
                // TODO : 연결 에러
                callback?.cancel()
                emitEvent(HostEvent.ExitPage)
            },
        )
    }

    private fun handleResponse(response: SocketResponse) {
        when (response) {
            is SocketResponse.DisplayControl ->
                displayControl(
                    displayId = response.displayId,
                    offset = response.offset,
                    interval = response.interval,
                )

            SocketResponse.Error -> {
                // TODO : Error handling
            }

            is SocketResponse.GroupChange -> {
                // 호스트는 처리할 필요 없음
            }

            is SocketResponse.RoomCreate -> onRoomCreate(response)
            is SocketResponse.RoomDisplayChange -> {
                // 호스트는 처리할 필요 없음
            }

            is SocketResponse.RoomInfo ->
                changeRoomInfo(
                    title = response.title,
                    description = response.description,
                    clients = response.clientCount,
                )

            is SocketResponse.RoomJoin -> {
                // 호스트는 처리할 필요 없음
            }

            is SocketResponse.CheerEnd -> {
                // 호스트는 처리할 필요 없음
            }

            is SocketResponse.CheerStart -> onCheerStart()
            is SocketResponse.RoomClose -> {
                // 호스트는 처리할 필요 없음
            }
        }
    }

    private fun startCheer() {
        webSocketClient.emit(SocketRequest.StartCheer(hostStateHolder.value.roomId))
        onCheerStart()
    }

    private fun endCheer() {
        webSocketClient.emit(SocketRequest.EndCheer(hostStateHolder.value.roomId))
        tempoDetector.stopDetection()
        hostStateHolder.update { it.copy(hostDialogState = HostDialogState.Closed) }
    }

    private fun displayControl(
        displayId: Long,
        offset: Float,
        interval: Float,
    ) {
        if (hostStateHolder.value.hostDialogState is HostDialogState.StartCheer) {
            hostStateHolder.update {
                it.copy(
                    hostDialogState =
                        HostDialogState.StartCheer(
                            displayId = displayId,
                            offset = offset,
                            interval = interval,
                        ),
                )
            }
        }
    }

    private fun changeRoomInfo(
        title: String,
        description: String,
        clients: Int,
    ) {
        hostStateHolder.update {
            it.copy(
                title = title,
                description = description,
                clients = clients,
            )
        }
    }

    private fun onCheerStart() {
        // TODO : 응원 시작 로직
        viewModelScope.launch {
            val state = hostStateHolder.value
            val interval =
                (if (state.effect == DisplayEffect.NONE || state.doDetect) 0.0f else state.interval) * 1000

            Log.d(TAG, "onCheerStart: $state $interval")
            webSocketClient.emit(
                SocketRequest.ControlDisplay(
                    roomId = state.roomId,
                    displays =
                        state.list.mapIndexed { index, groupDisplayData ->
                            Display.Control(
                                displayId = groupDisplayData.displayId,
                                offset =
                                    when (state.effect) {
                                        DisplayEffect.NONE -> 0.0f
                                        DisplayEffect.FLASH -> 0.0f
                                        DisplayEffect.CROSS -> if (index % 2 == 0) 0.0f else 1.0f
                                        DisplayEffect.WAVE -> index / state.list.size.toFloat()
                                    },
                                interval = interval,
                            )
                        },
                ),
            )
            // 호스트 디스플레이 조작
            hostStateHolder.update {
                it.copy(
                    hostDialogState =
                        HostDialogState.StartCheer(
                            displayId = it.list.first().displayId,
                            offset = 0.0f,
                            interval = interval,
                        ),
                )
            }

            detection()
        }
    }

    private fun createRoom(
        latitude: Double,
        longitude: Double,
    ) {
        viewModelScope.launch {
            dataStoreRepository.getAccessToken()?.let { token ->
                val data = hostStateHolder.value

                if (data.title.isEmpty()) {
                    emitEvent(HostEvent.EmptyTitle)
                } else if (data.list.isEmpty()) {
                    emitEvent(HostEvent.EmptyDisplayList)
                } else {
                    webSocketClient.emit(
                        SocketRequest.CreateRoom(
                            title = data.title,
                            description = data.description,
                            location = Location(latitude = latitude, longitude = longitude),
                            displays = data.list.map { Display.Group(it.displayId) },
                            user = User(token),
                        ),
                    )
                    hostStateHolder.update { it.copy(hostDialogState = HostDialogState.Loading) }
                    delay(1000)
                    if (hostStateHolder.value.hostDialogState is HostDialogState.Loading) {
                        hostStateHolder.update { it.copy(hostDialogState = HostDialogState.Closed) }
                    }
                }
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
        val newList = hostStateHolder.value.list + GroupDisplayData(displayId, thumbnailUrl)
        if (hostState.value is HostState.WaitingRoom) {
            webSocketClient.emit(
                SocketRequest.ChangeRoomDisplay(
                    hostStateHolder.value.roomId,
                    displays = newList.map { Display.Group(it.displayId) },
                ),
            )
        }
        hostStateHolder.update {
            it.copy(
                list = newList,
                hostDialogState = HostDialogState.Closed,
            )
        }
    }

    private var lastStartedTime = 0L
    private var lastInterval = 0f

    private fun detection() {
        val state = hostStateHolder.value
        // reset
        lastStartedTime = System.currentTimeMillis()
        lastInterval = 0f

        if (state.doDetect && state.hasPermission) {
            tempoDetector.startDetection(
                onTempoUpdate = { bpm ->
                    Log.d(TAG, "onCheerStart: $bpm ${lastStartedTime - System.currentTimeMillis()}")

                    if (bpm > 0 && System.currentTimeMillis() - lastStartedTime > lastInterval * 2) {
                        val autoInterval = 60 / bpm.toFloat() * 1000
                        lastInterval = autoInterval
                        lastStartedTime = System.currentTimeMillis()

                        Log.d(TAG, "onCheerStart: $autoInterval")

                        webSocketClient.emit(
                            SocketRequest.ControlDisplay(
                                roomId = state.roomId,
                                displays =
                                    state.list.mapIndexed { index, groupDisplayData ->
                                        Display.Control(
                                            displayId = groupDisplayData.displayId,
                                            offset =
                                                when (state.effect) {
                                                    DisplayEffect.NONE -> 0.0f
                                                    DisplayEffect.FLASH -> 0.0f
                                                    DisplayEffect.CROSS -> if (index % 2 == 0) 0.0f else 1.0f
                                                    DisplayEffect.WAVE -> index / state.list.size.toFloat()
                                                },
                                            interval = autoInterval,
                                        )
                                    },
                            ),
                        )
                        hostStateHolder.update {
                            it.copy(
                                hostDialogState =
                                    HostDialogState.StartCheer(
                                        displayId = it.list.first().displayId,
                                        offset = 0.0f,
                                        interval = autoInterval,
                                    ),
                            )
                        }
                    }
                },
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (hostState.value is HostState.WaitingRoom) {
            webSocketClient.emit(SocketRequest.CloseRoom(hostStateHolder.value.roomId))
        }
        webSocketClient.closeSocket()
        tempoDetector.stopDetection()
    }
}
