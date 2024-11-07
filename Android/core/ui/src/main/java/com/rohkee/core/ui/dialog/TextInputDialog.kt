package com.rohkee.core.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.component.appbar.ConfirmAppBar
import com.rohkee.core.ui.component.common.RoundedTextInput
import com.rohkee.core.ui.theme.AppColor

@Composable
fun TextInputDialog(
    modifier: Modifier = Modifier,
    initialValue: String = "",
    hint: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val (textState, setTextState) = remember(initialValue) { mutableStateOf(initialValue) }

    Column(
        modifier = modifier.fillMaxSize().background(color = AppColor.BackgroundTransparent).imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ConfirmAppBar(
            modifier = Modifier.fillMaxWidth(),
            onCloseClick = onDismiss,
            onConfirmClick = { onConfirm(textState) },
        )
        Spacer(modifier = Modifier.weight(1f))
        RoundedTextInput(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            autofocus = true,
            value = textState,
            hint = hint,
            isError = isError,
            errorMessage = errorMessage,
            onValueChange = setTextState,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
private fun TextInputDialogPreview() {
    TextInputDialog(
        hint = "text input",
        onDismiss = {},
        onConfirm = {},
    )
}
