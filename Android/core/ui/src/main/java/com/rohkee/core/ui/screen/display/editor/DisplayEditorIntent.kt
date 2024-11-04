package com.rohkee.core.ui.screen.display.editor

import androidx.compose.ui.text.font.FontFamily
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.model.CustomColor

sealed interface DisplayEditorIntent {
    data object ExitPage : DisplayEditorIntent

    data object SaveDisplay : DisplayEditorIntent

    data class UpdateImageState(
        val imageState: DisplayImageState,
    ) : DisplayEditorIntent

    data class UpdateTextState(
        val textState: DisplayTextState,
    ) : DisplayEditorIntent

    // Info ToolBar
    sealed interface InfoToolBar : DisplayEditorIntent {
        data object EditText : InfoToolBar

        data object EditImage : InfoToolBar

        data object EditBackground : InfoToolBar
    }

    // Text ToolBar
    sealed interface TextToolBar : DisplayEditorIntent {
        data class SelectColor(
            val color: CustomColor,
        ) : TextToolBar

        data object SelectCustomColor : TextToolBar

        data class SelectFont(
            val font: FontFamily,
        ) : TextToolBar

        data class Rotate(
            val degree: Float,
        ) : TextToolBar

        data object Delete : TextToolBar

        data object Close : TextToolBar
    }

    // Image ToolBar
    sealed interface ImageToolBar : DisplayEditorIntent {
        data class SelectColor(
            val color: CustomColor,
        ) : ImageToolBar

        data object SelectCustomColor : ImageToolBar

        data class Rotate(
            val degree: Float,
        ) : ImageToolBar

        data object Change : ImageToolBar

        data object Delete : ImageToolBar

        data object Close : ImageToolBar
    }

    // Background ToolBar
    sealed interface BackgroundToolBar : DisplayEditorIntent {
        data class SelectColor(
            val color: CustomColor,
        ) : BackgroundToolBar

        data object SelectCustomColor : BackgroundToolBar

        data class ChangeBrightness(
            val brightness: Float,
        ) : BackgroundToolBar

        data object Delete : BackgroundToolBar

        data object Close : BackgroundToolBar
    }
}
