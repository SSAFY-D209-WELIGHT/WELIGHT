package com.rohkee.core.ui.component.display.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.R
import com.rohkee.core.ui.component.display.TagRow
import com.rohkee.core.ui.component.display.TitleRow
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

sealed interface DetailInfoState {
    @Immutable
    data object Loading : DetailInfoState

    sealed interface Loaded : DetailInfoState {
        val title: String
        val tags: PersistentList<String>
        val author: String

        @Immutable
        data class Default(
            override val title: String,
            override val tags: PersistentList<String>,
            override val author: String,
        ) : Loaded

        @Immutable
        data class Shared(
            override val title: String,
            override val tags: PersistentList<String>,
            override val author: String,
            val liked: Boolean,
            val like: Int,
            val download: Int,
            val comment: Int,
            val stored: Boolean,
        ) : Loaded
    }
}

@Composable
fun InfoBottomBar(
    modifier: Modifier = Modifier,
    state: DetailInfoState,
    onLikeClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
) {
    when (state) {
        DetailInfoState.Loading -> LoadingInfoBottomBar(modifier = modifier)
        is DetailInfoState.Loaded.Default ->
            DefaultInfoBottomBar(
                modifier = modifier,
                state = state,
            )

        is DetailInfoState.Loaded.Shared ->
            SharedInfoBottomBar(
                modifier = modifier,
                state = state,
                onLikeClick = onLikeClick,
                onDownloadClick = onDownloadClick,
                onCommentClick = onCommentClick,
            )
    }
}

@Composable
private fun LoadingInfoBottomBar(modifier: Modifier = Modifier) {
}

@Composable
private fun DefaultInfoBottomBar(
    modifier: Modifier = Modifier,
    state: DetailInfoState.Loaded,
    additionalContent: @Composable () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    AppColor.SurfaceTransparent,
                                    AppColor.BackgroundTransparent,
                                ),
                        ),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        TitleRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = state.title,
            editable = false,
        )
        if (state.tags.isNotEmpty()) {
            TagRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                tags = state.tags,
            )
        }
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "by ${state.author}",
            style = Pretendard.SemiBold16,
            color = AppColor.Convex,
        )
        additionalContent()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SharedInfoBottomBar(
    modifier: Modifier = Modifier,
    state: DetailInfoState.Loaded.Shared,
    onLikeClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
) {
    DefaultInfoBottomBar(
        modifier = modifier,
        state = state,
    ) {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = AppColor.Convex,
        )
        DetailInfoRow(
            liked = state.liked,
            like = state.like,
            download = state.download,
            comment = state.comment,
            hasDownloaded = state.stored,
            onLikeClick = onLikeClick,
            onDownloadClick = onDownloadClick,
            onCommentClick = onCommentClick,
        )
    }
}

@Composable
private fun DetailInfoRow(
    modifier: Modifier = Modifier,
    liked: Boolean,
    like: Int,
    download: Int,
    hasDownloaded: Boolean = false,
    comment: Int,
    onLikeClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
) {
    val likeIcon =
        rememberVectorPainter(if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder)

    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconWithNumber(
            modifier = Modifier,
            icon = likeIcon,
            number = like,
            onClick = onLikeClick,
        )
        IconWithNumber(
            modifier = Modifier,
            icon = painterResource(R.drawable.download),
            number = download,
            onClick = onDownloadClick,
            enabled = !hasDownloaded,
        )
        Spacer(modifier = Modifier.weight(1f))
//        IconWithNumber(
//            modifier = Modifier,
//            icon = painterResource(R.drawable.text_balloon),
//            number = comment,
//            onClick = onCommentClick,
//        )
    }
}

@Composable
private fun IconWithNumber(
    modifier: Modifier = Modifier,
    icon: Painter,
    number: Int,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.wrapContentWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = icon,
            contentDescription = null,
            tint = if(enabled) AppColor.OnBackground else AppColor.Inactive,
        )
        Text(
            text = number.toString(),
            style = Pretendard.Medium16,
            color = if(enabled) AppColor.OnBackground else AppColor.Inactive,
        )
    }
}

@Preview
@Composable
private fun IconWithNumberPreview() {
    IconWithNumber(icon = rememberVectorPainter(Icons.Default.FavoriteBorder), number = 10)
}

@Preview
@Composable
private fun InfoBottomBarPreview() {
    InfoBottomBar(
        state =
            DetailInfoState.Loaded.Default(
                title = "제목",
                tags = persistentListOf("태그1", "태그2", "태그3", "태그4", "태그5", "태그6", "태그7"),
                author = "작성자",
            ),
    )
}

@Preview
@Composable
private fun SharedInfoBottomBarPreview() {
    SharedInfoBottomBar(
        state =
            DetailInfoState.Loaded.Shared(
                title = "제목",
                tags = persistentListOf("태그1", "태그2", "태그3", "태그4", "태그5", "태그6", "태그7"),
                author = "작성자",
                liked = true,
                like = 10,
                download = 20,
                comment = 30,
                stored = false,
            ),
    )
}
