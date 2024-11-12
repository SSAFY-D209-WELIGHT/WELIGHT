package com.rohkee.feature.group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rohkee.core.ui.component.appbar.TitleAppBar

@Composable
fun GroupContent(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = { TitleAppBar(title = "단체 응원") },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {

        }
    }
}
