package com.rohkee.core.ui.component.common

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
class CustomColor(
    val colors: PersistentList<Color>,
) {
    val primary: Color = if (colors.isEmpty()) Color.Unspecified else colors[0]

    constructor(color: Color) : this(persistentListOf(color))
}
