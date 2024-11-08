package com.rohkee.core.ui.component.display.editor

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.R
import com.rohkee.core.ui.component.display.TagRow
import com.rohkee.core.ui.component.display.TitleRow
import com.rohkee.core.ui.theme.AppColor
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class EditorInfoState(
    val title: String = "",
    val tags: PersistentList<String> = persistentListOf(),
)

@Composable
fun InfoToolBar(
    modifier: Modifier = Modifier,
    state: EditorInfoState,
    onEditInfo: () -> Unit = {},
    onTextEditClick: () -> Unit = {},
    onImageEditClick: () -> Unit = {},
    onBackgroundEditClick: () -> Unit = {},
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
            onEditClick = onEditInfo,
        )
        TagRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            tags = state.tags,
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = AppColor.Convex,
        )
        IconRow(
            onTextEditClick = onTextEditClick,
            onImageEditClick = onImageEditClick,
            onBackgroundEditClick = onBackgroundEditClick,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun IconRow(
    modifier: Modifier = Modifier,
    onTextEditClick: () -> Unit = {},
    onImageEditClick: () -> Unit = {},
    onBackgroundEditClick: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Icon(
            modifier =
                Modifier
                    .size(32.dp)
                    .clickable { onTextEditClick() },
            painter = painterResource(R.drawable.text_edit),
            contentDescription = "Edit",
            tint = AppColor.OnSurface,
        )
        Icon(
            modifier =
                Modifier
                    .size(32.dp)
                    .clickable { onImageEditClick() },
            painter = painterResource(R.drawable.image_edit),
            contentDescription = "Edit",
            tint = AppColor.OnSurface,
        )
        Icon(
            modifier =
                Modifier
                    .size(32.dp)
                    .clickable { onBackgroundEditClick() },
            painter = painterResource(R.drawable.background_edit),
            contentDescription = "Edit",
            tint = AppColor.OnSurface,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomToolBarPreview() {
    InfoToolBar(
        state =
            EditorInfoState(
                title = "",
                tags = persistentListOf(),
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun BottomToolBarPreviewWithData() {
    InfoToolBar(
        state =
            EditorInfoState(
                title = "제목",
                tags = persistentListOf("태그1", "긴_태그2", "태그3", "태그4", "길고_긴_태그5", "태그6", "태그7"),
            ),
    )
}
