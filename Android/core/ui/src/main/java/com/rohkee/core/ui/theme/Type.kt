package com.rohkee.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rohkee.core.ui.R

internal val pretendardFamily = FontFamily(Font(R.font.pretendard))

object Pretendard {
    val SemiBold24 =
        TextStyle(
            fontSize = 24.sp,
            fontFamily = pretendardFamily,
            fontWeight = FontWeight.SemiBold,
        )

    val SemiBold20 =
        TextStyle(
            fontSize = 20.sp,
            fontFamily = pretendardFamily,
            fontWeight = FontWeight.SemiBold,
        )

    val SemiBold16 =
        TextStyle(
            fontSize = 16.sp,
            fontFamily = pretendardFamily,
            fontWeight = FontWeight.SemiBold,
        )

    val SemiBold12 =
        TextStyle(
            fontSize = 12.sp,
            fontFamily = pretendardFamily,
            fontWeight = FontWeight.SemiBold,
        )

    val SemiBold10 =
        TextStyle(
            fontSize = 10.sp,
            fontFamily = pretendardFamily,
            fontWeight = FontWeight.SemiBold,
        )

    val Medium24 =
        TextStyle(
            fontSize = 24.sp,
            fontFamily = pretendardFamily,
            fontWeight = FontWeight.Medium,
        )

    val Medium20 =
        TextStyle(
            fontSize = 20.sp,
            fontFamily = pretendardFamily,
            fontWeight = FontWeight.Medium,
        )

    val Medium16 =
        TextStyle(
            fontSize = 16.sp,
            fontFamily = pretendardFamily,
            fontWeight = FontWeight.Medium,
        )

    val Medium12 =
        TextStyle(
            fontSize = 12.sp,
            fontFamily = pretendardFamily,
            fontWeight = FontWeight.Medium,
        )

    val Regular14 =
        TextStyle(
            fontSize = 14.sp,
            fontFamily = pretendardFamily,
            fontWeight = FontWeight.Normal,
        )
}

val Typography =
    Typography(
        titleLarge = Pretendard.SemiBold24,
        bodyLarge = Pretendard.Medium16,
    )
