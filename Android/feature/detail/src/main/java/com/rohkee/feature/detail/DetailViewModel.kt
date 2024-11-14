package com.rohkee.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.util.handle
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
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
class DetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val displayRepository: DisplayRepository,
) : ViewModel() {
    private val id: Long = savedStateHandle.toRoute<DetailRoute>().displayId

    private val detailStateHolder = MutableStateFlow<DisplayDetailData>(DisplayDetailData())

    val detailState: StateFlow<DetailState> =
        detailStateHolder
            .onStart {
                loadData()
            }.map { data ->
                data.toState()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DetailState.Loading,
            )

    val detailEvent = MutableSharedFlow<DetailEvent>()

    fun onIntent(intent: DetailIntent) {
        when (intent) {
            DetailIntent.Comment -> openComments()
            DetailIntent.Delete -> deleteDisplay()
            DetailIntent.Download -> download()
            DetailIntent.Duplicate -> duplicateDisplay()
            DetailIntent.Edit -> editDisplay()
            DetailIntent.Post -> postToBoard()
            DetailIntent.ToggleFavorite -> toggleFavorite()
            DetailIntent.ToggleLike -> toggleLike()
            DetailIntent.ToggleUI -> {
                // TODO : UI 토글
            }

            DetailIntent.ExitPage -> emitEvent(DetailEvent.ExitPage)
        }
    }

    private fun emitEvent(event: DetailEvent) {
        viewModelScope.launch {
            detailEvent.emit(event)
        }
    }

    private suspend fun loadData() {
        displayRepository.getDisplayDetail(id = id).handle(
            onSuccess = { response ->
                response?.let { data ->
                    detailStateHolder.emit(
                        DisplayDetailData(
                            displayId = id,
                            thumbnailUrl = data.thumbnailUrl,
                            isAuthor = data.isOwner,
                            isPublished = data.posted,
                            isFavorite = data.favorite,
                            title = data.title,
                            tags = data.tags.toPersistentList(),
                            author = data.authorName,
                            liked = data.liked,
                            like = data.likes,
                            download = data.downloads,
                            comment = data.comments,
                            displayImageState = DisplayImageState(),
                            displayTextState = DisplayTextState(),
                            displayBackgroundState = DisplayBackgroundState(),
                        ),
                    )
                }
            },
            onError = { errorCode, message ->
                // TODO : 에러처리
            },
        )
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            displayRepository.favoriteDisplay(id).handle(
                onSuccess = {
                    detailStateHolder.emit(
                        detailStateHolder.value.copy(
                            isFavorite = !detailStateHolder.value.isFavorite,
                        ),
                    )
                },
                onError = { _, message ->
                    // TODO : 에러처리
                },
            )
        }
    }

    private fun toggleLike() {
        viewModelScope.launch {
            if (detailStateHolder.value.liked) {
                displayRepository.unlikeDisplay(id).handle(
                    onSuccess = {
                        detailStateHolder.update {
                            it.copy(
                                liked = false,
                                like = it.like - 1,
                            )
                        }
                    },
                    onError = { _, message ->
                        // TODO : 에러처리
                    },
                )
            } else {
                displayRepository.likeDisplay(id).handle(
                    onSuccess = {
                        detailStateHolder.update {
                            it.copy(
                                liked = true,
                                like = it.like + 1,
                            )
                        }
                    },
                    onError = { _, message ->
                        // TODO : 에러처리
                    },
                )
            }
        }
    }

    private fun download() {
        viewModelScope.launch {
            displayRepository.importDisplayToMyStorage(id).handle(
                onSuccess = {
                    detailStateHolder.update { data ->
                        data.copy(download = data.download + 1)
                    }
                    if (it != null) {
                        detailEvent.emit(DetailEvent.Download.Success(it.id))
                    }
                },
                onError = { _, _ -> detailEvent.emit(DetailEvent.Download.Error) },
            )
        }
    }

    private fun openComments() {
        // TODO : open comments
    }

    private fun postToBoard() {
        viewModelScope.launch {
            displayRepository.publishDisplay(id).handle(
                onSuccess = {
                    detailStateHolder.update { data -> data.copy(isPublished = true) }
                    if (it != null) {
                        detailEvent.emit(DetailEvent.Publish.Success(it.id))
                    }
                },
                onError = { _, _ -> detailEvent.emit(DetailEvent.Publish.Error) },
            )
        }
    }

    private fun editDisplay() {
        viewModelScope.launch {
            detailEvent.emit(DetailEvent.EditDisplay(displayId = id))
        }
    }

    private fun duplicateDisplay() {
        viewModelScope.launch {
            displayRepository.duplicateDisplay(id).handle(
                onSuccess = {
                    detailEvent.emit(DetailEvent.Duplicate.Success(displayId = id))
                },
                onError = { _, _ -> detailEvent.emit(DetailEvent.Duplicate.Error) },
            )
        }
    }

    private fun deleteDisplay() {
        viewModelScope.launch {
            displayRepository.deleteDisplayFromStorage(id).handle(
                onSuccess = {
                    detailEvent.emit(DetailEvent.Delete.Success)
                },
                onError = { _, _ -> detailEvent.emit(DetailEvent.Delete.Error) },
            )
        }
    }
}