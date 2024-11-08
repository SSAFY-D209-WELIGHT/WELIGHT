package com.rohkee.feat.display.editor

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.font.FontFamily
import com.rohkee.core.ui.component.display.editor.DisplayImageState
import com.rohkee.core.ui.component.display.editor.DisplayTextState
import com.rohkee.core.ui.model.CustomColor

sealed interface EditorIntent {
    data class Load(
        val displayId: Long,
    ) : EditorIntent

    data object CreateNew : EditorIntent

    data object AttemptExitPage : EditorIntent

    data object Save : EditorIntent

    // Text Object
    sealed interface TextObject : EditorIntent {
        data class Transform(
            val textState: DisplayTextState,
        ) : TextObject
    }

    // Image Object
    sealed interface ImageObject : EditorIntent {
        data class Transform(
            val imageState: DisplayImageState,
        ) : ImageObject
    }

    // Info ToolBar
    sealed interface InfoToolBar : EditorIntent {
        data object EditInfo : InfoToolBar

        data object EditText : InfoToolBar

        data object EditImage : InfoToolBar

        data object EditBackground : InfoToolBar
    }

    // Text ToolBar
    sealed interface TextToolBar : EditorIntent {
        data class SelectColor(
            val color: CustomColor,
        ) : TextToolBar

        data object SelectCustomColor : TextToolBar

        data class SelectFont(
            val font: FontFamily,
        ) : TextToolBar

        data object EditText : TextToolBar

        data object Delete : TextToolBar

        data object Close : TextToolBar
    }

    // Image ToolBar
    sealed interface ImageToolBar : EditorIntent {
        data class SelectColor(
            val color: CustomColor,
        ) : ImageToolBar

        data object SelectCustomColor : ImageToolBar

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
        data object Close : Dialog

        data class ColorPicked(
            val color: CustomColor,
        ) : Dialog

        data object ExitPage : Dialog

        data object DeleteText : Dialog

        data object DeleteImage : Dialog

        data object DeleteBackground : Dialog

        data class EditText(
            val text: String,
        ) : Dialog

        data class EditInfo(
            val title: String,
            val tags: List<String>,
        ) : Dialog

        data class PickedImage(
            val context: Context,
            val image: Uri,
        ) : Dialog
    }
}
