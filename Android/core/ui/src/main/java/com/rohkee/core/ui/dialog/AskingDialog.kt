package com.rohkee.core.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rohkee.core.ui.component.common.OutlinedButton
import com.rohkee.core.ui.component.common.PlainButton
import com.rohkee.core.ui.component.common.WarningButton
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun WarningDialog(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    icon: ImageVector = Icons.Default.Warning,
    confirmText: String = "확인",
    cancelText: String = "취소",
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit,
) {
    PlainDialog(
        modifier = modifier,
        title = title,
        content = content,
        onDismiss = onDismiss,
    ) {
        OutlinedButton(modifier = Modifier.weight(1f), text = cancelText, onClick = onDismiss)
        WarningButton(
            modifier = Modifier.weight(1f),
            text = confirmText,
            icon = icon,
            onClick = onConfirm,
        )
    }
}

@Composable
fun AskingDialog(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    confirmText: String = "확인",
    cancelText: String = "취소",
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit,
) {
    PlainDialog(
        modifier = modifier,
        title = title,
        content = content,
        onDismiss = onDismiss,
    ) {
        OutlinedButton(modifier = Modifier.weight(1f), text = cancelText, onClick = onDismiss)
        PlainButton(modifier = Modifier.weight(1f), text = confirmText, onClick = onConfirm)
    }
}

@Composable
fun PlainDialog(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    onDismiss: () -> Unit,
    buttonContent: @Composable (RowScope.() -> Unit),
) {
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier =
                modifier
                    .sizeIn(maxWidth = width * 0.7f)
                    .background(
                        color = AppColor.Surface,
                        shape = RoundedCornerShape(8.dp),
                    ).padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (title.isNotEmpty()) {
                Text(
                    modifier = Modifier,
                    text = title,
                    style = Pretendard.Medium24,
                    color = AppColor.OnSurface,
                )
            }
            if (content.isNotEmpty()) {
                Text(
                    modifier = Modifier,
                    text = content,
                    style = Pretendard.Medium16,
                    color = AppColor.OnSurface,
                )
            }
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = buttonContent,
            )
        }
    }
}

@Preview
@Composable
private fun AskingDialogPreview() {
    AskingDialog(
        title = "테스트",
        content = "테스트",
        onConfirm = {},
        onDismiss = {},
    )
}

@Preview
@Composable
private fun WarningDialogPreview() {
    WarningDialog(
        title = "테스트",
        content = "테스트",
        onConfirm = {},
        onDismiss = {},
    )
}
