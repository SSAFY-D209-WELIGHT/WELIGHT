package com.rohkee.feature.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    detailViewModel: DetailViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit = {},
    onEditDisplay: (displayId: Long) -> Unit = {},
    onDuplicateDisplay: (displayId: Long) -> Unit = {},
    onDownloadDisplay: (displayId: Long) -> Unit = {},
) {
    val state by detailViewModel.detailState.collectAsStateWithLifecycle()

    detailViewModel.detailEvent.collectWithLifecycle { event ->
        when (event) {
            DetailEvent.ExitPage -> onPopBackStack()
            is DetailEvent.Download.Success -> onDownloadDisplay(event.displayId)
            DetailEvent.Download.Error -> {
                // TODO: 에러 처리
            }
            DetailEvent.Delete.Success -> onPopBackStack()
            DetailEvent.Delete.Error -> {
                // TODO: 에러 처리
            }
            is DetailEvent.Duplicate.Success -> onDuplicateDisplay(event.displayId)
            DetailEvent.Duplicate.Error -> {
                // TODO: 에러 처리
            }
            is DetailEvent.EditDisplay -> onEditDisplay(event.displayId)
        }
    }

    DetailContent(
        modifier = modifier,
        state = state,
        onIntent = detailViewModel::onIntent,
    )
}
