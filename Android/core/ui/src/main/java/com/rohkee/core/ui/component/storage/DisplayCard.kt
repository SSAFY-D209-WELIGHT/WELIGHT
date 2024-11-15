package com.rohkee.core.ui.component.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.component.common.GradientImageLoader
import com.rohkee.core.ui.theme.AppColor

/**
 * UI 상태를 저정하는 클래스
 */
@Immutable
data class DisplayCardState(
    val cardId: Long = 0,
    val imageSource: String? = null,
    val selected: Boolean = false,
)

@Composable
fun DisplayCard(
    modifier: Modifier = Modifier,
    state: DisplayCardState,
    onCardSelected: () -> Unit = {},
) {
    GradientImageLoader(
        modifier =
            modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onCardSelected() }
                .background(color = AppColor.Surface),
        imageSource = state.imageSource,
    )
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
