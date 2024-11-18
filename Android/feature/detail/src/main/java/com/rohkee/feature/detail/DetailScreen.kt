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
    onPublishDisplay: (displayId: Long) -> Unit = {},
    onShowSnackbar: (message: String) -> Unit = {},
) {
    val state by detailViewModel.detailState.collectAsStateWithLifecycle()

    detailViewModel.detailEvent.collectWithLifecycle { event ->
        when (event) {
            DetailEvent.ExitPage -> onPopBackStack()
            is DetailEvent.Download.Success -> {
                onDownloadDisplay(event.displayId)
                onShowSnackbar("내 보관함에 추가되었습니다.")
            }
            DetailEvent.Download.Reject -> {
                onShowSnackbar("보관함에 존재하는 디스플레이입니다.")
            }
            DetailEvent.Download.Error -> {
                onShowSnackbar("다운로드 중 오류가 발생하였습니다.")
            }

            DetailEvent.Delete.Success -> {
                onPopBackStack()
                onShowSnackbar("삭제되었습니다.")
            }

            DetailEvent.Delete.Error -> {
                onShowSnackbar("삭제 중 오류가 발생하였습니다.")
            }

            is DetailEvent.Duplicate.Success -> {
                onDuplicateDisplay(event.displayId)
                onShowSnackbar("내 보관함에 복제되었습니다.")
            }

            DetailEvent.Duplicate.Error -> {
                onShowSnackbar("복제 중 오류가 발생하였습니다.")
            }

            is DetailEvent.EditDisplay -> onEditDisplay(event.displayId)
            is DetailEvent.Publish.Success -> {
                onPublishDisplay(event.displayId)
                onShowSnackbar("게시판에 공유되었습니다.")
            }

            DetailEvent.Publish.Error -> {
                onShowSnackbar("공유 중 오류가 발생하였습니다.")
            }
        }
    }

    DetailContent(
        modifier = modifier,
        state = state,
        onIntent = detailViewModel::onIntent,
    )
}
