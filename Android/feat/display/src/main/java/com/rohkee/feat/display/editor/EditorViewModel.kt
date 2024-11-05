package com.rohkee.feat.display.editor

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.EditorInfoState
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

class EditorViewModel @Inject constructor() : ViewModel() {
    private val editorStateHolder = MutableStateFlow<DisplayEditorData>(DisplayEditorData())

    val editorState: StateFlow<EditorState> =
        editorStateHolder
            .onStart {
                // TODO: Load initial data
            }.map { data ->
                data.toState()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = EditorState.Loading,
            )

    val editorEvent = MutableSharedFlow<EditorEvent>()

    fun onIntent(intent: EditorIntent) {
        when (intent) {
            is EditorIntent.ExitPage -> {
                emitEvent(EditorEvent.Open.ExitDialog)
            }

            EditorIntent.Save -> TODO()

            // ImageObject
            is EditorIntent.ImageObject.Select -> {
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Image) }
            }
            is EditorIntent.ImageObject.Transform -> {
                editorStateHolder.update { editorStateHolder.value.copy(editorImageState = intent.imageState) }
            }

            // TextObject
            is EditorIntent.TextObject.Select -> {
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Text) }
            }
            is EditorIntent.TextObject.Transform -> {
                editorStateHolder.update { editorStateHolder.value.copy(editorTextState = intent.textState) }
            }

            // Dialog
            is EditorIntent.Dialog.ExitPage -> {
                emitEvent(EditorEvent.ExitPage)
            }

            is EditorIntent.Dialog.ColorPicked -> TODO()
            EditorIntent.Dialog.DeleteBackground -> TODO()
            EditorIntent.Dialog.DeleteImage -> TODO()
            EditorIntent.Dialog.DeleteText -> TODO()

            // InfoToolBar
            EditorIntent.InfoToolBar.EditText -> {
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Text) }
            }
            EditorIntent.InfoToolBar.EditImage -> {
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Image) }
            }
            EditorIntent.InfoToolBar.EditBackground -> {
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Background) }
            }

            // TextToolBar
            EditorIntent.TextToolBar.Close -> {
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.None) }
            }
            EditorIntent.TextToolBar.Delete -> TODO()
            is EditorIntent.TextToolBar.Rotate -> TODO()
            EditorIntent.TextToolBar.Select -> TODO()
            is EditorIntent.TextToolBar.SelectColor -> TODO()
            EditorIntent.TextToolBar.SelectCustomColor -> TODO()
            is EditorIntent.TextToolBar.SelectFont -> TODO()

            // ImageToolBar
            EditorIntent.ImageToolBar.Close -> {
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.None) }
            }
            EditorIntent.ImageToolBar.Change -> TODO()
            EditorIntent.ImageToolBar.Delete -> TODO()
            is EditorIntent.ImageToolBar.Rotate -> TODO()
            EditorIntent.ImageToolBar.Select -> TODO()
            is EditorIntent.ImageToolBar.SelectColor -> TODO()
            EditorIntent.ImageToolBar.SelectCustomColor -> TODO()

            // BackgroundToolBar
            EditorIntent.BackgroundToolBar.Close -> {
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.None) }
            }
            is EditorIntent.BackgroundToolBar.ChangeBrightness -> TODO()
            EditorIntent.BackgroundToolBar.Delete -> TODO()
            is EditorIntent.BackgroundToolBar.SelectColor -> TODO()
            EditorIntent.BackgroundToolBar.SelectCustomColor -> TODO()
        }
    }

    private fun emitEvent(event: EditorEvent) {
        viewModelScope.launch {
            editorEvent.emit(event)
        }
    }
}

@Immutable
data class DisplayEditorData(
    val displayId: Long? = null,
    val editorInfoState: EditorInfoState = EditorInfoState(),
    val editorImageState: DisplayImageState = DisplayImageState(),
    val editorTextState: DisplayTextState = DisplayTextState(),
    val editorBackgroundState: DisplayBackgroundState = DisplayBackgroundState(),
    val bottomBarState: EditingState = EditingState.None,
) {
    fun toState(): EditorState =
        EditorState.Edit(
            displayId = displayId,
            editorInfoState = editorInfoState,
            editorImageState = editorImageState,
            editorTextState = editorTextState,
            editorBackgroundState = editorBackgroundState,
            bottomBarState = bottomBarState,
        )
}
