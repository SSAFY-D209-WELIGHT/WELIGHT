package com.rohkee.core.ui.component.common

import android.graphics.Bitmap
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor

@Composable
fun SaturationPanel(
    initialHue: Float,
    initialSaturation: Float,
    initialValue: Float,
    setSatVal: (sat: Float, value: Float) -> Unit,
) {
    val interactionSource =
        remember {
            MutableInteractionSource()
        }
    val scope = rememberCoroutineScope()
    var sat: Float
    var value: Float
    val (handleOffset, setHandleOffset) =
        remember(initialSaturation, initialValue) {
            mutableStateOf(Offset(initialSaturation, 1 - initialValue))
        }
    Canvas(
        modifier =
            Modifier
                .aspectRatio(1f)
                .emitDragGesture(interactionSource)
                .clip(RoundedCornerShape(12.dp)),
    ) {
        val cornerRadius = 12.dp.toPx()
        val satValSize = size
        val bitmap =
            Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(bitmap)
        val satValPanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val rgb = AndroidColor.HSVToColor(floatArrayOf(initialHue, 1f, 1f))
        val satShader =
            LinearGradient(
                satValPanel.left,
                satValPanel.top,
                satValPanel.right,
                satValPanel.top,
                -0x1,
                rgb,
                Shader.TileMode.CLAMP,
            )
        val valShader =
            LinearGradient(
                satValPanel.left,
                satValPanel.top,
                satValPanel.left,
                satValPanel.bottom,
                -0x1,
                -0x1000000,
                Shader.TileMode.CLAMP,
            )
        canvas.drawRoundRect(
            satValPanel,
            cornerRadius,
            cornerRadius,
            Paint().apply {
                shader =
                    ComposeShader(
                        valShader,
                        satShader,
                        PorterDuff.Mode.MULTIPLY,
                    )
            },
        )
        drawBitmap(
            bitmap = bitmap,
            panel = satValPanel,
        )

        fun pointToSatVal(
            pointX: Float,
            pointY: Float,
        ): Pair<Float, Float> {
            val width = satValPanel.width()
            val height = satValPanel.height()
            val x =
                when {
                    pointX < satValPanel.left -> 0f
                    pointX > satValPanel.right -> width
                    else -> pointX - satValPanel.left
                }
            val y =
                when {
                    pointY < satValPanel.top -> 0f
                    pointY > satValPanel.bottom -> height
                    else -> pointY - satValPanel.top
                }
            val satPoint = 1f / width * x
            val valuePoint = 1f - 1f / height * y
            return satPoint to valuePoint
        }
        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPositionOffset =
                Offset(
                    pressPosition.x.coerceIn(0f..satValSize.width),
                    pressPosition.y.coerceIn(0f..satValSize.height),
                )

//            setHandleOffset(
//                Offset(
//                    pressPositionOffset.x / satValSize.width,
//                    pressPositionOffset.y / satValSize.height,
//                ),
//            )
            val (satPoint, valuePoint) = pointToSatVal(pressPositionOffset.x, pressPositionOffset.y)
            sat = satPoint
            value = valuePoint
            setSatVal(sat, value)
        }
        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = Offset(handleOffset.x * satValSize.width, handleOffset.y * satValSize.height),
            style =
                Stroke(
                    width = 2.dp.toPx(),
                ),
        )
        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = Offset(handleOffset.x * satValSize.width, handleOffset.y * satValSize.height),
        )
    }
}

@Preview
@Composable
private fun SaturationPanelPreview() {
    SaturationPanel(0f, 0.5f, 0.5f, setSatVal = { _, _ -> })
}
