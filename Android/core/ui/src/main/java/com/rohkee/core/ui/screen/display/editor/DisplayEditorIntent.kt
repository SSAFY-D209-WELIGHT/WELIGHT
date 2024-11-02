package com.rohkee.core.ui.screen.display.editor

sealed interface DisplayEditorIntent {
    data object ExitPage : DisplayEditorIntent

    data object SaveDisplay : DisplayEditorIntent

    data object EditInfo : DisplayEditorIntent
}
