package com.rohkee.feat.display.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rohkee.core.ui.component.appbar.ConfirmAppBar
import com.rohkee.core.ui.component.common.RoundedTextInput
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard
import com.rohkee.feat.display.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfoEditDialog(
    modifier: Modifier = Modifier,
    title: String,
    tags: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, tags: List<String>) -> Unit,
) {
    val (titleText, setTitleText) = remember(title) { mutableStateOf(title) }
    val (titleSizeError, setTitleSizeError) = remember { mutableStateOf(false) }
    val (tagText, setTagText) = remember { mutableStateOf("") }
    val (tagSizeError, setTagSizeError) = remember { mutableStateOf(false) }
    val (tagList, setTagList) = remember(tags) { mutableStateOf(tags) }
    val (tagListSizeError, setTagListSizeError) = remember { mutableStateOf(false) }
    val (hasEdited, setHasEdited) = remember { mutableStateOf(false) }



    fun inputTitle(title: String) {
        setHasEdited(true)
        setTitleSizeError(title.length > 100)
        if (title.length <= 100) {
            setTitleText(title)
        } else {
            setTitleText(title.substring(0 until 100))
        }
    }

    fun inputTag(tag: String) {
        setHasEdited(true)
        setTagSizeError(tag.length > 50)
        if (tag.length <= 50) {
            setTagText(tag)
        } else {
            setTagText(tag.substring(0 until 50))
        }
    }

    fun addToTagList(tag: String) {
        setTagListSizeError(tagList.size >= 10)
        if (tagList.size < 10) setTagList(tagList + tag)
        setTagText("")
    }

    Dialog(
        onDismissRequest = {},
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
    ) {
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
                onCloseClick = onDismiss,
                onConfirmClick = {
                    if (hasEdited && titleText.isNotBlank()) {
                        onConfirm(titleText, tagList)
                    } else {
                        setHasEdited(true)
                    }
                },
            )
            RoundedTextInput(
                modifier = Modifier.fillMaxWidth().padding(16.dp).focusRequester(focusRequester),
                autofocus = true,
                value = titleText,
                hint = stringResource(R.string.dialog_info_title_hint),
                isError = (hasEdited && titleText.isBlank()) || titleSizeError,
                errorMessage = stringResource(R.string.dialog_info_title_error),
                onValueChange = { inputTitle(it) },
            )
            RoundedTextInput(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                autofocus = true,
                value = tagText,
                hint = stringResource(R.string.dialog_info_tag_hint),
                isError = tagSizeError,
                errorMessage = stringResource(R.string.dialog_info_title_error),
                onValueChange = { inputTag(it) },
                trailingContent = {
                    Box(
                        modifier =
                            Modifier
                                .padding(end = 8.dp)
                                .background(color = AppColor.Convex, shape = RoundedCornerShape(4.dp))
                                .padding(8.dp),
                    ) {
                        Text(
                            modifier = Modifier.clickable { addToTagList(tagText) },
                            text = stringResource(R.string.dialog_info_tag_add),
                            style = Pretendard.SemiBold16,
                        )
                    }
                },
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (tag in tagList) {
                    TagChip(
                        tag = tag,
                        onDelete = { setTagList(tagList - tag) },
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TagChip(
    modifier: Modifier = Modifier,
    tag: String,
    onDelete: (tag: String) -> Unit,
) {
    Row(
        modifier =
            modifier
                .background(color = AppColor.Convex, shape = CircleShape)
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier,
            text = tag,
            style = Pretendard.SemiBold16,
            color = AppColor.OnConvex,
            textAlign = TextAlign.Center,
        )
        Icon(
            modifier = Modifier.size(16.dp).clickable { onDelete(tag) },
            imageVector = Icons.Rounded.Close,
            tint = AppColor.OnConvex,
            contentDescription = "Delete",
        )
    }
}

@Preview
@Composable
private fun TagChipPreview() {
    TagChip(tag = "태그", onDelete = {})
}

@Preview
@Composable
private fun InfoEditDialogPreview() {
    InfoEditDialog(
        title = "title",
        tags = listOf("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7"),
        onDismiss = {},
        onConfirm = { _, _ -> },
    )
}
