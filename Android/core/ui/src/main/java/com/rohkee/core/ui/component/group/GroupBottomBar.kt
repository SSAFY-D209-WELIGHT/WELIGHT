package com.rohkee.core.ui.component.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.component.display.TitleRow
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Immutable
data class GroupBottomBarState(
    val title: String,
    val description: String,
    val groupNumber: Int,
    val groupSize: Int,
)

@Composable
fun GroupBottomBar(
    modifier: Modifier = Modifier,
    state: GroupBottomBarState,
    onGroupChange: (Int) -> Unit = {},
) {
    val (openDropdown, setOpenDropdown) = remember { mutableStateOf(false) }

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
                ).padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TitleRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = state.title,
            editable = false,
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = state.description,
            style = Pretendard.SemiBold16,
            color = AppColor.Convex,
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = AppColor.Convex,
        )
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(text = "그룹 선택", style = Pretendard.Medium20, color = AppColor.OnSurface)
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier =
                    Modifier
                        .wrapContentWidth()
                        .background(color = AppColor.Convex, shape = RoundedCornerShape(8.dp))
                        .padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 8.dp)
                        .clickable { setOpenDropdown(true) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Text(
                    text = "${state.groupNumber}번 그룹",
                    style = Pretendard.SemiBold16,
                    color = AppColor.OnConvex,
                )
                Box {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "arrow down",
                        tint = AppColor.OnConvex,
                    )
                    DropdownMenu(
                        modifier = Modifier.background(color = AppColor.Surface),
                        expanded = openDropdown,
                        onDismissRequest = { setOpenDropdown(false) },
                    ) {
                        for (i in 1..state.groupSize) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${i}번 그룹",
                                        style = Pretendard.SemiBold16,
                                        color = AppColor.OnSurface,
                                    )
                                },
                                onClick = {
                                    setOpenDropdown(false)
                                    onGroupChange(i)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun GroupBottomBarPreview() {
    GroupBottomBar(
        state =
            GroupBottomBarState(
                title = "제목",
                description = "설명",
                groupNumber = 1,
                groupSize = 5,
            ),
    )
}
