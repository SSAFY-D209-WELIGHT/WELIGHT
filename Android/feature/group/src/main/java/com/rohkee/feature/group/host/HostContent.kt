package com.rohkee.feature.group.host

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.rohkee.core.ui.component.appbar.ConfirmAppBar
import com.rohkee.core.ui.component.appbar.GradientAppBar
import com.rohkee.core.ui.component.common.ChipGroup
import com.rohkee.core.ui.component.common.RoundedTextInput
import com.rohkee.core.ui.component.group.GroupSizeChip
import com.rohkee.core.ui.component.storage.DisplayCard
import com.rohkee.core.ui.component.storage.DisplayCardState
import com.rohkee.core.ui.component.storage.RatioHorizontalPager
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard
import com.rohkee.feature.group.dialog.DisplaySelectionDialog
import com.rohkee.feature.group.util.MultiplePermissionHandler
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay

enum class DisplayEffect(
    val text: String,
) {
    NONE("없음"),
    FLASH("점멸"),
    CROSS("교차점멸"),
    WAVE("파도타기"),
    ;

    companion object {
        fun parse(text: String): DisplayEffect = entries.find { it.text == text } ?: NONE
    }
}

@SuppressLint("MissingPermission")
@Composable
fun HostContent(
    modifier: Modifier = Modifier,
    state: HostState,
    onIntent: (HostIntent) -> Unit = {},
) {
    if (state is HostState.Creation &&
        state.dialogState is DialogState.SelectDisplay ||
        state is HostState.WaitingRoom &&
        state.dialogState is DialogState.SelectDisplay
    ) {
        DisplaySelectionDialog(
            modifier = Modifier.fillMaxSize(),
            onDismiss = { onIntent(HostIntent.Dialog.Cancel) },
            onConfirm = { displayId, thumbnailUrl ->
                onIntent(
                    HostIntent.Dialog.SelectDisplay(
                        displayId,
                        thumbnailUrl,
                    ),
                )
            },
        )
    }

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        when (state) {
            is HostState.Creation ->
                CreationContent(
                    modifier = Modifier.padding(innerPadding),
                    state = state,
                    onIntent = onIntent,
                )

            is HostState.WaitingRoom ->
                WaitingRoomContent(
                    modifier = modifier.padding(innerPadding),
                    state = state,
                    onIntent = onIntent,
                )
        }
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
        GradientAppBar(modifier = Modifier.fillMaxWidth(), onClick = { onIntent(HostIntent.Control.Exit) }) { }
        Box(
            modifier =
                Modifier
                    .weight(0.5f)
                    .fillMaxWidth(),
        ) {
            DisplayList(
                modifier = Modifier.fillMaxSize(),
                list = state.list,
                onAdd = { onIntent(HostIntent.Control.AddDisplayGroup) },
            )
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
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp),
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
                    colors =
                        SwitchDefaults.colors().copy(
                            checkedThumbColor = AppColor.OnConvex,
                            checkedTrackColor = AppColor.Convex,
                            checkedBorderColor = AppColor.OnConvex,
                            uncheckedThumbColor = AppColor.Surface,
                            uncheckedTrackColor = AppColor.Inactive,
                            uncheckedBorderColor = AppColor.Surface,
                        ),
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

@SuppressLint("MissingPermission")
@Composable
private fun CreationContent(
    modifier: Modifier,
    state: HostState.Creation,
    onIntent: (HostIntent) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    MultiplePermissionHandler(
        permissions =
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
    ) { result ->
        if (result.all { it.value }) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                latitude = it.latitude
                longitude = it.longitude
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = AppColor.BackgroundTransparent),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ConfirmAppBar(
            modifier = Modifier.fillMaxWidth(),
            onCloseClick = { onIntent(HostIntent.Creation.Cancel) },
            onConfirmClick = {
                onIntent(
                    HostIntent.Creation.CreateRoom(latitude = latitude, longitude = longitude),
                )
            },
        )
        RowTitleText(modifier = Modifier.padding(horizontal = 16.dp), text = "응원방 이름")
        RoundedTextInput(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .focusRequester(focusRequester),
            autofocus = true,
            value = state.title,
            hint = "제목", // TODO : string resource
            isError = state.title.isEmpty(),
            errorMessage = "제목을 입력해주세요.", // TODO : string resource,
            onValueChange = { onIntent(HostIntent.Creation.UpdateTitle(it)) },
        )
        RowTitleText(modifier = Modifier.padding(horizontal = 16.dp), text = "응원방 설명")
        RoundedTextInput(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            autofocus = true,
            value = state.description,
            hint = "설명", // TODO : string resource
            isError = false,
            errorMessage = "",
            onValueChange = { onIntent(HostIntent.Creation.UpdateDescription(it)) },
        )
        RowTitleText(modifier = Modifier.padding(horizontal = 16.dp), text = "디스플레이 목록")
        DisplayList(
            modifier = Modifier,
            list = state.list,
            onAdd = { onIntent(HostIntent.Creation.AddDisplay) },
        )
    }
}

@Composable
private fun RowTitleText(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        style = Pretendard.SemiBold20,
        color = AppColor.OnSurface,
    )
}

@Composable
private fun DisplayList(
    modifier: Modifier = Modifier,
    list: List<DisplayCardState>,
    onAdd: () -> Unit,
) {
    RatioHorizontalPager(
        modifier = modifier,
        pageCount = list.size + 1,
        pageRatio = 0.4f,
    ) { index ->
        if (index == list.size) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .aspectRatio(0.5f)
                        .background(color = AppColor.Surface, shape = RoundedCornerShape(4.dp))
                        .clickable { onAdd() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(modifier = Modifier.size(32.dp), imageVector = Icons.Default.AddCircle, contentDescription = "add")
            }
        } else {
            DisplayCard(
                modifier = Modifier.aspectRatio(0.5f).clip(RoundedCornerShape(4.dp)),
                state = list[index],
                onCardSelected = {},
            )
        }
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
                dialogState = DialogState.Closed,
            ),
        onIntent = {},
    )
}

@Preview
@Composable
private fun CreationContentPreview() {
    CreationContent(
        modifier = Modifier,
        state =
            HostState.Creation(
                title = "title",
                description = "description",
                list = persistentListOf(),
                dialogState = DialogState.Closed,
            ),
        onIntent = {},
    )
}
