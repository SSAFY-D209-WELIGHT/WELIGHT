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
class EditorViewModel @Inject constructor() : ViewModel() {
    private val editorStateHolder = MutableStateFlow<DisplayEditorData>(DisplayEditorData())

    val editorState: StateFlow<EditorState> =
        editorStateHolder
            .onStart {
                // TODO : init
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
            is EditorIntent.CreateNew -> createData()

            is EditorIntent.Load -> loadData(intent.displayId)

            is EditorIntent.ExitPage -> emitEvent(EditorEvent.ExitPage)

            EditorIntent.Save -> {
                // TODO : Save Display
                emitEvent(EditorEvent.ExitPage)
            }

            // ImageObject
            is EditorIntent.ImageObject.Transform ->
                editorStateHolder.updateState(
                    editorImageState = intent.imageState,
                    bottomBarState = EditingState.Image,
                )

            // TextObject
            is EditorIntent.TextObject.Transform ->
                editorStateHolder.updateState(
                    editorTextState = intent.textState,
                    bottomBarState = EditingState.Text,
                )

            // InfoToolBar
            EditorIntent.InfoToolBar.EditText -> tryEditText()

            EditorIntent.InfoToolBar.EditImage -> tryEditImage()

            EditorIntent.InfoToolBar.EditBackground ->
                editorStateHolder.updateBottomBar(bottomBarState = EditingState.Background)

            EditorIntent.InfoToolBar.EditInfo ->
                editorStateHolder.updateDialog(dialogState = DialogState.InfoEdit(editorStateHolder.value.editorInfoState))

            // TextToolBar
            EditorIntent.TextToolBar.Close ->
                editorStateHolder.deselectObject()

            EditorIntent.TextToolBar.Delete ->
                editorStateHolder.updateDialog(dialogState = DialogState.TextDeleteWarning)

            EditorIntent.TextToolBar.EditText ->
                editorStateHolder.updateDialog(dialogState = DialogState.TextEdit(editorStateHolder.value.editorTextState.text))

            is EditorIntent.TextToolBar.SelectColor ->
                editorStateHolder.updateText(color = intent.color)

            is EditorIntent.TextToolBar.SelectFont ->
                editorStateHolder.updateText(font = intent.font)

            is EditorIntent.TextToolBar.SelectCustomColor ->
                editorStateHolder.updateDialog(dialogState = DialogState.ColorPicker(color = editorStateHolder.value.editorTextState.color))

            // ImageToolBar
            EditorIntent.ImageToolBar.Close ->
                editorStateHolder.updateBottomBar(bottomBarState = EditingState.None)

            EditorIntent.ImageToolBar.Delete ->
                editorStateHolder.updateDialog(dialogState = DialogState.ImageDeleteWarning)

            EditorIntent.ImageToolBar.Change -> {
                // TODO load image
                val imageSource = null
                editorStateHolder.updateImage(imageSource = imageSource)
            }

            is EditorIntent.ImageToolBar.SelectColor ->
                editorStateHolder.updateImage(color = intent.color)

            is EditorIntent.ImageToolBar.SelectCustomColor ->
                editorStateHolder.updateDialog(
                    dialogState = DialogState.ColorPicker(color = editorStateHolder.value.editorImageState.color),
                )

            // BackgroundToolBar
            EditorIntent.BackgroundToolBar.Close ->
                editorStateHolder.resetBackground()

            EditorIntent.BackgroundToolBar.Delete ->
                editorStateHolder.updateDialog(dialogState = DialogState.BackgroundDeleteWarning)

            is EditorIntent.BackgroundToolBar.ChangeBrightness ->
                editorStateHolder.updateBackground(brightness = intent.brightness)

            is EditorIntent.BackgroundToolBar.SelectColor ->
                editorStateHolder.updateBackground(color = intent.color)

            is EditorIntent.BackgroundToolBar.SelectCustomColor ->
                editorStateHolder.updateDialog(
                    dialogState = DialogState.ColorPicker(color = editorStateHolder.value.editorBackgroundState.color),
                )

            // Dialog
            is EditorIntent.Dialog.ExitPage ->
                editorStateHolder.updateDialog(dialogState = DialogState.ExitAsking)

            is EditorIntent.Dialog.ColorPicked -> {
                when (editorStateHolder.value.bottomBarState) {
                    EditingState.Text ->
                        editorStateHolder.updateState(
                            editorTextState = editorStateHolder.value.editorTextState.copy(color = intent.color),
                            dialogState = DialogState.Closed,
                        )

                    EditingState.Image ->
                        editorStateHolder.updateState(
                            editorImageState = editorStateHolder.value.editorImageState.copy(color = intent.color),
                            dialogState = DialogState.Closed,
                        )

                    EditingState.Background ->
                        editorStateHolder.updateState(
                            editorBackgroundState =
                                editorStateHolder.value.editorBackgroundState.copy(
                                    color = intent.color,
                                ),
                            dialogState = DialogState.Closed,
                        )

                    EditingState.None -> {}
                }
            }

            EditorIntent.Dialog.DeleteText ->
                editorStateHolder.updateState(
                    bottomBarState = EditingState.None,
                    editorTextState = DisplayTextState(),
                )

            EditorIntent.Dialog.DeleteImage ->
                editorStateHolder.updateState(
                    bottomBarState = EditingState.None,
                    editorImageState = DisplayImageState(),
                )

            EditorIntent.Dialog.DeleteBackground ->
                editorStateHolder.updateState(
                    bottomBarState = EditingState.None,
                    editorBackgroundState = DisplayBackgroundState(),
                )

            is EditorIntent.Dialog.EditText ->
                editorStateHolder.updateState(
                    editorTextState = editorStateHolder.value.editorTextState.copy(text = intent.text),
                    dialogState = DialogState.Closed,
                )

            is EditorIntent.Dialog.EditInfo ->
                editorStateHolder.updateState(
                    editorInfoState =
                        EditorInfoState(
                            title = intent.title,
                            tags = intent.tags.toPersistentList(),
                        ),
                    dialogState = DialogState.Closed,
                )

            is EditorIntent.Dialog.PickedImage ->
                editorStateHolder.updateState(
                    editorImageState = editorStateHolder.value.editorImageState.copy(imageSource = intent.image),
                    dialogState = DialogState.Closed,
                )

            EditorIntent.Dialog.Close ->
                editorStateHolder.updateDialog(dialogState = DialogState.Closed)
        }
    }

    private fun emitEvent(event: EditorEvent) {
        viewModelScope.launch {
            editorEvent.emit(event)
        }
    }

    private fun createData() {
        editorStateHolder.update {
            DisplayEditorData(
                displayId = null,
                editorInfoState = EditorInfoState(),
                editorImageState = DisplayImageState(),
                editorTextState = DisplayTextState(),
                editorBackgroundState = DisplayBackgroundState(),
            )
        }
    }

    private fun loadData(displayId: Long) {
        viewModelScope.launch {
            // TODO : load from api
        }
    }

    private fun tryEditText() {
        if (editorStateHolder.value.editorTextState.text
                .isEmpty()
        ) {
            editorStateHolder.update {
                editorStateHolder.value.copy(
                    bottomBarState = EditingState.Text,
                    dialogState = DialogState.TextEdit(""),
                )
            }
        } else {
            editorStateHolder.updateBottomBar(bottomBarState = EditingState.Text)
        }
    }

    private fun tryEditImage() {
        if (editorStateHolder.value.editorImageState.imageSource == null) {
            emitEvent(EditorEvent.OpenPhotoGallery)
        } else {
            editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Image) }
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
    val dialogState: DialogState = DialogState.Closed,
) {
    fun toState(): EditorState =
        EditorState.Edit(
            displayId = displayId,
            editorInfoState = editorInfoState,
            displayImageState = editorImageState,
            displayTextState = editorTextState,
            displayBackgroundState = editorBackgroundState,
            editingState = bottomBarState,
            dialogState = dialogState,
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

private fun MutableStateFlow<DisplayEditorData>.updateState(
    displayId: Long? = this.value.displayId,
    editorInfoState: EditorInfoState = this.value.editorInfoState,
    editorImageState: DisplayImageState = this.value.editorImageState,
    editorTextState: DisplayTextState = this.value.editorTextState,
    editorBackgroundState: DisplayBackgroundState = this.value.editorBackgroundState,
    bottomBarState: EditingState = this.value.bottomBarState,
    dialogState: DialogState = this.value.dialogState,
) = update {
    DisplayEditorData(
        displayId = displayId,
        editorInfoState = editorInfoState,
        editorImageState = editorImageState,
        editorTextState = editorTextState,
        editorBackgroundState = editorBackgroundState,
        bottomBarState = bottomBarState,
        dialogState = dialogState,
    )
}

private fun MutableStateFlow<DisplayEditorData>.updateInfo(editorInfoState: EditorInfoState) =
    update { this.value.copy(editorInfoState = editorInfoState) }

private fun MutableStateFlow<DisplayEditorData>.resetText() = update { this.value.copy(editorTextState = DisplayTextState()) }

private fun MutableStateFlow<DisplayEditorData>.updateText(editorTextState: DisplayTextState) =
    update { this.value.copy(editorTextState = editorTextState) }

private fun MutableStateFlow<DisplayEditorData>.updateText(
    isSelected: Boolean = this.value.editorTextState.isSelected,
    text: String = this.value.editorTextState.text,
    color: CustomColor = this.value.editorTextState.color,
    font: FontFamily = this.value.editorTextState.font,
    scale: Float = this.value.editorTextState.scale,
    rotationDegree: Float = this.value.editorTextState.rotationDegree,
    offsetPercentX: Float = this.value.editorTextState.offsetPercentX,
    offsetPercentY: Float = this.value.editorTextState.offsetPercentY,
) = update {
    this.value.copyWithText(
        isSelected = isSelected,
        text = text,
        color = color,
        font = font,
        scale = scale,
        rotationDegree = rotationDegree,
        offsetPercentX = offsetPercentX,
        offsetPercentY = offsetPercentY,
    )
}

private fun MutableStateFlow<DisplayEditorData>.resetImage() = update { this.value.copy(editorImageState = DisplayImageState()) }

private fun MutableStateFlow<DisplayEditorData>.updateImage(editorImageState: DisplayImageState) =
    update { this.value.copy(editorImageState = editorImageState) }

private fun MutableStateFlow<DisplayEditorData>.updateImage(
    isSelected: Boolean = this.value.editorImageState.isSelected,
    imageSource: Any? = this.value.editorImageState.imageSource,
    color: CustomColor = this.value.editorImageState.color,
    scale: Float = this.value.editorImageState.scale,
    rotationDegree: Float = this.value.editorImageState.rotationDegree,
    offsetPercentX: Float = this.value.editorImageState.offsetPercentX,
    offsetPercentY: Float = this.value.editorImageState.offsetPercentY,
) = update {
    this.value.copyWithImage(
        isSelected = isSelected,
        imageSource = imageSource,
        color = color,
        scale = scale,
        rotationDegree = rotationDegree,
        offsetPercentX = offsetPercentX,
        offsetPercentY = offsetPercentY,
    )
}

private fun MutableStateFlow<DisplayEditorData>.resetBackground() =
    update { this.value.copy(editorBackgroundState = DisplayBackgroundState()) }

private fun MutableStateFlow<DisplayEditorData>.updateBackground(editorBackgroundState: DisplayBackgroundState) =
    update { this.value.copy(editorBackgroundState = editorBackgroundState) }

private fun MutableStateFlow<DisplayEditorData>.updateBackground(
    color: CustomColor = this.value.editorBackgroundState.color,
    brightness: Float = this.value.editorBackgroundState.brightness,
) = update {
    this.value.copyWithBackground(
        color = color,
        brightness = brightness,
    )
}

private fun MutableStateFlow<DisplayEditorData>.updateDialog(dialogState: DialogState) =
    update { this.value.copy(dialogState = dialogState) }

private fun MutableStateFlow<DisplayEditorData>.updateBottomBar(bottomBarState: EditingState) =
    update { this.value.copy(bottomBarState = bottomBarState) }

private fun MutableStateFlow<DisplayEditorData>.selectTextObject() =
    update {
        this.value
            .copy(bottomBarState = EditingState.Text)
            .copyWithText(isSelected = true)
            .copyWithImage(isSelected = false)
    }

private fun MutableStateFlow<DisplayEditorData>.selectImageObject() =
    update {
        this.value
            .copy(bottomBarState = EditingState.Image)
            .copyWithImage(isSelected = true)
            .copyWithText(isSelected = false)
    }

private fun MutableStateFlow<DisplayEditorData>.deselectObject() =
    update {
        this.value
            .copy(bottomBarState = EditingState.None)
            .copyWithText(isSelected = false)
            .copyWithImage(isSelected = false)
    }
