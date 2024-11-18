package com.rohkee.core.ui.component.common

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toRect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor

@Composable
fun HueBar(
    initialHue: Float,
    setColor: (hue: Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    val interactionSource =
        remember {
            MutableInteractionSource()
        }
    val (handleOffset, setHandleOffset) =
        remember(initialHue) {
            mutableFloatStateOf(initialHue / 360f)
        }
    Canvas(
        modifier =
            Modifier
                .height(40.dp)
                .width(300.dp)
                .clip(RoundedCornerShape(50))
                .emitDragGesture(interactionSource),
    ) {
        val drawScopeSize = size
        val bitmap =
            Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val hueCanvas = AndroidCanvas(bitmap)
        val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val hueColors = IntArray((huePanel.width()).toInt())
        var hue = 0f
        for (i in hueColors.indices) {
            hueColors[i] = AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))
            hue += 360f / hueColors.size
        }
        val linePaint = Paint()
        linePaint.strokeWidth = 0F
        for (i in hueColors.indices) {
            linePaint.color = hueColors[i]
            hueCanvas.drawLine(i.toFloat(), 0F, i.toFloat(), huePanel.bottom, linePaint)
        }
        drawBitmap(
            bitmap = bitmap,
            panel = huePanel,
        )

        fun pointToHue(pointX: Float): Float {
            val width = huePanel.width()
            val x =
                when {
                    pointX < huePanel.left -> 0F
                    pointX > huePanel.right -> width
                    else -> pointX - huePanel.left
                }
            return x * 360f / width
        }

        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPos = pressPosition.x.coerceIn(0f..drawScopeSize.width)
            //setHandleOffset(pressPos / drawScopeSize.width)
            val selectedHue = pointToHue(pressPos)
            setColor(selectedHue)
        }

        drawCircle(
            Color.White,
            radius = size.height / 2,
            center = Offset(handleOffset * drawScopeSize.width, size.height / 2),
            style =
                Stroke(
                    width = 2.dp.toPx(),
                ),
        )
    }
}

fun CoroutineScope.collectForPress(
    interactionSource: InteractionSource,
    setOffset: (Offset) -> Unit,
) {
    launch {
        interactionSource.interactions.collect { interaction ->
            (interaction as? PressInteraction.Press)
                ?.pressPosition
                ?.let(setOffset)
        }
    }
}

internal fun Modifier.emitDragGesture(interactionSource: MutableInteractionSource): Modifier =
    composed {
        val scope = rememberCoroutineScope()
        pointerInput(Unit) {
            detectDragGestures { input, _ ->
                scope.launch {
                    interactionSource.emit(PressInteraction.Press(input.position))
                }
            }
        }.clickable(interactionSource, null) {
        }
    }

internal fun DrawScope.drawBitmap(
    bitmap: Bitmap,
    panel: RectF,
) {
    drawIntoCanvas {
        it.nativeCanvas.drawBitmap(
            bitmap,
            null,
            panel.toRect(),
            null,
        )
    }
}

@Preview
@Composable
private fun HueBarPreview() {
    var hue by remember { mutableFloatStateOf(0f) }

    HueBar(
        initialHue = hue,
        setColor = { hue = it },
    )
}
