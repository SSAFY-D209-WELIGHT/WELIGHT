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

    fun socketEventCallbacks() =
        callbackFlow<SocketResponse> {
            socket?.apply {
                on(SocketEvent.On.ROOM_CREATE.event) { args ->
                    deserialize<SocketResponse.RoomCreate>(args)?.let { trySend(it) }
                }
                on(SocketEvent.On.ROOM_JOIN.event) { args ->
                    deserialize<SocketResponse.RoomJoin>(args)?.let { trySend(it) }
                }
                on(SocketEvent.On.ROOM_INFO_RECEIVE.event) { args ->
                    deserialize<SocketResponse.RoomInfo>(args)?.let { trySend(it) }
                }
                on(SocketEvent.On.GROUP_SELECT.event) { args ->
                    deserialize<SocketResponse.GroupChange>(args)?.let { trySend(it) }
                }
                on(SocketEvent.On.ROOM_DISPLAY_CHANGE.event) { args ->
                    deserialize<SocketResponse.RoomDisplayChange>(args)?.let { trySend(it) }
                }
                on(SocketEvent.On.CHEER_START.event) { args ->
                    deserialize<SocketResponse.CheerStart>(args)?.let { trySend(it) }
                }
                on(SocketEvent.On.CHEER_END.event) { args ->
                    deserialize<SocketResponse.CheerEnd>(args)?.let { trySend(it) }
                }
                on(SocketEvent.On.ROOM_CLOSE.event) { args ->
                    deserialize<SocketResponse.RoomClose>(args)?.let { trySend(it) }
                }
                on(SocketEvent.On.DISPLAY_CONTROL.event) { args ->
                    deserialize<SocketResponse.DisplayControl>(args)?.let { trySend(it) }
                }
                on(SocketEvent.On.ERROR.event) { args ->
                    deserialize<SocketResponse.Error>(args)?.let { trySend(it) }
                }
            }

            awaitClose {
            }
        }

    fun emit(request: SocketRequest) {
        val jsonObject = JSONObject(Json.encodeToString(request))
        jsonObject.remove("type") // 불필요한 type 키 제거
        Log.d("TAG", "emit: $jsonObject")
        socket?.emit(request.name(), jsonObject)
    }

    fun closeSocket() {
        socket?.apply {
            disconnect()
            close()
            socket = null
        }
    }
}

private inline fun <reified T> deserialize(args: Array<Any>?): T? {
    Log.d("TAG", "deserialize: ${args?.firstOrNull()}")
    return args?.firstOrNull()?.toString()?.let { msg ->
        try {
            Json.decodeFromString<T>(msg)
        } catch (e: Exception) {
            Log.d("TAG", "deserialize: ${e.stackTrace}")
            null
        }
    }
}

private fun SocketRequest.encode() = Json.encodeToString(this)
