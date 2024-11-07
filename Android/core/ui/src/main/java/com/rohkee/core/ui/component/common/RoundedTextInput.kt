package com.rohkee.core.ui.component.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun RoundedTextInput(
    modifier: Modifier = Modifier,
    autofocus: Boolean = false,
    value: String,
    hint: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    onValueChange: (String) -> Unit,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (autofocus) focusRequester.requestFocus()
    }

    OutlinedTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        shape = RoundedCornerShape(4.dp),
        textStyle = Pretendard.Medium20.copy(color = AppColor.OnSurface),
        placeholder = {
            Text(text = hint, style = Pretendard.Medium20)
        },
        supportingText = {
            if (isError) Text(text = errorMessage, style = Pretendard.SemiBold12)
        },
        trailingIcon = trailingContent,
        colors =
            OutlinedTextFieldDefaults.colors().copy(
                focusedContainerColor = AppColor.Surface,
                unfocusedContainerColor = AppColor.Surface,
                disabledContainerColor = AppColor.Surface,
                errorContainerColor = AppColor.Surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = AppColor.Warning,
                focusedPlaceholderColor = AppColor.Inactive,
                unfocusedPlaceholderColor = AppColor.Inactive,
                disabledPlaceholderColor = AppColor.Inactive,
                errorPlaceholderColor = AppColor.Inactive,
                focusedSupportingTextColor = AppColor.Warning,
                unfocusedSupportingTextColor = AppColor.Warning,
                disabledSupportingTextColor = AppColor.Warning,
                errorSupportingTextColor = AppColor.Warning,
                cursorColor = AppColor.OnSurface,
            ),
    )
}

@Preview
@Composable
private fun RoundedTextInputPreview() {
    Column {
        RoundedTextInput(value = "Value", hint = "hint", onValueChange = {})
        RoundedTextInput(value = "", hint = "hint", errorMessage = "error", onValueChange = {})
    }
}

@Preview
@Composable
private fun RoundedTextInputWarningPreview() {
    RoundedTextInput(
        value = "error",
        hint = "hint",
        isError = true,
        errorMessage = "잘못된 입력입니다.",
        onValueChange = {},
    )
}
