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
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val groupStateHolder = MutableStateFlow<GroupData>(GroupData())

    val groupState: StateFlow<GroupState> =
        groupStateHolder
            .map { data ->
                data.toState()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = GroupState.Loading,
            )

    val groupEvent = MutableSharedFlow<GroupEvent>()

    fun onIntent(intent: GroupIntent) {
        when (intent) {
            GroupIntent.CreateGroup -> TODO()
            is GroupIntent.GroupJoin -> TODO()
        }
    }
}
