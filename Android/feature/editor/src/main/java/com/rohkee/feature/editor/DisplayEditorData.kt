package com.rohkee.feature.editor

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontFamily
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.EditorInfoState
import com.rohkee.core.ui.model.CustomColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


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
    val isEditMode = displayId != null

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
        imageSource: Uri? = editorImageState.imageSource,
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

internal fun MutableStateFlow<DisplayEditorData>.updateState(
    displayId: Long? = this.value.displayId,
    editorInfoState: EditorInfoState = this.value.editorInfoState,
    displayImageState: DisplayImageState = this.value.editorImageState,
    displayTextState: DisplayTextState = this.value.editorTextState,
    displayBackgroundState: DisplayBackgroundState = this.value.editorBackgroundState,
    editingState: EditingState = this.value.bottomBarState,
    dialogState: DialogState = this.value.dialogState,
) = update {
    DisplayEditorData(
        displayId = displayId,
        editorInfoState = editorInfoState,
        editorImageState = displayImageState,
        editorTextState = displayTextState,
        editorBackgroundState = displayBackgroundState,
        bottomBarState = editingState,
        dialogState = dialogState,
    )
}

internal fun MutableStateFlow<DisplayEditorData>.updateInfo(editorInfoState: EditorInfoState) =
    update { this.value.copy(editorInfoState = editorInfoState) }

internal fun MutableStateFlow<DisplayEditorData>.resetText() = update { this.value.copy(editorTextState = DisplayTextState()) }

internal fun MutableStateFlow<DisplayEditorData>.updateText(editorTextState: DisplayTextState) =
    update { this.value.copy(editorTextState = editorTextState) }

internal fun MutableStateFlow<DisplayEditorData>.updateText(
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

internal fun MutableStateFlow<DisplayEditorData>.resetImage() = update { this.value.copy(editorImageState = DisplayImageState()) }

internal fun MutableStateFlow<DisplayEditorData>.updateImage(editorImageState: DisplayImageState) =
    update { this.value.copy(editorImageState = editorImageState) }

internal fun MutableStateFlow<DisplayEditorData>.updateImage(
    isSelected: Boolean = this.value.editorImageState.isSelected,
    imageSource: Uri? = this.value.editorImageState.imageSource,
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

internal fun MutableStateFlow<DisplayEditorData>.resetBackground() =
    update { this.value.copy(editorBackgroundState = DisplayBackgroundState()) }

internal fun MutableStateFlow<DisplayEditorData>.updateBackground(displayBackgroundState: DisplayBackgroundState) =
    update { this.value.copy(editorBackgroundState = displayBackgroundState) }

internal fun MutableStateFlow<DisplayEditorData>.updateBackground(
    color: CustomColor = this.value.editorBackgroundState.color,
    brightness: Float = this.value.editorBackgroundState.brightness,
) = update {
    this.value.copyWithBackground(
        color = color,
        brightness = brightness,
    )
}

internal fun MutableStateFlow<DisplayEditorData>.updateDialog(dialogState: DialogState) =
    update { this.value.copy(dialogState = dialogState) }

internal fun MutableStateFlow<DisplayEditorData>.updateBottomBar(editingState: EditingState) =
    update { this.value.copy(bottomBarState = editingState) }

internal fun MutableStateFlow<DisplayEditorData>.selectTextObject() =
    update {
        this.value
            .copy(bottomBarState = EditingState.Text)
            .copyWithText(isSelected = true)
            .copyWithImage(isSelected = false)
    }

internal fun MutableStateFlow<DisplayEditorData>.selectImageObject() =
    update {
        this.value
            .copy(bottomBarState = EditingState.Image)
            .copyWithImage(isSelected = true)
            .copyWithText(isSelected = false)
    }

internal fun MutableStateFlow<DisplayEditorData>.deselectObject() =
    update {
        this.value
            .copy(bottomBarState = EditingState.None)
            .copyWithText(isSelected = false)
            .copyWithImage(isSelected = false)
    }
