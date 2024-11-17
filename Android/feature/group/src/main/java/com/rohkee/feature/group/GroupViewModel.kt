package com.rohkee.feature.group

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.network.repository.CheerRepository
import com.rohkee.core.network.util.handle
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val cheerRepository: CheerRepository,
) : ViewModel() {
    private val groupStateHolder = MutableStateFlow<GroupData>(GroupData())

    val groupState: StateFlow<GroupState> =
        groupStateHolder
            .onStart {
                loadData()
            }.map { data ->
                data.toState()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = GroupState.Loading,
            )

    val groupEvent = MutableSharedFlow<GroupEvent>()

    fun onIntent(intent: GroupIntent) {
        when (intent) {
            GroupIntent.CreateGroup -> emitEvent(GroupEvent.OpenRoomCreation)
            is GroupIntent.GroupJoin -> emitEvent(GroupEvent.OpenClient(intent.id))
            GroupIntent.LoadGroupList -> viewModelScope.launch { loadData() }
            is GroupIntent.UpdateLocation ->
                groupStateHolder.update {
                    Log.d("TAG", "onIntent: ${intent.latitude} ${intent.longitude}")
                    it.copy(currentPostion = LatLng(intent.latitude, intent.longitude))
                }
        }
    }

    private fun emitEvent(event: GroupEvent) {
        viewModelScope.launch {
            groupEvent.emit(event)
        }
    }

    private suspend fun loadData() {
        groupStateHolder.update { it.invalidate() }
        delay(300)
        cheerRepository
            .getCheerRoomList(
                latitude = groupStateHolder.value.currentPostion.latitude,
                longitude = groupStateHolder.value.currentPostion.longitude,
            ).handle(
                onSuccess = { response ->
                    groupStateHolder.update {
                        it.copy(
                            isValid = true,
                            list =
                                response?.map { data ->
                                    RoomData(
                                        title = data.roomName,
                                        description = data.roomDescription,
                                        participants = data.participantCount,
                                        roomId = data.roomId,
                                    )
                                } ?: emptyList(),
                        )
                    }
                },
                onError = { _, message ->
                    groupStateHolder.update { it.validate() }
                    Log.d("TAG", "loadData: $message")
                },
            )
    }
}
