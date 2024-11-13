package com.rohkee.feature.websocketclient

import ChatMessage
import Client
import Location
import Message
import MessageType
import Room
import RoomInfo
import RoomUpdate
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import javax.inject.Inject
import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@HiltViewModel
class WebSocketClientViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    private val _connectionStatus = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Disconnected)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _roomInfo = MutableStateFlow<RoomInfo?>(null)
    val roomInfo = _roomInfo.asStateFlow()

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients = _clients.asStateFlow()

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms = _rooms.asStateFlow()

    private var socket: Socket? = null

    private val _location = MutableStateFlow<Location?>(null)
    val location = _location.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

    private val _showCreateRoomDialog = MutableStateFlow(false)
    val showCreateRoomDialog = _showCreateRoomDialog.asStateFlow()

    private val _selectedMessageType = MutableStateFlow(MessageType.SYSTEM)
    val selectedMessageType = _selectedMessageType.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    private val _currentRoomInfo = MutableStateFlow<RoomInfo?>(null)
    val currentRoomInfo = _currentRoomInfo.asStateFlow()

    private val _roomUpdate = MutableStateFlow<RoomUpdate?>(null)
    val roomUpdate = _roomUpdate.asStateFlow()

    init {
        initializeSocket()
        requestLocationPermission()
        setupSocketEvents()
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
            }
            socket?.connect()
        } catch (e: Exception) {
            _connectionStatus.value = ConnectionStatus.Error
        }
    }

    private fun handleError(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { errorData ->
            try {
                val jsonObject = JSONObject(errorData)
                when (jsonObject.optString("code")) {
                    "ROOM_NOT_FOUND" -> {
                        _showCreateRoomDialog.value = true
                        addMessage(jsonObject.optString("message", "존재하지 않는 방입니다."))
                    }
                    else -> addMessage(jsonObject.optString("message", "알 수 없는 오류가 발생했습니다."))
                }
            } catch (e: Exception) {
                addMessage("오류 처리 중 문제가 발생했습니다: ${e.message}")
            }
        }
    }

    private fun handleInitInfo(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { data ->
            try {
                val jsonObject = JSONObject(data)
                val roomInfo = RoomInfo(
                    location = Location(
                        latitude = jsonObject.getJSONObject("location").getDouble("latitude"),
                        longitude = jsonObject.getJSONObject("location").getDouble("longitude")
                    ),
                    address = jsonObject.getString("address"),
                    groupNumber = jsonObject.getInt("groupNumber"),
                    clientNumber = jsonObject.getInt("clientNumber"),
                    isOwner = jsonObject.getBoolean("isOwner")
                )
                _currentRoomInfo.value = roomInfo
                addMessage("방에 입장했습니다.")
            } catch (e: Exception) {
                addMessage("초기 정보 처리 중 오류 발생: ${e.message}")
            }
        }
    }

    private fun handleRoomUpdate(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { data ->
            try {
                val jsonObject = JSONObject(data)
                val clientsArray = jsonObject.getJSONArray("clients")
                val clients = mutableListOf<Client>()
                
                for (i in 0 until clientsArray.length()) {
                    val clientObject = clientsArray.getJSONObject(i)
                    clients.add(
                        Client(
                            socketId = clientObject.getString("socketId"),
                            groupNumber = clientObject.getInt("groupNumber"),
                            clientNumber = clientObject.getInt("clientNumber"),
                            isOwner = clientObject.getBoolean("isOwner")
                        )
                    )
                }
                _roomUpdate.value = RoomUpdate(clients)
            } catch (e: Exception) {
                addMessage("방 업데이트 처리 중 오류 발생: ${e.message}")
            }
        }
    }

    private fun handleUpdateDisplay(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { data ->
            try {
                val jsonObject = JSONObject(data)
                // 디스플레이 업데이트 처리
                addMessage("디스플레이 업데이트: $data")
            } catch (e: Exception) {
                addMessage("디스플레이 업데이트 처리 중 오류 발생: ${e.message}")
            }
        }
    }

    private fun handleRoomClosed(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { data ->
            try {
                val jsonObject = JSONObject(data)
                val message = jsonObject.optString("message", "방이 닫혔습니다.")
                _currentRoomInfo.value = null
                _roomUpdate.value = null
                addMessage(message)
            } catch (e: Exception) {
                addMessage("방 종료 처리 중 오류 발생: ${e.message}")
            }
        }
    }

    private fun handleRoomList(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { data ->
            try {
                val jsonObject = JSONObject(data)
                val roomsArray = jsonObject.getJSONArray("rooms")
                val roomsList = mutableListOf<Room>()
                
                for (i in 0 until roomsArray.length()) {
                    val roomObject = roomsArray.getJSONObject(i)
                    val locationObject = roomObject.getJSONObject("location")
                    roomsList.add(
                        Room(
                            roomId = roomObject.getString("roomId"),
                            clientCount = roomObject.getInt("clientCount"),
                            location = Location(
                                latitude = locationObject.getDouble("latitude"),
                                longitude = locationObject.getDouble("longitude")
                            ),
                            address = roomObject.getString("address"),
                            description = roomObject.optString("description", "")
                        )
                    )
                }
                _rooms.value = roomsList
                addMessage("방 목록이 업데이트되었습니다.")
            } catch (e: Exception) {
                addMessage("방 목록 처리 중 오류 발생: ${e.message}")
            }
        }
    }

    private fun handleChat(args: Array<Any>) {
        args.firstOrNull()?.toString()?.let { data ->
            try {
                val jsonObject = JSONObject(data)
                val message = jsonObject.getString("message")
                val sender = jsonObject.getJSONObject("sender")
                val socketId = sender.getString("socketId")
                val groupNumber = sender.getInt("groupNumber")
                val clientNumber = sender.getInt("clientNumber")
                
                val shortSocketId = socketId.takeLast(6)
                
                addMessage(
                    "👤 ${groupNumber}-${clientNumber} [$shortSocketId]: $message",
                    MessageType.CHAT
                )
            } catch (e: Exception) {
                addMessage("채팅 메시지 처리 중 오류 발생: ${e.message}", MessageType.SYSTEM)
            }
        }
    }

    private fun addMessage(content: String, type: MessageType = MessageType.SYSTEM) {
        _messages.value = _messages.value + Message(content, type)
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                _location.value = Location(it.latitude, it.longitude)
            }
        }
    }

    fun joinRoom(roomId: String) {
        _location.value?.let { location ->
            socket?.emit("joinRoom", JSONObject().apply {
                put("roomId", roomId)
                put("location", JSONObject().apply {
                    put("latitude", location.latitude)
                    put("longitude", location.longitude)
                })
            })
        } ?: run {
            addMessage("위치 정보가 필요합니다.")
        }
    }

    private fun setupSocketEvents() {
        socket?.apply {
            on("error") { args -> handleError(args) }
            on("initInfo") { args -> handleInitInfo(args) }
            on("roomUpdate") { args -> handleRoomUpdate(args) }
            on("updateDisplay") { args -> handleUpdateDisplay(args) }
            on("roomClosed") { args -> handleRoomClosed(args) }
            on("roomList") { args -> handleRoomList(args) }
            on("chatUpdate") { args -> handleChat(args) }
        }
    }

    fun createRoom(roomId: String, description: String = "") {
        _location.value?.let { location ->
            socket?.emit("createRoom", JSONObject().apply {
                put("roomId", roomId)
                put("description", description)
                put("location", JSONObject().apply {
                    put("latitude", location.latitude)
                    put("longitude", location.longitude)
                })
            })
            _showCreateRoomDialog.value = false
        } ?: run {
            addMessage("위치 정보가 필요합니다.")
        }
    }

    fun dismissCreateRoomDialog() {
        _showCreateRoomDialog.value = false
    }

    fun sendChat(message: String) {
        socket?.emit("chat", JSONObject().apply {
            put("message", message)
            put("timestamp", System.currentTimeMillis())
        })
    }

    fun setMessageType(type: MessageType) {
        _selectedMessageType.value = type
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