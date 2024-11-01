package com.rohkee.core.ui.component.display.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

@Immutable
sealed interface BottomToolBarState {
    data class Info(
        val title: String,
        val tags: List<String>,
    )
}

@Composable
fun BottomToolBar(
    modifier: Modifier = Modifier,
    title: String = "",
    tags: List<String> = emptyList(),
    onEditClick: () -> Unit = {},
) {

}

