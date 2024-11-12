package com.rohkee.feature.editor

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    editorViewModel: EditorViewModel = hiltViewModel(),
    onNavigateToDisplayDetail: (Long) -> Unit,
    onPopBackStack: () -> Unit,
    onShowSnackBar: (String) -> Unit = {},
) {
    val context = LocalContext.current as ComponentActivity
    val editorUIState by editorViewModel.editorState.collectAsStateWithLifecycle()

    val photoGalleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let { editorViewModel.onIntent(EditorIntent.Dialog.PickedImage(context, uri)) }
        }

    editorViewModel.editorEvent.collectWithLifecycle { event ->
        when (event) {
            is EditorEvent.ExitPage -> onPopBackStack()
            is EditorEvent.ShowSnackBar -> onShowSnackBar(event.message)
            EditorEvent.OpenPhotoGallery -> photoGalleryLauncher.launch("image/*")
            is EditorEvent.Save.Success -> onNavigateToDisplayDetail(event.displayId)
            EditorEvent.Save.Failure -> { // TODO : Snackbar
            }
        }
    }

    BackHandler {
        editorViewModel.onIntent(EditorIntent.AttemptExitPage)
    }

    LaunchedEffect(Unit) {
        context.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AppColor.BackgroundTransparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(AppColor.BackgroundTransparent.toArgb()),
        )
    }

    EditorContent(
        modifier = modifier,
        state = editorUIState,
        onIntent = editorViewModel::onIntent,
    )
}
