package com.rohkee.feat.display.editor

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.util.handle
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.EditorInfoState
import com.rohkee.core.ui.model.ColorType
import com.rohkee.core.ui.model.CustomColor
import com.rohkee.core.ui.util.toComposeColor
import com.rohkee.core.ui.util.toFontFamily
import com.rohkee.feat.display.editor.navigation.EditorRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
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
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val displayRepository: DisplayRepository,
) : ViewModel() {
    private val displayId: Long? = savedStateHandle.toRoute<EditorRoute>().id

    private val editorStateHolder = MutableStateFlow<DisplayEditorData>(DisplayEditorData())

    val editorState: StateFlow<EditorState> =
        editorStateHolder
            .onStart {
                loadData()
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
            is EditorIntent.AttemptExitPage -> {
                editorStateHolder.updateDialog(dialogState = DialogState.ExitAsking)
            }

            is EditorIntent.Save -> {
                saveDisplay(intent.context, intent.bitmap)
                //emitEvent(EditorEvent.ExitPage)
            }

            // ImageObject
            is EditorIntent.ImageObject.Transform ->
                editorStateHolder.updateState(
                    displayImageState = intent.imageState,
                    editingState = EditingState.Image,
                )

            // TextObject
            is EditorIntent.TextObject.Transform ->
                editorStateHolder.updateState(
                    displayTextState = intent.textState,
                    editingState = EditingState.Text,
                )

            // InfoToolBar
            EditorIntent.InfoToolBar.EditText -> tryEditText()

            EditorIntent.InfoToolBar.EditImage -> tryEditImage()

            EditorIntent.InfoToolBar.EditBackground ->
                editorStateHolder.updateBottomBar(editingState = EditingState.Background)

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
                editorStateHolder.updateBottomBar(editingState = EditingState.None)

            EditorIntent.ImageToolBar.Delete ->
                editorStateHolder.updateDialog(dialogState = DialogState.ImageDeleteWarning)

            EditorIntent.ImageToolBar.Change -> {
                emitEvent(EditorEvent.OpenPhotoGallery)
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
            is EditorIntent.Dialog.ExitPage -> {
                editorStateHolder.updateDialog(dialogState = DialogState.Closed)
                emitEvent(EditorEvent.ExitPage)
            }

            is EditorIntent.Dialog.ColorPicked -> {
                when (editorStateHolder.value.bottomBarState) {
                    EditingState.Text ->
                        editorStateHolder.updateState(
                            displayTextState = editorStateHolder.value.editorTextState.copy(color = intent.color),
                            dialogState = DialogState.Closed,
                        )

                    EditingState.Image ->
                        editorStateHolder.updateState(
                            displayImageState = editorStateHolder.value.editorImageState.copy(color = intent.color),
                            dialogState = DialogState.Closed,
                        )

                    EditingState.Background ->
                        editorStateHolder.updateState(
                            displayBackgroundState =
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
                    editingState = EditingState.None,
                    displayTextState = DisplayTextState(),
                )

            EditorIntent.Dialog.DeleteImage ->
                editorStateHolder.updateState(
                    editingState = EditingState.None,
                    displayImageState = DisplayImageState(),
                )

            EditorIntent.Dialog.DeleteBackground ->
                editorStateHolder.updateState(
                    editingState = EditingState.None,
                    displayBackgroundState = DisplayBackgroundState(),
                )

            is EditorIntent.Dialog.EditText ->
                editorStateHolder.updateState(
                    displayTextState = editorStateHolder.value.editorTextState.copy(text = intent.text),
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
                    displayImageState = editorStateHolder.value.editorImageState.copy(imageSource = intent.image),
                    dialogState = DialogState.Closed,
                )

            EditorIntent.Dialog.Close ->
                editorStateHolder.updateState(
                    editingState = EditingState.None,
                    dialogState = DialogState.Closed,
                )
        }
    }

    private fun emitEvent(event: EditorEvent) {
        viewModelScope.launch {
            editorEvent.emit(event)
        }
    }

    private fun createData() {
        if (editorState.value is EditorState.Edit) return
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

    private suspend fun loadData() {
        if (displayId != null && editorState.value !is EditorState.Edit) {
            displayRepository.getDisplayEdit(displayId).handle(
                onSuccess = { display ->
                    display?.let {
                        editorStateHolder.update {
                            DisplayEditorData(
                                displayId = display.id,
                                editorInfoState =
                                    EditorInfoState(
                                        title = display.title,
                                        tags = display.tags.toPersistentList(),
                                    ),
                                editorImageState =
                                    display.images.firstOrNull()?.let { image ->
                                        DisplayImageState(
                                            imageSource = image.url,
                                            color = CustomColor.Single(color = image.color.toComposeColor()),
                                            scale = image.scale,
                                            rotationDegree = image.rotation,
                                            offsetPercentX = image.offsetX,
                                            offsetPercentY = image.offsetY,
                                        )
                                    } ?: DisplayImageState(),
                                editorTextState =
                                    display.texts.firstOrNull()?.let { text ->
                                        DisplayTextState(
                                            text = text.text,
                                            color = CustomColor.Single(color = text.color.toComposeColor()),
                                            font = text.font.toFontFamily(),
                                            scale = text.scale,
                                            rotationDegree = text.rotation,
                                            offsetPercentX = text.offsetX,
                                            offsetPercentY = text.offsetY,
                                        )
                                    } ?: DisplayTextState(),
                                editorBackgroundState =
                                    DisplayBackgroundState(
                                        color =
                                            display.background.color.let {
                                                if (it.isSingle) {
                                                    CustomColor.Single(color = it.color1.toComposeColor())
                                                } else {
                                                    CustomColor.Gradient(
                                                        colors =
                                                            persistentListOf(
                                                                it.color1.toComposeColor(),
                                                                it.color2.toComposeColor(),
                                                            ),
                                                        type = ColorType.valueOf(it.type),
                                                    )
                                                }
                                            },
                                        brightness = display.background.brightness,
                                    ),
                            )
                        }
                    }
                },
                onError = { _, _ -> },
            )
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
            editorStateHolder.updateBottomBar(editingState = EditingState.Text)
        }
    }

    private fun tryEditImage() {
        if (editorStateHolder.value.editorImageState.imageSource == null) {
            emitEvent(EditorEvent.OpenPhotoGallery)
        } else {
            editorStateHolder.update { editorStateHolder.value.copy(bottomBarState = EditingState.Image) }
        }
    }

    private fun saveDisplay(context: Context, bitmap: GraphicsLayer) {
        viewModelScope.launch {
            val bitmap = bitmap.toImageBitmap()
            val uri = bitmap.asAndroidBitmap().saveToDisk(context)

            editorStateHolder.updateImage(imageSource = uri)
//            editorStateHolder.value.let { data ->
//                displayRepository.createDisplay(
//                    display =
//                        DisplayRequest(
//                            title = data.editorInfoState.title,
//                            tags = data.editorInfoState.tags.toList(),
//                            thumbnailUrl = TODO(),
//                            posted = false,
//                            images =
//                                listOf(
//                                    DisplayImage(
//                                        url = data.editorImageState.imageSource.toString(),
//                                        color =
//                                            data.editorImageState.color.primary
//                                                .toHexString(),
//                                        scale = data.editorImageState.scale,
//                                        rotation = data.editorImageState.rotationDegree,
//                                        offsetX = data.editorImageState.offsetPercentX,
//                                        offsetY = data.editorImageState.offsetPercentY,
//                                    ),
//                                ),
//                            texts =
//                                listOf(
//                                    DisplayText(
//                                        text = data.editorTextState.text,
//                                        color =
//                                            data.editorTextState.color.primary
//                                                .toHexString(),
//                                        font = data.editorTextState.font.toFontName(),
//                                        rotation = data.editorTextState.rotationDegree,
//                                        scale = data.editorImageState.scale,
//                                        offsetX = data.editorTextState.offsetPercentX,
//                                        offsetY = data.editorTextState.offsetPercentY,
//                                    ),
//                                ),
//                            background = DisplayBackground(
//                                brightness = data.editorBackgroundState.brightness,
//                                color =
//                                when(data.editorBackgroundState.color){
//                                    is CustomColor.Single -> DisplayColor(
//                                        isSingle = true,
//                                        color1 = data.editorBackgroundState.color.primary.toHexString(),
//                                        color2 = data.editorBackgroundState.color.primary.toHexString(),
//                                        type = ColorType.Radial.name,
//                                    )
//                                    is CustomColor.Gradient -> DisplayColor(
//                                        isSingle = data.editorBackgroundState.color is CustomColor.Single,
//                                        color1 = data.editorImageState.color.primary.toHexString(),
//                                        color2 = data.editorImageState.color.primary.toHexString(),
//                                        type = TODO()
//                                    )
//                                }
//                            ),
//                        ),
//                )
//            }
        }
    }
}


private suspend fun Bitmap.saveToDisk(context: Context): Uri {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "screenshot-${System.currentTimeMillis()}.png"
    )

    file.writeBitmap(this, Bitmap.CompressFormat.PNG, 100)

    return scanFilePath(context, file.path) ?: throw Exception("File could not be saved")
}

/**
 * We call [MediaScannerConnection] to index the newly created image inside MediaStore to be visible
 * for other apps, as well as returning its [MediaStore] Uri
 */
private suspend fun scanFilePath(context: Context, filePath: String): Uri? {
    return suspendCancellableCoroutine { continuation ->
        MediaScannerConnection.scanFile(
            context,
            arrayOf(filePath),
            arrayOf("image/png")
        ) { _, scannedUri ->
            if (scannedUri == null) {
                continuation.cancel(Exception("File $filePath could not be scanned"))
            } else {
                continuation.resume(scannedUri)
            }
        }
    }
}

private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}