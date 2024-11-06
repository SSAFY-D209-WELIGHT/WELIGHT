package com.rohkee.feat.display.editor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.EditorInfoState
import com.rohkee.core.ui.model.CustomColor
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
            is EditorIntent.ImageObject.Select ->
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Image) }

            is EditorIntent.ImageObject.Transform ->
                editorStateHolder.update { editorStateHolder.value.copy(editorImageState = intent.imageState) }

            // TextObject
            is EditorIntent.TextObject.Select ->
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Text) }

            is EditorIntent.TextObject.Transform ->
                editorStateHolder.update { editorStateHolder.value.copy(editorTextState = intent.textState) }

            // Dialog
            is EditorIntent.Dialog.ExitPage -> emitEvent(EditorEvent.ExitPage)

            is EditorIntent.Dialog.ColorPicked -> {
                when (editorStateHolder.value.bottomBarState) {
                    EditingState.Text ->
                        editorStateHolder.update { editorStateHolder.value.copyWithText(color = intent.color) }

                    EditingState.Image ->
                        editorStateHolder.update { editorStateHolder.value.copyWithImage(color = intent.color) }

                    EditingState.Background ->
                        editorStateHolder.update { editorStateHolder.value.copyWithBackground(color = intent.color) }

                    EditingState.None -> {}
                }
            }

            EditorIntent.Dialog.DeleteText -> TODO()
            EditorIntent.Dialog.DeleteImage -> TODO()
            EditorIntent.Dialog.DeleteBackground ->
                editorStateHolder.update {
                    editorStateHolder.value.copyWithBackground()
                }

            // InfoToolBar
            EditorIntent.InfoToolBar.EditText ->
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Text) }

            EditorIntent.InfoToolBar.EditImage ->
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Image) }

            EditorIntent.InfoToolBar.EditBackground ->
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Background) }

            // TextToolBar
            EditorIntent.TextToolBar.Close ->
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.None) }

            EditorIntent.TextToolBar.Delete -> {
                emitEvent(EditorEvent.Open.TextDeleteDialog)
            }

            EditorIntent.TextToolBar.EditText -> TODO()

            is EditorIntent.TextToolBar.SelectColor ->
                editorStateHolder.update { editorStateHolder.value.copyWithText(color = intent.color) }

            is EditorIntent.TextToolBar.SelectFont ->
                editorStateHolder.update { editorStateHolder.value.copyWithText(font = intent.font) }

            is EditorIntent.TextToolBar.SelectCustomColor ->
                emitEvent(EditorEvent.Open.ColorPicker(intent.currentColor))

            // ImageToolBar
            EditorIntent.ImageToolBar.Close ->
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.None) }

            EditorIntent.ImageToolBar.Delete -> emitEvent(EditorEvent.Open.ImageDeleteDialog)
            EditorIntent.ImageToolBar.Change -> TODO()
            is EditorIntent.ImageToolBar.SelectColor ->
                editorStateHolder.update { editorStateHolder.value.copyWithImage(color = intent.color) }

            is EditorIntent.ImageToolBar.SelectCustomColor ->
                emitEvent(EditorEvent.Open.ColorPicker(intent.currentColor))

            // BackgroundToolBar
            EditorIntent.BackgroundToolBar.Close ->
                editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.None) }

            EditorIntent.BackgroundToolBar.Delete -> emitEvent(EditorEvent.Open.BackgroundDeleteDialog)
            is EditorIntent.BackgroundToolBar.ChangeBrightness ->
                editorStateHolder.update { editorStateHolder.value.copyWithBackground(brightness = intent.brightness) }

            is EditorIntent.BackgroundToolBar.SelectColor ->
                editorStateHolder.update { editorStateHolder.value.copyWithBackground(color = intent.color) }

            is EditorIntent.BackgroundToolBar.SelectCustomColor ->
                emitEvent(
                    EditorEvent.Open.ColorPicker(
                        intent.currentColor,
                    ),
                )
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

    fun copyWithText(
        isSelected: Boolean = editorTextState.isSelected,
        text: String = editorTextState.text,
        color: CustomColor = editorTextState.color,
        font: FontFamily = editorTextState.font,
        scale: Float = editorTextState.scale,
        rotationDegree: Float = editorTextState.rotationDegree,
        offsetPercentX: Float = editorTextState.offsetPercentX,
        offsetPercentY: Float = editorTextState.offsetPercentY,
    ) = copy(
        editorTextState =
            editorTextState.copy(
                isSelected = isSelected,
                text = text,
                color = color,
                font = font,
                scale = scale,
                rotationDegree = rotationDegree,
                offsetPercentX = offsetPercentX,
                offsetPercentY = offsetPercentY,
            ),
    )

    fun copyWithImage(
        isSelected: Boolean = editorImageState.isSelected,
        imageSource: Any? = editorImageState.imageSource,
        color: CustomColor = editorImageState.color,
        scale: Float = editorImageState.scale,
        rotationDegree: Float = editorImageState.rotationDegree,
        offsetPercentX: Float = editorImageState.offsetPercentX,
        offsetPercentY: Float = editorImageState.offsetPercentY,
    ) = copy(
        editorImageState =
            editorImageState.copy(
                isSelected = isSelected,
                imageSource = imageSource,
                color = color,
                scale = scale,
                rotationDegree = rotationDegree,
                offsetPercentX = offsetPercentX,
                offsetPercentY = offsetPercentY,
            ),
    )

    fun copyWithBackground(
        color: CustomColor = editorBackgroundState.color,
        brightness: Float = editorBackgroundState.brightness,
    ) = copy(
        editorBackgroundState =
            editorBackgroundState.copy(
                color = color,
                brightness = brightness,
            ),
    )
}
