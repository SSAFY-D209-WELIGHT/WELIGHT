package com.rohkee.core.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rohkee.core.ui.component.common.HueBar
import com.rohkee.core.ui.component.common.SaturationPanel
import com.rohkee.core.ui.model.ColorType
import com.rohkee.core.ui.model.CustomColor
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard
import android.graphics.Color as AndroidColor

private enum class ColorSelectionType(
    val text: String,
) {
    SINGLE("단색"),
    GRADIENT("그라데이션"),
}

private enum class OrderType {
    PRIMARY,
    SECONDARY,
}

@Composable
fun ColorPickerDialog(
    color: CustomColor? = null,
    gradientEnabled: Boolean = true,
    onConfirm: (CustomColor) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val config = LocalConfiguration.current
    val widthDp = remember { (config.screenWidthDp * 0.7).dp }

    //val (openDropdown, setOpenDropdown) = remember { mutableStateOf(false) }
    val (selectedType, setSelectedType) =
        remember {
            mutableStateOf(
                when (gradientEnabled) {
                    true -> ColorSelectionType.GRADIENT
                    false -> ColorSelectionType.SINGLE
                },
            )
        }
    val (selectedOrder, setSelectedOrder) = remember { mutableStateOf(OrderType.PRIMARY) }
    val (gradientType, setGradientType) = remember { mutableStateOf(ColorType.Horizontal) }

    val (primaryHsv, setPrimaryHsv) =
        remember(color) {
            val hsv = floatArrayOf(0f, 0f, 0f)
            when (color) {
                is CustomColor.Gradient -> {
                    AndroidColor.colorToHSV(color.colors[0].toArgb(), hsv)
                }

                is CustomColor.Single -> {
                    AndroidColor.colorToHSV(color.color.toArgb(), hsv)
                }

                else -> AndroidColor.colorToHSV(Color.Red.toArgb(), hsv)
            }
            mutableStateOf(
                Triple(hsv[0], hsv[1], hsv[2]),
            )
        }
    val primaryColor by
        remember(primaryHsv) {
            mutableStateOf(Color.hsv(primaryHsv.first, primaryHsv.second, primaryHsv.third))
        }

    val (secondaryHsv, setSecondaryHsv) =
        remember(color) {
            val hsv = floatArrayOf(0f, 0f, 0f)
            when (color) {
                is CustomColor.Gradient -> {
                    AndroidColor.colorToHSV(color.colors[1].toArgb(), hsv)
                }

                is CustomColor.Single -> {
                    AndroidColor.colorToHSV(Color.Black.toArgb(), hsv)
                }

                else -> AndroidColor.colorToHSV(Color.Black.toArgb(), hsv)
            }
            mutableStateOf(
                Triple(hsv[0], hsv[1], hsv[2]),
            )
        }
    val secondaryColor by
        remember(secondaryHsv) {
            mutableStateOf(Color.hsv(secondaryHsv.first, secondaryHsv.second, secondaryHsv.third))
        }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier =
                Modifier
                    .width(widthDp)
                    .background(color = AppColor.Surface, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            modifier = Modifier.clickable { setSelectedType(ColorSelectionType.SINGLE) },
                            text = ColorSelectionType.SINGLE.text,
                            style = Pretendard.SemiBold20,
                            color = if (selectedType == ColorSelectionType.SINGLE) AppColor.Active else AppColor.Inactive,
                        )
                        if (gradientEnabled) {
                            Text(
                                modifier = Modifier.clickable { setSelectedType(ColorSelectionType.GRADIENT) },
                                text = ColorSelectionType.GRADIENT.text,
                                style = Pretendard.SemiBold20,
                                color = if (selectedType == ColorSelectionType.GRADIENT) AppColor.Active else AppColor.Inactive,
                            )
                        }
                    }
//                    DropdownMenu(
//                        modifier = Modifier.background(color = AppColor.OverSurface),
//                        expanded = openDropdown,
//                        onDismissRequest = { setOpenDropdown(false) },
//                    ) {
//                        DropdownMenuItem(
//                            text = { Text(text = "색상 선택", style = Pretendard.SemiBold16) },
//                            onClick = {
//                                setSelectedType(ColorSelectionType.SINGLE)
//                                setSelectedOrder(OrderType.PRIMARY)
//                                setOpenDropdown(false)
//                            },
//                        )
//                        DropdownMenuItem(
//                            text = { Text(text = "그라데이션 선택", style = Pretendard.SemiBold16) },
//                            onClick = {
//                                setSelectedType(ColorSelectionType.GRADIENT)
//                                setSelectedOrder(OrderType.PRIMARY)
//                                setOpenDropdown(false)
//                            },
//                        )
//                    }
                }
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "close dialog",
                    modifier = Modifier.clickable { onDismiss() },
                    tint = AppColor.OnSurface,
                )
            }
            when (selectedType) {
                ColorSelectionType.SINGLE -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(
                                    color = primaryColor,
                                    shape = RoundedCornerShape(4.dp),
                                ),
                    )
                }

                ColorSelectionType.GRADIENT -> {
                    GradientTypeSelector(
                        modifier = Modifier.fillMaxWidth(),
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        selectedType = gradientType,
                        setSelectedType = { setGradientType(it) },
                    )
                    GradientColorControls(
                        modifier = Modifier.fillMaxWidth(),
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        selectedOrder = selectedOrder,
                        setSelectedOrder = { setSelectedOrder(it) },
                    )
                }
            }
            when (selectedType) {
                ColorSelectionType.SINGLE -> {
                    ColorControls(
                        hsv = primaryHsv,
                        setHsv = { hsv -> setPrimaryHsv(hsv) },
                    )
                }

                ColorSelectionType.GRADIENT -> {
                    when (selectedOrder) {
                        OrderType.PRIMARY -> {
                            ColorControls(
                                hsv = primaryHsv,
                                setHsv = { hsv -> setPrimaryHsv(hsv) },
                            )
                        }

                        OrderType.SECONDARY -> {
                            ColorControls(
                                hsv = secondaryHsv,
                                setHsv = { hsv -> setSecondaryHsv(hsv) },
                            )
                        }
                    }
                }
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color = AppColor.Convex,
                            shape = RoundedCornerShape(8.dp),
                        ).padding(8.dp)
                        .clickable {
                            onConfirm(
                                if (selectedType == ColorSelectionType.SINGLE) {
                                    CustomColor.Single(color = primaryColor)
                                } else {
                                    CustomColor.Gradient(
                                        colors = listOf(primaryColor, secondaryColor),
                                        type = gradientType,
                                    )
                                },
                            )
                        },
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "확인",
                    style = Pretendard.SemiBold16,
                    color = AppColor.OnConvex,
                )
            }
        }
    }
}

@Composable
private fun GradientTypeSelector(
    modifier: Modifier,
    primaryColor: Color,
    secondaryColor: Color,
    selectedType: ColorType,
    setSelectedType: (ColorType) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "방향 지정", style = Pretendard.SemiBold20, color = AppColor.OnSurface)
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(primaryColor, secondaryColor)),
                        shape = RoundedCornerShape(4.dp),
                    ).then(
                        if (selectedType == ColorType.Horizontal) {
                            Modifier.border(
                                width = 2.dp,
                                color = AppColor.Active,
                                shape = RoundedCornerShape(4.dp),
                            )
                        } else {
                            Modifier
                        },
                    ).clickable { setSelectedType(ColorType.Horizontal) },
        )
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(primaryColor, secondaryColor)),
                        shape = RoundedCornerShape(4.dp),
                    ).then(
                        if (selectedType == ColorType.Vertical) {
                            Modifier.border(
                                width = 2.dp,
                                color = AppColor.Active,
                                shape = RoundedCornerShape(4.dp),
                            )
                        } else {
                            Modifier
                        },
                    ).clickable { setSelectedType(ColorType.Vertical) },
        )
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .background(
                        brush = Brush.radialGradient(listOf(primaryColor, secondaryColor)),
                        shape = RoundedCornerShape(4.dp),
                    ).then(
                        if (selectedType == ColorType.Radial) {
                            Modifier.border(
                                width = 2.dp,
                                color = AppColor.Active,
                                shape = RoundedCornerShape(4.dp),
                            )
                        } else {
                            Modifier
                        },
                    ).clickable { setSelectedType(ColorType.Radial) },
        )
    }
}

@Composable
private fun GradientColorControls(
    modifier: Modifier,
    primaryColor: Color,
    secondaryColor: Color,
    selectedOrder: OrderType,
    setSelectedOrder: (OrderType) -> Unit,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .background(
                        color = primaryColor,
                        shape = RoundedCornerShape(8.dp),
                    ).then(
                        if (selectedOrder == OrderType.PRIMARY) {
                            Modifier.border(
                                color = AppColor.Active,
                                width = 3.dp,
                                shape = RoundedCornerShape(8.dp),
                            )
                        } else {
                            Modifier
                        },
                    ).clickable {
                        setSelectedOrder(OrderType.PRIMARY)
                    },
        )
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(32.dp)
                    .padding(horizontal = 4.dp)
                    .background(
                        brush =
                            Brush.horizontalGradient(
                                listOf(primaryColor, secondaryColor),
                            ),
                        shape = RoundedCornerShape(8.dp),
                    ),
        )
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .background(
                        color = secondaryColor,
                        shape = RoundedCornerShape(8.dp),
                    ).then(
                        if (selectedOrder == OrderType.SECONDARY) {
                            Modifier.border(
                                color = AppColor.Active,
                                width = 3.dp,
                                shape = RoundedCornerShape(8.dp),
                            )
                        } else {
                            Modifier
                        },
                    ).clickable {
                        setSelectedOrder(OrderType.SECONDARY)
                    },
        )
    }
}

@Composable
private fun ColorControls(
    hsv: Triple<Float, Float, Float>,
    setHsv: (Triple<Float, Float, Float>) -> Unit,
) {
    SaturationPanel(
        initialHue = hsv.first,
        initialSaturation = hsv.second,
        initialValue = hsv.third,
        setSatVal = { sat, value ->
            setHsv(Triple(hsv.first, sat, value))
        },
    )
    HueBar(
        initialHue = hsv.first,
        setColor = { hue ->
            setHsv(
                Triple(
                    hue,
                    hsv.second,
                    hsv.third,
                ),
            )
        },
    )
}

@Preview
@Composable
private fun GradientTypeSelectorPreview() {
    GradientTypeSelector(
        modifier = Modifier.fillMaxWidth(),
        primaryColor = Color.Red,
        secondaryColor = Color.Blue,
        selectedType = ColorType.Horizontal,
        setSelectedType = {},
    )
}

@Preview
@Composable
private fun GradientColorControlsPreview() {
    GradientColorControls(
        modifier = Modifier.fillMaxWidth(),
        primaryColor = Color.Red,
        secondaryColor = Color.Blue,
        selectedOrder = OrderType.PRIMARY,
        setSelectedOrder = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun ColorPickerDialogPreview() {
    ColorPickerDialog(
        color = CustomColor.Single(color = Color.Blue),
        gradientEnabled = false,
    )
}
