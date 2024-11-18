package com.rohkee.feature.websocketclient

import Message
import MessageType
import Room
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun WebSocketClientScreen(
    viewModel: WebSocketClientViewModel = hiltViewModel()
) {
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val roomId = remember { mutableStateOf("") }
    val showCreateRoomDialog by viewModel.showCreateRoomDialog.collectAsState()
    val flashEffect by viewModel.flashEffect.collectAsState()
    val catImageUrl by viewModel.catImageUrl.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusBar(connectionStatus)
            
            Box(modifier = Modifier.weight(0.4f)) {
                RoomList(
                    rooms = rooms,
                    onRoomClick = { viewModel.joinRoom(it.roomId) }
                )
            }
            
            RoomJoinInput(
                roomId = roomId.value,
                onRoomIdChange = { roomId.value = it },
                onJoinClick = {
                    if (roomId.value.isNotBlank()) {
                        viewModel.joinRoom(roomId.value)
                    }
                }
            )
            
            Box(modifier = Modifier.weight(0.6f)) {
                MessageList(
                    messages = messages,
                    selectedType = viewModel.selectedMessageType.collectAsState().value,
                    onTypeChange = { viewModel.setMessageType(it) }
                )
            }

            if (showCreateRoomDialog) {
                CreateRoomDialog(
                    roomId = roomId.value,
                    onConfirm = { description -> 
                        viewModel.createRoom(roomId.value, description)
                    },
                    onDismiss = { viewModel.dismissCreateRoomDialog() }
                )
            }
        }
        
        if (flashEffect) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
        
        catImageUrl?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = "고양이 이미지",
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(32.dp),
                contentScale = ContentScale.Fit
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
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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

        TextButton(
            onClick = onJoinClick,
            modifier = Modifier
                .height(56.dp)
                .width(56.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "입장",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun MessageList(
    messages: List<Message>,
    selectedType: MessageType,
    onTypeChange: (MessageType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "메시지",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == MessageType.SYSTEM,
                        onClick = { onTypeChange(MessageType.SYSTEM) },
                        label = { Text("시스템") }
                    )
                    FilterChip(
                        selected = selectedType == MessageType.CHAT,
                        onClick = { onTypeChange(MessageType.CHAT) },
                        label = { Text("채팅") }
                    )
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true
            ) {
                items(messages.filter { it.type == selectedType }.asReversed()) { message ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when(message.type) {
                                MessageType.SYSTEM -> MaterialTheme.colorScheme.secondaryContainer
                                MessageType.CHAT -> MaterialTheme.colorScheme.tertiaryContainer
                            }.copy(alpha = 0.7f)
                        )
                    ) {
                        Text(
                            text = message.content,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateRoomDialog(
    roomId: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("방 생성") },
        text = {
            Column {
                Text("'$roomId'번 방을 생성하시겠습니까?")
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
                onClick = { onConfirm(description) }
            ) {
                Text("생성")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}