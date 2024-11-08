package com.rohkee.feat.display.editor

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    displayId: Long? = null,
    editorViewModel: EditorViewModel = hiltViewModel(),
    onNavigateToDisplayDetail: (Long) -> Unit,
    onPopBackStack: () -> Unit,
    onShowSnackBar: (String) -> Unit = {},
) {
    val editorUIState by editorViewModel.editorState.collectAsStateWithLifecycle()

    val photoGalleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let { editorViewModel.onIntent(EditorIntent.Dialog.PickedImage(uri)) }
        }

    editorViewModel.editorEvent.collectWithLifecycle { event ->
        when (event) {
            is EditorEvent.ExitPage -> onPopBackStack()
            is EditorEvent.ShowSnackBar -> onShowSnackBar(event.message)
            EditorEvent.OpenPhotoGallery -> photoGalleryLauncher.launch("image/*")
        }
    }

    BackHandler {
        editorViewModel.onIntent(EditorIntent.ExitPage)
    }

    LaunchedEffect(displayId) {
        if (displayId == null) {
            editorViewModel.onIntent(EditorIntent.CreateNew)
        } else {
            editorViewModel.onIntent(EditorIntent.Load(displayId))
        }
    }

    EditorContent(
        modifier = modifier,
        state = editorUIState,
        onIntent = editorViewModel::onIntent,
    )
}
