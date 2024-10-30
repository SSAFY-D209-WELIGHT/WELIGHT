package com.rohkee.core.ui.screen.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.Pretendard

/**
 * 보관함 화면
 */
@Composable
fun StorageContent(
    modifier: Modifier = Modifier,
    state: StorageState,
    onIntent: (StorageIntent) -> Unit = {},
) {
    Column(modifier = modifier) {
        when (state) {
            is StorageState.Loading -> {
            }

            is StorageState.Loaded -> {
            }

            is StorageState.NoData -> {
            }

            is StorageState.Error -> {
            }
        }
    }
}

@Composable
fun CreateDisplayButton(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = Color.Gray,
                    shape = RoundedCornerShape(16.dp),
                ),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "추가",
                textAlign = TextAlign.Center,
                style = Pretendard.Medium20
            )
        }
    }
}

@Preview
@Composable
fun CreateDisplayButtonPreview() {
    CreateDisplayButton()
}
