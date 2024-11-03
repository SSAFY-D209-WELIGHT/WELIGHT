package com.rohkee.core.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.rohkee.core.ui.model.CustomColor
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard
import android.graphics.Color as AndroidColor

private enum class SelectionType(
    val text: String,
) {
    SINGLE("색상 선택"),
    GRADIENT("그라데이션 선택"),
}

private enum class OrderType {
    PRIMARY,
    SECONDARY,
}

@Composable
fun ColorPickerDialog(color: CustomColor? = null) {
    val config = LocalConfiguration.current
    val widthDp = remember { (config.screenWidthDp * 0.7).dp }

    val (openDropdown, setOpenDropdown) = remember { mutableStateOf(false) }
    val (selectedType, setSelectedType) =
        remember {
            mutableStateOf(
                when (color) {
                    is CustomColor.Gradient -> SelectionType.GRADIENT
                    else -> SelectionType.SINGLE
                }
            )
        }

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
                    AndroidColor.colorToHSV(color.color.toArgb(), hsv)
                }

                else -> AndroidColor.colorToHSV(Color.Red.toArgb(), hsv)
            }
            mutableStateOf(
                Triple(hsv[0], hsv[1], hsv[2]),
            )
        }
    val secondaryColor by
        remember(secondaryHsv) {
            mutableStateOf(Color.hsv(secondaryHsv.first, secondaryHsv.second, secondaryHsv.third))
        }

    val (selectedOrder, setSelectedOrder) = remember { mutableStateOf(OrderType.PRIMARY) }

    val selectedHsv =
        remember(primaryHsv, secondaryHsv, selectedType, selectedOrder) {
            if (selectedType == SelectionType.GRADIENT && selectedOrder == OrderType.SECONDARY) {
                secondaryHsv
            } else {
                primaryHsv
            }
        }

    Dialog(
        onDismissRequest = {},
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
                    ) {
                        Text(
                            text = selectedType.text,
                            style = Pretendard.SemiBold20,
                            color = AppColor.OnSurface,
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "open dropdown",
                            modifier = Modifier.clickable { setOpenDropdown(true) },
                            tint = AppColor.OnSurface,
                        )
                    }
                    DropdownMenu(
                        expanded = openDropdown,
                        onDismissRequest = { setOpenDropdown(false) },
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "색상 선택") },
                            onClick = {
                                setSelectedType(SelectionType.SINGLE)
                                setSelectedOrder(OrderType.PRIMARY)
                                setOpenDropdown(false)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(text = "그라데이션 선택") },
                            onClick = {
                                setSelectedType(SelectionType.GRADIENT)
                                setSelectedOrder(OrderType.PRIMARY)
                                setOpenDropdown(false)
                            },
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "close dialog",
                    modifier = Modifier.clickable { setOpenDropdown(true) },
                    tint = AppColor.OnSurface,
                )
            }
            when (selectedType) {
                SelectionType.SINGLE -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(
                                    color = primaryColor,
                                    shape = RoundedCornerShape(4.dp),
                                ),
                    ) { }
                }

                SelectionType.GRADIENT -> {
                    Row(modifier = Modifier.fillMaxWidth()) {
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
                        ) { }
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
                        ) { }
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
                        ) { }
                    }
                }
            }

            SaturationPanel(
                initialHue = selectedHsv.first,
                initialSaturation = selectedHsv.second,
                initialValue = selectedHsv.third,
                setSatVal = { sat, value ->
                    if (selectedType == SelectionType.GRADIENT && selectedOrder == OrderType.SECONDARY) {
                        setSecondaryHsv(Triple(secondaryHsv.first, sat, value))
                    } else {
                        setPrimaryHsv(
                            Triple(primaryHsv.first, sat, value),
                        )
                    }
                },
            )
            HueBar(
                initialHue = selectedHsv.first,
                setColor = { hue ->
                    if (selectedType == SelectionType.GRADIENT && selectedOrder == OrderType.SECONDARY) {
                        setSecondaryHsv(Triple(hue, secondaryHsv.second, secondaryHsv.third))
                    } else {
                        setPrimaryHsv(
                            Triple(hue, primaryHsv.second, primaryHsv.third),
                        )
                    }
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorPickerDialogPreview() {
    ColorPickerDialog(
        color = CustomColor.Single(color = Color.Blue)
    )
}
