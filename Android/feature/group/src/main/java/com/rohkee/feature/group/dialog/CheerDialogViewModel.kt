package com.rohkee.feature.group.dialog

import androidx.compose.runtime.Immutable
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.util.handle
import com.rohkee.core.ui.component.display.editor.DisplayBackgroundState
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.model.ColorType
import com.rohkee.core.ui.model.CustomColor
import com.rohkee.core.ui.util.toComposeColor
import com.rohkee.core.ui.util.toFontFamily
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheerDialogViewModel @Inject constructor(
    private val displayRepository: DisplayRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<CheerDialogState>(CheerDialogState.Loading)
    val state = _state.asStateFlow()

    fun loadDisplay(displayId: Long) {
        viewModelScope.launch {
            displayRepository.getDisplayDetail(displayId).handle(
                onSuccess = { display ->
                    display?.let {
                        _state.update {
                            CheerDialogState.Loaded(
                                imageState =
                                    display.images.firstOrNull()?.let { image ->
                                        DisplayImageState(
                                            imageSource = image.url.toUri(),
                                            color = CustomColor.Single(color = image.color.toComposeColor()),
                                            scale = image.scale,
                                            rotationDegree = image.rotation,
                                            offsetPercentX = image.offsetX,
                                            offsetPercentY = image.offsetY,
                                        )
                                    } ?: DisplayImageState(),
                                textState =
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
                                backgroundState =
                                    DisplayBackgroundState(
                                        color =
                                            display.background.let {
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
                onError = { _, message ->
                    // Log.d("TAG", "loadData: $message")
                },
            )
        }
    }

    fun animate(offset: Float, interval: Float) {

    }
}

@Immutable
sealed interface CheerDialogState {
    data object Loading : CheerDialogState

    data class Loaded(
        val imageState: DisplayImageState,
        val textState: DisplayTextState,
        val backgroundState: DisplayBackgroundState,
    ) : CheerDialogState
}
