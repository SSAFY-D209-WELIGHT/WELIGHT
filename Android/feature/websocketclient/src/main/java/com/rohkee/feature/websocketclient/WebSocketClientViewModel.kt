package com.rohkee.feature.websocketclient

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

    private var socket: Socket? = null

    init {
        initializeSocket()
    }

    private fun initializeSocket() {
        try {
            val options = IO.Options().apply {
                transports = arrayOf("websocket")
                secure = true
            }
            
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
            addMessage("방 정보가 초기화되었습니다.")
        }
    }

    private fun handleRoomUpdate(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { data ->
            addMessage("방 정보가 업데이트되었습니다.")
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