package com.rohkee.feat.detail

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
) {
    val state by detailViewModel.detailState.collectAsStateWithLifecycle()

    detailViewModel.detailEvent.collectWithLifecycle { event ->
        when (event) {
            DetailEvent.ExitPage -> TODO()
            DetailEvent.Download.Success -> TODO()
            DetailEvent.Download.Error -> TODO()
            DetailEvent.Delete.Error -> TODO()
            DetailEvent.Delete.Success -> TODO()
            DetailEvent.Duplicate.Error -> TODO()
            is DetailEvent.Duplicate.Success -> TODO()
            is DetailEvent.EditDisplay -> TODO()
        }
    }

    DetailContent(
        modifier = modifier,
        state = state,
        onIntent = detailViewModel::onIntent,
    )
}
