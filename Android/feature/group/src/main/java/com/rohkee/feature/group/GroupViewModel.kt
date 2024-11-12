package com.rohkee.feature.group

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
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
        }
    }

    private fun emitEvent(event: GroupEvent) {
        viewModelScope.launch {
            groupEvent.emit(event)
        }
    }

    private suspend fun loadData() {
        // TODO: load data from repository
    }
}
