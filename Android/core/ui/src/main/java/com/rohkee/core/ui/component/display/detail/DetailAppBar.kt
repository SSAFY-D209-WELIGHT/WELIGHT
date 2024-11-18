package com.rohkee.core.ui.component.display.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.R
import com.rohkee.core.ui.component.appbar.GradientAppBar
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

sealed interface DetailAppBarState {
    @Immutable
    data object View : DetailAppBarState

    sealed interface Editable : DetailAppBarState {
        val isFavorite: Boolean

        @Immutable
        data class Default(
            override val isFavorite: Boolean,
        ) : Editable

        @Immutable
        data class Shared(
            override val isFavorite: Boolean,
        ) : Editable
    }
}

@Composable
fun DetailAppBar(
    modifier: Modifier = Modifier,
    state: DetailAppBarState,
    onCloseClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onPublishClick: () -> Unit = {},
    onDuplicateClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    when (state) {
        is DetailAppBarState.View -> GradientAppBar(modifier = modifier, onClick = onCloseClick) {}
        is DetailAppBarState.Editable ->
            DefaultDetailAppBar(
                modifier = modifier,
                state = state,
                onCloseClick = onCloseClick,
                onFavoriteClick = onFavoriteClick,
                onPublishClick = onPublishClick,
                onDuplicateClick = onDuplicateClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
            )
    }
}

@Composable
fun DefaultDetailAppBar(
    modifier: Modifier = Modifier,
    state: DetailAppBarState.Editable,
    onCloseClick: () -> Unit,
    onFavoriteClick: () -> Unit = {},
    onPublishClick: () -> Unit = {},
    onDuplicateClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    val favorite =
        remember(state.isFavorite) { if (state.isFavorite) R.drawable.favorite_star_filled else R.drawable.favorite_star }

    val (openDropdown, setOpenDropdown) = remember { mutableStateOf(false) }

    GradientAppBar(
        modifier = modifier,
        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
        onClick = onCloseClick,
    ) {
        Icon(
            modifier =
                Modifier
                    .size(28.dp)
                    .clickable { onFavoriteClick() },
            painter = painterResource(favorite),
            contentDescription = "favorite",
            tint = AppColor.OnBackground,
        )
        Box {
            Icon(
                modifier =
                    Modifier
                        .size(28.dp)
                        .clickable { setOpenDropdown(true) },
                imageVector = Icons.Default.MoreVert,
                contentDescription = "more",
                tint = AppColor.OnBackground,
            )
            DropdownMenu(
                modifier = Modifier.background(color = AppColor.Surface),
                expanded = openDropdown,
                onDismissRequest = { setOpenDropdown(false) },
            ) {
                if (state is DetailAppBarState.Editable.Default) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "게시하기",
                                style = Pretendard.SemiBold16,
                                color = AppColor.OnSurface,
                            )
                        },
                        onClick = {
                            setOpenDropdown(false)
                            onPublishClick()
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "수정",
                                style = Pretendard.SemiBold16,
                                color = AppColor.OnSurface,
                            )
                        },
                        onClick = {
                            setOpenDropdown(false)
                            onEditClick()
                        },
                    )
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "복제",
                            style = Pretendard.SemiBold16,
                            color = AppColor.OnSurface,
                        )
                    },
                    onClick = {
                        setOpenDropdown(false)
                        onDuplicateClick()
                    },
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "삭제",
                            style = Pretendard.SemiBold16,
                            color = AppColor.OnSurface,
                        )
                    },
                    onClick = {
                        setOpenDropdown(false)
                        onDeleteClick()
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun DetailAppBarPreview() {
    DetailAppBar(state = DetailAppBarState.View)
}

@Preview
@Composable
fun DetailAppBarOptionsPreview() {
    DetailAppBar(state = DetailAppBarState.Editable.Shared(isFavorite = true))
}
