package com.rohkee.core.websocket

import io.socket.client.IO
import io.socket.client.Socket
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

    fun setupSocketEvents() {
//        socket?.apply {
//            on("error") { args -> handleError(args) }
//            on("initInfo") { args -> handleInitInfo(args) }
//            on("roomUpdate") { args -> handleRoomUpdate(args) }
//            on("updateDisplay") { args -> handleUpdateDisplay(args) }
//            on("roomClosed") { args -> handleRoomClosed(args) }
//            on("roomList") { args -> handleRoomList(args) }
//            on("chatUpdate") { args -> handleChat(args) }
//        }
    }
}
