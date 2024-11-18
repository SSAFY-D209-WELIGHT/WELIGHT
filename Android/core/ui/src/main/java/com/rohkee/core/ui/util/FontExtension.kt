package com.rohkee.core.ui.util

import androidx.compose.ui.text.font.FontFamily
import com.rohkee.core.ui.theme.pretendardFamily

enum class AppFont(
    val fontFamily: FontFamily,
) {
    SansSerif(FontFamily.SansSerif),
    Serif(FontFamily.Serif),
    Monospace(FontFamily.Monospace),
    Pretendard(pretendardFamily),
}

fun String.toFontFamily(): FontFamily =
    when (this) {
        AppFont.SansSerif.name -> AppFont.SansSerif.fontFamily
        AppFont.Serif.name -> AppFont.Serif.fontFamily
        AppFont.Monospace.name -> AppFont.Monospace.fontFamily
        AppFont.Pretendard.name -> AppFont.Pretendard.fontFamily
        else -> pretendardFamily
    }

fun FontFamily.toFontName(): String =
    when (this){
        AppFont.SansSerif.fontFamily -> AppFont.SansSerif.name
        AppFont.Serif.fontFamily -> AppFont.Serif.name
        AppFont.Monospace.fontFamily -> AppFont.Monospace.name
        AppFont.Pretendard.fontFamily -> AppFont.Pretendard.name
        else -> AppFont.Pretendard.name
    }
