package com.rohkee.feature.websocketclient

import Room
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WebSocketClientScreen(
    viewModel: WebSocketClientViewModel = hiltViewModel()
) {
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val roomId = remember { mutableStateOf("") }
    val showCreateRoomDialog by viewModel.showCreateRoomDialog.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // 상태 표시줄
        StatusBar(connectionStatus)

        Spacer(modifier = Modifier.height(16.dp))

        // 방 목록
        RoomList(
            rooms = rooms,
            onRoomClick = { viewModel.joinRoom(it.roomId) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 방 입장 입력
        RoomJoinInput(
            roomId = roomId.value,
            onRoomIdChange = { roomId.value = it },
            onJoinClick = {
                if (roomId.value.isNotBlank()) {
                    viewModel.joinRoom(roomId.value)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 메시지 목록
        MessageList(messages = messages)

        if (showCreateRoomDialog) {
            var description by remember { mutableStateOf("") }
            
            AlertDialog(
                onDismissRequest = { viewModel.dismissCreateRoomDialog() },
                title = { Text("방 생성") },
                text = {
                    Column {
                        Text("입력하신 번호로 새로운 방을 생성하시겠습니까?")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("방 설명 (선택사항)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            if (roomId.value.isNotBlank()) {
                                viewModel.createRoom(roomId.value, description)
                            }
                        }
                    ) {
                        Text("생성")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissCreateRoomDialog() }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}

@Composable
private fun StatusBar(connectionStatus: ConnectionStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "응원방",
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            when (connectionStatus) {
                                is ConnectionStatus.Connected -> MaterialTheme.colorScheme.tertiary
                                is ConnectionStatus.Disconnected -> MaterialTheme.colorScheme.error
                                is ConnectionStatus.Error -> MaterialTheme.colorScheme.errorContainer
                            },
                            shape = CircleShape
                        )
                )
                Text(
                    text = when (connectionStatus) {
                        is ConnectionStatus.Connected -> "연결됨"
                        is ConnectionStatus.Disconnected -> "연결 끊김"
                        is ConnectionStatus.Error -> "연결 오류"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun RoomList(
    rooms: List<Room>,
    onRoomClick: (Room) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "사용 가능한 방 목록",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rooms) { room ->
                    RoomItem(room = room, onClick = { onRoomClick(room) })
                }
            }
        }
    }
}

@Composable
private fun RoomItem(room: Room, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "방 ${room.roomId}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👥 ${room.clientCount}명",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                Text(
                    text = "📍 ${room.address}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
            if (room.description.isNotBlank()) {
                Text(
                    text = room.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
private fun RoomJoinInput(
    roomId: String,
    onRoomIdChange: (String) -> Unit,
    onJoinClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = roomId,
            onValueChange = onRoomIdChange,
            modifier = Modifier.weight(1f),
            label = { Text("방 번호") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.outline
            )
        )

        Button(
            onClick = onJoinClick,
            modifier = Modifier
                .height(56.dp)
                .width(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "입장",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun MessageList(messages: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "메시지",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true
            ) {
                items(messages.asReversed()) { message ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                        )
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}