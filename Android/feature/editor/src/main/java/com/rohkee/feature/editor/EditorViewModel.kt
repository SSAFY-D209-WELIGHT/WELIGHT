package com.rohkee.feature.editor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rohkee.core.network.model.DisplayBackground
import com.rohkee.core.network.model.DisplayImage
import com.rohkee.core.network.model.DisplayRequest
import com.rohkee.core.network.model.DisplayResponse
import com.rohkee.core.network.model.DisplayText
import com.rohkee.core.network.model.Upload
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.repository.UploadRepository
import com.rohkee.core.network.util.handle
import com.rohkee.core.network.util.process
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.component.display.editor.EditorInfoState
import com.rohkee.core.ui.model.ColorType
import com.rohkee.core.ui.model.CustomColor
import com.rohkee.core.ui.util.toComposeColor
import com.rohkee.core.ui.util.toFontFamily
import com.rohkee.core.ui.util.toFontName
import com.rohkee.core.ui.util.toHexString
import com.rohkee.feature.editor.navigation.EditorRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val displayRepository: DisplayRepository,
    private val uploadRepository: UploadRepository,
) : ViewModel() {
    private val displayId: Long? = savedStateHandle.toRoute<EditorRoute>().displayId

    private val editorStateHolder = MutableStateFlow<DisplayEditorData>(DisplayEditorData(displayId = displayId))

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
                // emitEvent(EditorEvent.ExitPage)
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
                editorStateHolder.updateBottomBar(editingState = EditingState.None)

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

    private suspend fun loadData() {
        if (displayId != null && editorState.value !is EditorState.Edit) {
            displayRepository.getDisplayEdit(displayId).handle(
                onSuccess = { display ->
                    display?.let {
                        editorStateHolder.update {
                            display.toDisplayEditorData(it.displayId!!)
                        }
                    }
                },
                onError = { _, message ->
                    // Log.d("TAG", "loadData: $message")
                },
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

    private fun saveDisplay(
        context: Context,
        bitmap: GraphicsLayer,
    ) {
        val userId = 2 // TODO : userId
        val editorData = editorStateHolder.value

        if (editorData.editorInfoState.title
                .isEmpty()
        ) {
            editorStateHolder.updateDialog(dialogState = DialogState.InfoEdit(editorData.editorInfoState))
            return
        }
        viewModelScope.launch {
            val thumbnailBitmap = bitmap.toImageBitmap().asAndroidBitmap()
            val thumbnailName =
                "${editorStateHolder.value.editorInfoState.title}-${System.currentTimeMillis()}.png"
            val file = thumbnailBitmap.saveToInternalStorage(thumbnailName, context)

            val imageFile =
                editorStateHolder.value.editorImageState.imageSource?.let { uri ->
                    getFileFromUri(context, uri)
                }

            Log.d("TAG", "saveDisplay: $imageFile")

            uploadRepository
                .upload("$userId/thumbnails/$thumbnailName", file)
                .map {
                    it.process(
                        onSuccess = { upload ->
                            when (upload) {
                                is Upload.Completed -> upload.uploadedFile
                                else -> ""
                            }
                        },
                        onError = { _, _ -> "" },
                    )
                }.combine(
                    // 이미지 정보를 내부 저장소에서 가져왔을 경우
                    if (imageFile != null) {
                        uploadRepository.upload("$userId/images/${imageFile.name}", imageFile).map {
                            it.process(
                                onSuccess = { upload ->
                                    when (upload) {
                                        is Upload.Completed -> upload.uploadedFile
                                        else -> ""
                                    }
                                },
                                onError = { _, _ -> "" },
                            )
                        }
                    } else {
                        flow {
                            // 외부 저장소에서 가져온 경우
                            emit(editorData.editorImageState.imageSource?.toString() ?: "")
                        }
                    },
                ) { thumbnailUrl, imageUrl ->
                    if (editorData.isEditMode) {
                        displayRepository.editDisplay(
                            id = editorData.displayId!!,
                            display =
                                editorData.toDisplayRequest(
                                    thumbnailUrl = thumbnailUrl,
                                    imageUrl = imageUrl,
                                ),
                        )
                    } else {
                        displayRepository.createDisplay(
                            display =
                                editorData.toDisplayRequest(
                                    thumbnailUrl = thumbnailUrl,
                                    imageUrl = imageUrl,
                                ),
                        )
                    }
                }.collectLatest { response ->
                    response.handle(
                        onSuccess = {
                            // 저장 성공
                            emitEvent(EditorEvent.ExitPage)
                        },
                        onError = { _, message ->
                            // TODO 저장 실패
                        },
                    )
                }
        }
    }
}

private fun Bitmap.saveToInternalStorage(
    filename: String,
    context: Context,
): File {
    val file = File(context.filesDir, filename)

    file.writeBitmap(this, Bitmap.CompressFormat.PNG, 100)

    return file
}

private fun File.writeBitmap(
    bitmap: Bitmap,
    format: Bitmap.CompressFormat,
    quality: Int,
) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

private fun getFileFromUri(
    context: Context,
    uri: Uri,
): File? {
    val fileName = getFileName(context, uri) ?: return null
    val cacheDir = context.cacheDir
    val file = File(cacheDir, fileName)

    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return null
}

private fun getFileName(
    context: Context,
    uri: Uri,
): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex != -1) {
                return it.getString(displayNameIndex)
            }
        }
    }
    return null
}

private fun DisplayEditorData.toDisplayRequest(
    thumbnailUrl: String,
    imageUrl: String,
): DisplayRequest =
    DisplayRequest(
        title = this.editorInfoState.title,
        tags = this.editorInfoState.tags.toList(),
        thumbnailUrl = thumbnailUrl,
        posted = false,
        images =
            listOf(
                DisplayImage(
                    url = imageUrl,
                    color =
                        this.editorImageState.color.primary
                            .toHexString(),
                    scale = this.editorImageState.scale,
                    rotation = this.editorImageState.rotationDegree,
                    offsetX = this.editorImageState.offsetPercentX,
                    offsetY = this.editorImageState.offsetPercentY,
                ),
            ),
        texts =
            if (this.editorTextState.text.isNotEmpty()) {
                listOf(
                    DisplayText(
                        text = this.editorTextState.text,
                        color =
                            this.editorTextState.color.primary
                                .toHexString(),
                        font = this.editorTextState.font.toFontName(),
                        rotation = this.editorTextState.rotationDegree,
                        scale = this.editorImageState.scale,
                        offsetX = this.editorTextState.offsetPercentX,
                        offsetY = this.editorTextState.offsetPercentY,
                    ),
                )
            } else {
                emptyList()
            },
        background =
            when (val color = this.editorBackgroundState.color) {
                is CustomColor.Single ->
                    DisplayBackground(
                        brightness = this.editorBackgroundState.brightness,
                        isSingle = true,
                        color1 = color.primary.toHexString(),
                        color2 = color.primary.toHexString(),
                        type = ColorType.Radial.name,
                    )

                is CustomColor.Gradient -> {
                    DisplayBackground(
                        brightness = this.editorBackgroundState.brightness,
                        isSingle = false,
                        color1 = color.primary.toHexString(),
                        color2 = color.primary.toHexString(),
                        type = color.type.name,
                    )
                }
            },
    )

private fun DisplayResponse.Editable.toDisplayEditorData(displayId: Long) =
    DisplayEditorData(
        displayId = displayId,
        editorInfoState =
            EditorInfoState(
                title = this.title,
                tags = this.tags.toPersistentList(),
            ),
        editorImageState =
            this.images.firstOrNull()?.let { image ->
                DisplayImageState(
                    imageSource = image.url.toUri(),
                    color = CustomColor.Single(color = image.color.toComposeColor()),
                    scale = image.scale,
                    rotationDegree = image.rotation,
                    offsetPercentX = image.offsetX,
                    offsetPercentY = image.offsetY,
                )
            } ?: DisplayImageState(),
        editorTextState =
            this.texts.firstOrNull()?.let { text ->
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
                    this.background.let {
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
                brightness = this.background.brightness,
            ),
    )
