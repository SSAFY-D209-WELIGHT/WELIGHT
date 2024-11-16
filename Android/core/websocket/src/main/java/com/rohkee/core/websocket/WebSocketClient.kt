package com.rohkee.core.websocket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import javax.inject.Singleton

@Singleton
object WebSocketClient {
    private var socket: Socket? = null

    fun initializeSocket(
        onConnect: () -> Unit,
        onDisconnect: () -> Unit,
        onConnectionError: () -> Unit,
    ) {
        try {
            val options =
                IO.Options().apply {
                    transports = arrayOf("websocket")
                    path = "/socket.io/"
                    secure = true
                }

            if (socket != null) return

            socket =
                IO.socket("https://k11d209.p.ssafy.io", options).apply {
                    on(Socket.EVENT_CONNECT) {
                        onConnect()
                    }
                    on(Socket.EVENT_DISCONNECT) {
                        onDisconnect()
                    }
                    on(Socket.EVENT_CONNECT_ERROR) {
                        onConnectionError()
                    }
                }
            socket?.connect()
        } catch (e: Exception) {
            onConnectionError()
        }
    }

    fun setupSocketEvents() =
        callbackFlow<SocketResponse> {
            socket?.apply {
                on(SocketEvent.On.ROOM_CREATE.name) { args ->
                    deserialize<SocketResponse.RoomCreate>(args)
                }
                on(SocketEvent.On.ROOM_JOIN.name) { args ->
                    deserialize<SocketResponse.RoomJoin>(args)
                }
                on(SocketEvent.On.ROOM_INFO_RECEIVE.name) { args ->
                    deserialize<SocketResponse.RoomInfoReceive>(args)
                }
                on(SocketEvent.On.GROUP_SELECT.name) { args ->
                    deserialize<SocketResponse.GroupSelect>(args)
                }
                on(SocketEvent.On.ROOM_DISPLAY_CHANGE.name) { args ->
                    deserialize<SocketResponse.RoomDisplayChange>(args)
                }
                on(SocketEvent.On.CHEER_START.name) { args ->
                    deserialize<SocketResponse.CheerStart>(args)
                }
                on(SocketEvent.On.CHEER_END.name) { args ->
                    deserialize<SocketResponse.CheerEnd>(args)
                }
                on(SocketEvent.On.DISPLAY_CONTROL.name) { args ->
                    deserialize<SocketResponse.DisplayControl>(args)
                }
                on(SocketEvent.On.ERROR.name) { args ->
                    deserialize<SocketResponse.Error>(args)
                }
            }

            awaitClose {
                closeSocket()
            }
        }

    fun emit(request: SocketRequest) {
        var jsonObject = JSONObject(Json.encodeToString(request))
        jsonObject.remove("type")
        Log.d("TAG", "emit: ${jsonObject}")
        socket?.emit(request.name(), jsonObject)
    }


    fun closeSocket() {
        socket?.apply {
            disconnect()
            close()
            socket = null
        }
    }

//    fun createRoom(
//        title: String,
//        description: String,
//        latitude: Double,
//        longitude: Double,
//        displays: List<Long>,
//    ) {
//        val request =
//            SocketRequest.CreateRoom(
//                title = title,
//                description = description,
//                location = Location(latitude, longitude),
//                displays = displays.map { Display.Group(it) },
//            )
//
//        socket?.emit(SocketEvent.Emit.CREATE_ROOM.name, Json.encodeToString(request))
//    }
//
//    fun joinRoom(
//        roomId: Long,
//        userId: Long,
//    ) {
//        socket?.emit(
//            SocketEvent.Emit.JOIN_ROOM.name,
//            Json.encodeToString(
//                SocketRequest.JoinRoom(
//                    roomId = roomId,
//                    user = User(id = userId),
//                ),
//            ),
//        )
//    }
//
//    fun changeRoomDisplay(displays: List<Long>) {
//        socket?.emit(
//            SocketEvent.Emit.CHANGE_ROOM_DISPLAY.name,
//            Json.encodeToString(
//                SocketRequest.ChangeRoomDisplay(
//                    displays = displays.map { Display.Group(it) },
//                ),
//            ),
//        )
//    }
//
//    fun selectGroup(groupNumber: Int) {
//        socket?.emit(
//            SocketEvent.Emit.SELECT_GROUP.name,
//            Json.encodeToString(
//                SocketRequest.SelectGroup(
//                    groupNumber = groupNumber,
//                ),
//            ),
//        )
//    }
//
//    fun startCheer() {
//        socket?.emit(SocketEvent.Emit.START_CHEER.name)
//    }
//
//    fun endCheer() {
//        socket?.emit(SocketEvent.Emit.END_CHEER.name)
//    }
//
//    fun closeRoom() {
//        socket?.emit(SocketEvent.Emit.CLOSE_ROOM.name)
//    }
//
//    fun controlDisplay(displays: List<Display.Control>) {
//        socket?.emit(
//            SocketEvent.Emit.CONTROL_DISPLAY.name,
//            Json.encodeToString(
//                SocketRequest.ControlDisplay(
//                    displays = displays,
//                )
//            )
//        )
//    }
}

private inline fun <reified T> deserialize(args: Array<Any>?): T? =
    args?.firstOrNull()?.toString()?.let { msg ->
        try {
            Json.decodeFromString<T>(msg)
        } catch (e: Exception) {
            null
        }
    }

private fun SocketRequest.encode() = Json.encodeToString(this)
