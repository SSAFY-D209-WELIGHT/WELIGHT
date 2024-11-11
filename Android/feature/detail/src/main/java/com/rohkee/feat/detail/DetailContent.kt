package com.rohkee.feat.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rohkee.core.ui.component.display.detail.DetailAppBar
import com.rohkee.core.ui.component.display.detail.DetailAppBarState
import com.rohkee.core.ui.component.display.detail.DetailDisplay
import com.rohkee.core.ui.component.display.detail.DetailInfoState
import com.rohkee.core.ui.component.display.detail.InfoBottomBar
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground
import kotlinx.collections.immutable.persistentListOf

@Composable
fun DetailContent(
    modifier: Modifier = Modifier,
    state: DetailState,
    onIntent: (DetailIntent) -> Unit,
) {
    when (state) {
        is DetailState.Loading -> LoadingContent(modifier = modifier)

        is DetailState.Loaded ->
            EditContent(
                modifier = modifier,
                state = state,
                onIntent = onIntent,
            )

        is DetailState.Error -> {
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .animateGradientBackground(
                    startColor = AppColor.Background,
                    endColor = AppColor.Surface,
                ),
    )
}

@Composable
private fun EditContent(
    modifier: Modifier = Modifier,
    state: DetailState.Loaded,
    onIntent: (DetailIntent) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Box {
            DetailDisplay(
                modifier = Modifier.clickable { onIntent(DetailIntent.ToggleUI) },
                imageSource = state.thumbnailUrl,
            )
            DetailAppBar(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = innerPadding.calculateTopPadding()),
                state = state.detailAppBarState,
                onCloseClick = { onIntent(DetailIntent.ExitPage) },
                onEditClick = { onIntent(DetailIntent.Edit) },
                onFavoriteClick = { onIntent(DetailIntent.ToggleFavorite) },
                onPublishClick = { onIntent(DetailIntent.Post) },
                onDuplicateClick = { onIntent(DetailIntent.Duplicate) },
                onDeleteClick = { onIntent(DetailIntent.Delete) },
            )
            InfoBottomBar(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                state = state.detailInfoState,
                onLikeClick = { onIntent(DetailIntent.ToggleLike) },
                onDownloadClick = { onIntent(DetailIntent.Download) },
                onCommentClick = { onIntent(DetailIntent.Comment) },
            )
        }
    }
}

@Preview
@Composable
fun DetailContentPreview() {
    DetailContent(
        modifier = Modifier,
        state =
            DetailState.Loaded(
                thumbnailUrl = "",
                detailAppBarState = DetailAppBarState.View,
                detailInfoState =
                    DetailInfoState.Loaded.Default(
                        title = "",
                        tags = persistentListOf(),
                        author = "",
                    ),
                displayId = 1,
                displayImageState = TODO(),
                displayTextState = TODO(),
                displayBackgroundState = TODO(),
            ),
        onIntent = {},
    )
}
