package com.rohkee.feat.display.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.ui.screen.display.editor.DisplayEditorIntent
import com.rohkee.core.ui.screen.display.editor.DisplayEditorState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class EditorViewModel @Inject constructor() : ViewModel() {
    private val _editorState = MutableStateFlow(DisplayEditorState.Loading)
    val editorState: StateFlow<DisplayEditorState> =
        _editorState
            .onStart {
                // TODO: Load initial data
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DisplayEditorState.Loading,
            )

    val editorEvent = MutableSharedFlow<EditorEvent>()

    fun onIntent(intent: DisplayEditorIntent) {
        when (intent) {
            is DisplayEditorIntent.ExitPage -> {
                // TODO: exit page
            }

            is DisplayEditorIntent.BackgroundToolBar.ChangeBrightness -> TODO()
            is DisplayEditorIntent.BackgroundToolBar.Close -> TODO()
            is DisplayEditorIntent.BackgroundToolBar.Delete -> TODO()
            is DisplayEditorIntent.BackgroundToolBar.SelectColor -> TODO()
            is DisplayEditorIntent.BackgroundToolBar.SelectCustomColor -> TODO()
            is DisplayEditorIntent.ImageToolBar.Change -> TODO()
            is DisplayEditorIntent.ImageToolBar.Close -> TODO()
            is DisplayEditorIntent.ImageToolBar.Delete -> TODO()
            is DisplayEditorIntent.ImageToolBar.Rotate -> TODO()
            is DisplayEditorIntent.ImageToolBar.SelectColor -> TODO()
            is DisplayEditorIntent.ImageToolBar.SelectCustomColor -> TODO()
            is DisplayEditorIntent.InfoToolBar.EditBackground -> TODO()
            is DisplayEditorIntent.InfoToolBar.EditImage -> TODO()
            is DisplayEditorIntent.InfoToolBar.EditText -> TODO()
            is DisplayEditorIntent.SaveDisplay -> TODO()
            is DisplayEditorIntent.TextToolBar.Close -> TODO()
            is DisplayEditorIntent.TextToolBar.Delete -> TODO()
            is DisplayEditorIntent.TextToolBar.Rotate -> TODO()
            is DisplayEditorIntent.TextToolBar.SelectColor -> TODO()
            is DisplayEditorIntent.TextToolBar.SelectCustomColor -> TODO()
            is DisplayEditorIntent.TextToolBar.SelectFont -> TODO()
            is DisplayEditorIntent.UpdateImageState -> TODO()
            is DisplayEditorIntent.UpdateTextState -> TODO()
        }
    }
}

sealed interface EditorEvent {
    data object OpenDialog : EditorEvent

    data object ExitPage : EditorEvent

    data object SaveDisplay : EditorEvent

    data class ShowSnackBar(
        val message: String,
    ) : EditorEvent

    data object OpenColorPicker : EditorEvent

    data object OpenTextEditor : EditorEvent
}
