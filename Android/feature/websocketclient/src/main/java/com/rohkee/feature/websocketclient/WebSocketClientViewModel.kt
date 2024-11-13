package com.rohkee.feature.websocketclient

import Client
import Location
import RoomInfo
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class WebSocketClientViewModel @Inject constructor(
) : ViewModel() {
    private val _connectionStatus = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Disconnected)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _roomInfo = MutableStateFlow<RoomInfo?>(null)
    val roomInfo = _roomInfo.asStateFlow()

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients = _clients.asStateFlow()

    private var socket: Socket? = null

    init {
        initializeSocket()
    }

    private fun initializeSocket() {
        try {
            val options = IO.Options().apply {
                transports = arrayOf("websocket")
                path = "/socket.io/"
                secure = true
            }
            
            if (socket != null) return
            
            socket = IO.socket("https://k11d209.p.ssafy.io", options).apply {
                on(Socket.EVENT_CONNECT) {
                    _connectionStatus.value = ConnectionStatus.Connected
                }
                on(Socket.EVENT_DISCONNECT) {
                    _connectionStatus.value = ConnectionStatus.Disconnected
                }
                on(Socket.EVENT_CONNECT_ERROR) {
                    _connectionStatus.value = ConnectionStatus.Error
                }
                on("initInfo") { args ->
                    handleInitInfo(args)
                }
                on("roomUpdate") { args ->
                    handleRoomUpdate(args)
                }
                on("updateDisplay") { args ->
                    handleUpdateDisplay(args)
                }
                on("roomClosed") { args ->
                    handleRoomClosed(args)
                }
            }
            socket?.connect()
        } catch (e: Exception) {
            _connectionStatus.value = ConnectionStatus.Error
        }
    }

    private fun handleInitInfo(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { info ->
            try {
                val jsonObject = JSONObject(info)
                val location = jsonObject.getJSONObject("location")
                _roomInfo.value = RoomInfo(
                    location = Location(
                        latitude = location.optDouble("latitude", 0.0),
                        longitude = location.optDouble("longitude", 0.0)
                    ),
                    address = jsonObject.optString("address", ""),
                    groupNumber = jsonObject.optInt("groupNumber", -1),
                    clientNumber = jsonObject.optInt("clientNumber", -1),
                    isOwner = jsonObject.optBoolean("isOwner", false)
                )
                addMessage("roomInfo: ${_roomInfo.value}")
                addMessage("방 정보가 초기화되었습니다.")
            } catch (e: Exception) {
                addMessage("방 정보 파싱 오류: ${e.message}")
            }
        }
    }

    private fun handleRoomUpdate(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { data ->
            try {
                val jsonObject = JSONObject(data)
                val clientsArray = jsonObject.getJSONArray("clients")
                val clientsList = mutableListOf<Client>()
                
                for (i in 0 until clientsArray.length()) {
                    val clientObject = clientsArray.getJSONObject(i)
                    clientsList.add(
                        Client(
                            socketId = clientObject.optString("socketId", ""),
                            groupNumber = clientObject.optInt("groupNumber", -1),
                            clientNumber = clientObject.optInt("clientNumber", -1),
                            isOwner = clientObject.optBoolean("isOwner", false)
                        )
                    )
                }
                _clients.value = clientsList
                addMessage("clients: $clientsList")
                addMessage("방 정보가 업데이트되었습니다.")
            } catch (e: Exception) {
                addMessage("클라이언트 정보 파싱 오류: ${e.message}")
            }
        }
    }

    private fun handleUpdateDisplay(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { data ->
            try {
                val jsonObject = JSONObject(data)
                val message = jsonObject.optString("message", "")
                val command = jsonObject.optString("command", "")
                
                when (command) {
                    "startCheer" -> addMessage("응원이 시작되었습니다!")
                    "stopCheer" -> addMessage("응원이 일시정지되었습니다.")
                    "closeCheer" -> addMessage("응원이 종료되었습니다.")
                    "effectChange" -> {
                        val effect = jsonObject.optString("effect", "")
                        addMessage("$effect 효과가 적용되었습니다.")
                    }
                    else -> addMessage(message)
                }
            } catch (e: Exception) {
                addMessage(data)
            }
        }
    }

    private fun handleRoomClosed(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { data ->
            addMessage("방이 종료되었습니다.")
        }
    }

    private fun addMessage(message: String) {
        _messages.value = _messages.value + message
    }

    fun joinRoom() {
        socket?.emit("joinRoom", JSONObject().apply {
            put("message", "Hello")
            put("timestamp", System.currentTimeMillis())
        })
    }

    override fun onCleared() {
        socket?.disconnect()
        socket?.close()
        super.onCleared()
    }
}

sealed class ConnectionStatus {
    object Connected : ConnectionStatus()
    object Disconnected : ConnectionStatus()
    object Error : ConnectionStatus()
} 