package com.rohkee.feature.group.host

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.component.appbar.ConfirmAppBar
import com.rohkee.core.ui.component.appbar.GradientAppBar
import com.rohkee.core.ui.component.common.ChipGroup
import com.rohkee.core.ui.component.common.RoundedTextInput
import com.rohkee.core.ui.component.group.GroupSizeChip
import com.rohkee.core.ui.component.storage.DisplayCard
import com.rohkee.core.ui.component.storage.InfiniteHorizontalPager
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay

enum class DisplayEffect(
    val text: String,
) {
    NONE("없음"),
    FLASH("점멸"),
    CROSS("교차점멸"),
    WAVE("파도타기");

    companion object {
        fun parse(text: String): DisplayEffect {
            return entries.find { it.text == text } ?: NONE
        }
    }
}

@Composable
fun HostContent(
    modifier: Modifier = Modifier,
    state: HostState,
    onIntent: (HostIntent) -> Unit = {},
) {
    when (state) {
        is HostState.Creation ->
            CreationContent(
                modifier = modifier,
                onClose = { onIntent(HostIntent.Creation.Cancel) },
                onConfirm = { title, description ->
                    onIntent(
                        HostIntent.Creation.Confirm(
                            title,
                            description,
                        ),
                    )
                },
            )

        is HostState.WaitingRoom ->
            WaitingRoomContent(
                modifier = modifier,
                state = state,
                onIntent = onIntent,
            )
    }
}

@Composable
fun WaitingRoomContent(
    modifier: Modifier,
    state: HostState.WaitingRoom,
    onIntent: (HostIntent) -> Unit,
) {
    val options = remember { DisplayEffect.entries.map { it.text }.toPersistentList() }
    val (selected, setSelected) = remember(state) { mutableStateOf(state.effect) }

    val (checked, setChecked) = remember(state) { mutableStateOf(state.doDetect) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = AppColor.BackgroundTransparent)
                .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GradientAppBar(onClick = { onIntent(HostIntent.Control.Exit) }) { }
        Box(modifier = Modifier.weight(0.5f).fillMaxWidth()) {
            InfiniteHorizontalPager(
                modifier = Modifier.fillMaxSize(),
                pageCount = state.list.size,
                pageRatio = 0.3f,
            ) { index ->
                DisplayCard(state = state.list[index], onCardSelected = {})
            }
        }
        Column(
            modifier =
                Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
                    .background(color = AppColor.Surface)
                    .padding(vertical = 16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "방 제목", style = Pretendard.Medium20, color = AppColor.OnSurface)
                Text(text = state.title, style = Pretendard.Regular16, color = AppColor.OnSurface)
                Text(text = "방 설명", style = Pretendard.Medium20, color = AppColor.OnSurface)
                Text(
                    text = state.description,
                    style = Pretendard.Regular16,
                    color = AppColor.OnSurface,
                )
                Text(text = "효과", style = Pretendard.Medium20, color = AppColor.OnSurface)
                ChipGroup(
                    list = options,
                    selected = selected.text,
                ) {
                    val effect = DisplayEffect.parse(it)
                    setSelected(effect)
                    onIntent(HostIntent.Control.ChangeEffect(effect))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(color = AppColor.OnSurface)
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "음원 감지", style = Pretendard.Medium20, color = AppColor.OnSurface)
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = checked,
                    onCheckedChange = setChecked,
                    colors = SwitchDefaults.colors().copy(
                        checkedThumbColor = AppColor.OnConvex,
                        checkedTrackColor = AppColor.Convex,
                        checkedBorderColor = AppColor.OnConvex,
                        uncheckedThumbColor = AppColor.Surface,
                        uncheckedTrackColor = AppColor.Inactive,
                        uncheckedBorderColor = AppColor.Surface,

                    )
                )
            }
            Box(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .background(
                            color = AppColor.Contrast,
                            shape = RoundedCornerShape(4.dp),
                        ).padding(16.dp),
            ) {
                Text(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .clickable { onIntent(HostIntent.Control.StartCheer) },
                    text = "응원 시작",
                    style = Pretendard.SemiBold20,
                    color = AppColor.OnContrast,
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    GroupSizeChip(
                        number = 0,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreationContent(
    modifier: Modifier,
    onClose: () -> Unit = {},
    onConfirm: (title: String, description: String) -> Unit = { _, _ -> },
) {
    val (titleText, setTitleText) = remember { mutableStateOf("") }
    val (descriptionText, setDescriptionText) = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = AppColor.BackgroundTransparent)
                .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ConfirmAppBar(
            modifier = Modifier.fillMaxWidth(),
            onCloseClick = onClose,
            onConfirmClick = {
                onConfirm(titleText, descriptionText)
            },
        )
        RoundedTextInput(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .focusRequester(focusRequester),
            autofocus = true,
            value = titleText,
            hint = "제목", // TODO : string resource
            isError = titleText.isEmpty(),
            errorMessage = "제목을 입력해주세요.", // TODO : string resource,
            onValueChange = { setTitleText(it) },
        )
        RoundedTextInput(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            autofocus = true,
            value = descriptionText,
            hint = "설명", // TODO : string resource
            isError = false,
            errorMessage = "",
            onValueChange = { setDescriptionText(it) },
        )
    }
}

@Preview
@Composable
private fun WaitingRoomContentPreview() {
    WaitingRoomContent(
        modifier = Modifier,
        state =
            HostState.WaitingRoom(
                title = "title",
                description = "description",
                list = persistentListOf(),
                effect = DisplayEffect.NONE,
                doDetect = false,
            ),
        onIntent = {},
    )
}

@Preview
@Composable
private fun CreationContentPreview() {
    CreationContent(modifier = Modifier)
}
