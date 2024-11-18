package com.rohkee.core.ui.component.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList

@Composable
fun ChipGroup(
    modifier: Modifier = Modifier,
    list: PersistentList<String>,
    selected: String,
    onChipSelected: (String) -> Unit,
) {
    Row(
        modifier = modifier.wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        for (item in list) {
            RoundedChip(
                isSelected = item == selected,
                text = item,
                onclick = { onChipSelected(item) },
            )
        }
    }
}
