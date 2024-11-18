package com.rohkee.core.ui.component.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.theme.AppColor

@Composable
fun CommonSnackbar(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
) {
    Box(modifier = modifier.fillMaxSize().imePadding(), contentAlignment = Alignment.BottomCenter) {
        SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = AppColor.Convex,
                contentColor = AppColor.OnConvex,
                modifier = Modifier.padding(top = 16.dp).padding(16.dp),
            )
        }
    }
}