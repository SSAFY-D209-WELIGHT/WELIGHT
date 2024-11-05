package com.rohkee.feat.display.editor

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.EditorInfoState
import com.rohkee.core.ui.screen.display.editor.DisplayEditorIntent
import com.rohkee.core.ui.screen.display.editor.DisplayEditorState
import com.rohkee.core.ui.screen.display.editor.EditingState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class EditorViewModel @Inject constructor() : ViewModel() {
    private val editorStateHolder = MutableStateFlow<DisplayEditorData?>(null)

    val editorState: StateFlow<DisplayEditorState> =
        editorStateHolder
            .onStart {
                // TODO: Load initial data
            }.map { data ->
                data?.toState() ?: DisplayEditorState.Loading
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DisplayEditorState.Loading,
            )

    val editorEvent = MutableSharedFlow<EditorEvent>()

    fun onIntent(intent: DisplayEditorIntent) {
        when (intent) {
            is DisplayEditorIntent.AttemptExitPage -> {
                editorEvent.tryEmit(EditorEvent.OpenDialog)
            }
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
            is DisplayEditorIntent.UpdateImageState -> {
                editorStateHolder.update { editorStateHolder.value?.copy(editorImageState = intent.imageState) }
            }

            is DisplayEditorIntent.UpdateTextState -> {
                editorStateHolder.update { editorStateHolder.value?.copy(editorTextState = intent.textState) }
            }

            is DisplayEditorIntent.Dialog.ColorPicked -> TODO()
        }
    }
}

@Immutable
data class DisplayEditorData(
    val displayId: Long? = null,
    val editorInfoState: EditorInfoState,
    val editorImageState: DisplayImageState,
    val editorTextState: DisplayTextState,
    val editorBackgroundState: DisplayBackgroundState,
    val bottomBarState: EditingState,
) {
    fun toState(): DisplayEditorState =
        DisplayEditorState.Edit(
            displayId = displayId,
            editorInfoState = editorInfoState,
            editorImageState = editorImageState,
            editorTextState = editorTextState,
            editorBackgroundState = editorBackgroundState,
            bottomBarState = bottomBarState,
        )
}

sealed interface EditorEvent {
    data object OpenDialog : EditorEvent

    data object ExitPage : EditorEvent

    data class SaveDisplay(
        val displayId: Long,
    ) : EditorEvent

    data class ShowSnackBar(
        val message: String,
    ) : EditorEvent

    data object OpenColorPicker : EditorEvent

    data object OpenTextEditor : EditorEvent
}
