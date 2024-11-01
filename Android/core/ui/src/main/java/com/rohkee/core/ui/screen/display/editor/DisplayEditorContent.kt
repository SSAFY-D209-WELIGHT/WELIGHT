package com.rohkee.core.ui.screen.display.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.animateGradientBackground

@Composable
fun DisplayEditorContent(
    modifier: Modifier = Modifier,
    state: DisplayEditorState,
    onIntent: (DisplayEditorIntent) -> Unit = {},
) {
    when (state) {
        is DisplayEditorState.Loading -> {
        }

        is DisplayEditorState.Edit -> {
        }

        is DisplayEditorState.Create -> {
        }

        is DisplayEditorState.Error -> {
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier =
            Modifier
                .animateGradientBackground(
                    startColor = AppColor.Background,
                    endColor = AppColor.Surface,
                ),
    )
}

@Composable
private fun EditContent(modifier: Modifier = Modifier) {
}

@Composable
private fun CreateContent(modifier: Modifier = Modifier) {
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier) {
}
