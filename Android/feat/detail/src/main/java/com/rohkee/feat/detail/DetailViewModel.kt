package com.rohkee.feat.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rohkee.core.network.repository.DisplayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val displayRepository: DisplayRepository,
) : ViewModel() {
    private val id: Long = savedStateHandle.toRoute<DetailRoute>().displayId

    private val detailStateHolder = MutableStateFlow<DisplayDetailData>(DisplayDetailData())

    val detailState: StateFlow<DetailState> =
        detailStateHolder
            .onStart {
                // TODO : init
            }.map { data ->
                data.toState()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DetailState.Loading,
            )

    val detailEvent = MutableSharedFlow<DetailEvent>()

    fun onIntent(intent: DetailIntent) {

    }

    private suspend fun loadData() {

    }
}
