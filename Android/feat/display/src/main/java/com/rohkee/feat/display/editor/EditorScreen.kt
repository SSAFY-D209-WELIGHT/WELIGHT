package com.rohkee.feat.display.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.screen.display.editor.DisplayEditorContent
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    editorViewModel: EditorViewModel = hiltViewModel(),
    onNavigateToDisplayDetail: (Long) -> Unit,
    onPopBackStack: () -> Unit,
) {
    val editorUIState by editorViewModel.editorState.collectAsStateWithLifecycle()

    editorViewModel.editorEvent.collectWithLifecycle {
        when (it) {
            is EditorEvent.ExitPage -> onPopBackStack()
            else -> {}
        }
    }

    DisplayEditorContent(
        state = editorUIState,
        onIntent = editorViewModel::onIntent,
    )
}