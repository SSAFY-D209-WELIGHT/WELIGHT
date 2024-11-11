package com.rohkee.core.ui.component.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground

/**
 * UI 상태를 저정하는 클래스
 */
@Immutable
data class DisplayCardState(
    val cardId: Long,
    val imageSource: String? = null,
    val selected: Boolean = false,
)

@Composable
fun DisplayCard(
    modifier: Modifier = Modifier,
    state: DisplayCardState,
    onCardSelected: () -> Unit = {},
) {
    var imageState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onCardSelected() }
                .background(color = AppColor.Surface),
    ) {
        AsyncImage(
            modifier =
            Modifier,
            model = state.imageSource,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            onLoading = { imageState = it },
            onSuccess = { imageState = it },
            onError = { imageState = it },
        )
        when (imageState) {
            is AsyncImagePainter.State.Success -> {}
            else -> {
                Box(
                    modifier =
                        Modifier.fillMaxSize().animateGradientBackground(
                            startColor = Color.Gray,
                            endColor = Color.DarkGray,
                        ),
                ) {
                }
            }
        }
    }
}

@Composable
fun DisplayCardWithFavorite(
    modifier: Modifier = Modifier,
    state: DisplayCardState,
    onCardSelected: () -> Unit = {},
    onFavoriteSelected: (selected: Boolean) -> Unit = {},
) {
    Box(modifier = modifier) {
        DisplayCard(
            state = state,
            onCardSelected = onCardSelected,
        )
        FavoriteToggleButton(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
            selected = state.selected,
            onClick = onFavoriteSelected,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DisplayCardPreview() {
    DisplayCard(state = DisplayCardState(cardId = 0))
}

@Preview(showBackground = true)
@Composable
private fun DisplayCardWithFavoritePreview() {
    DisplayCardWithFavorite(state = DisplayCardState(cardId = 0))
}
