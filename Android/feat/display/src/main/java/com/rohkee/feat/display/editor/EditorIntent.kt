package com.rohkee.feat.display.editor

import androidx.compose.ui.text.font.FontFamily
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.model.CustomColor

sealed interface EditorIntent {
    data object ExitPage : EditorIntent

    data object Save : EditorIntent

    // Text Object
    sealed interface TextObject : EditorIntent {
        data object Select : TextObject
        data class Transform(
            val textState: DisplayTextState,
        ) : TextObject
    }

    // Image Object
    sealed interface ImageObject : EditorIntent {
        data object Select : ImageObject
        data class Transform(
            val imageState: DisplayImageState,
        ) : ImageObject
    }
    // Info ToolBar
    sealed interface InfoToolBar : EditorIntent {
        data object EditText : InfoToolBar

        data object EditImage : InfoToolBar

        data object EditBackground : InfoToolBar
    }

    // Text ToolBar
    sealed interface TextToolBar : EditorIntent {
        data object Select : TextToolBar

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
    sealed interface ImageToolBar : EditorIntent {
        data object Select : ImageToolBar

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
    sealed interface BackgroundToolBar : EditorIntent {
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

    // Dialog
    sealed interface Dialog : EditorIntent {
        data class ColorPicked(
            val color: CustomColor,
        ) : Dialog

        data object ExitPage : Dialog

        data object DeleteText : Dialog

        data object DeleteImage : Dialog

        data object DeleteBackground : Dialog
    }
}
