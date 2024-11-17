package com.rohkee.core.websocket

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
        val jsonObject = JSONObject(Json.encodeToString(request))
        jsonObject.remove("type") // 불필요한 type 키 제거
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

private inline fun <reified T> deserialize(args: Array<Any>?): T? =
    args?.firstOrNull()?.toString()?.let { msg ->
        try {
            Json.decodeFromString<T>(msg)
        } catch (e: Exception) {
            null
        }
    }

private fun SocketRequest.encode() = Json.encodeToString(this)
