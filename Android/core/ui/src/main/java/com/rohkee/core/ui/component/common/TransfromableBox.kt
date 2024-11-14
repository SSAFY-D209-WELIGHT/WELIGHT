package com.rohkee.core.ui.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 스케일, 회전, 이동이 가능한 Box
 * 선택이 된 이후에 이동 및 변경 가능
 *
 * @param scale : 스케일 ( 1.0f = 100% 크기 )
 * @param rotation : 회전 각도 ( -360 ~ 360 )
 * @param offset : 화면 크기 비례 이동량 ( 0.0(화면 중앙) ~ 0.5f(화면끝) )
 */
@Composable
fun TransformableBox(
    modifier: Modifier = Modifier,
    scale: Float,
    rotation: Float,
    offset: Offset,
    onTransfrm: (scale: Float, rotation: Float, offset: Offset) -> Unit = { _, _, _ -> },
    onTap: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
    ) {
        val density = LocalDensity.current
        val width = with(density) { maxWidth.toPx() }
        val height = with(density) { maxHeight.toPx() }

        val state =
            rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                onTransfrm.invoke(
                    scale * zoomChange,
                    rotation + rotationChange,
                    offset +
                        Offset(
                            x = offsetChange.x / width,
                            y = offsetChange.y / height,
                        ),
                )
            }

        Box(
            modifier
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { onTap() },
                    )
                }.graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = rotation,
                    translationX = (offset.x * width),
                    translationY = (offset.y * height),
                ).transformable(state = state),
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun TransformableBoxPreview() {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var selected by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TransformableBox(
            scale = scale,
            rotation = rotation,
            offset = offset,
            onTransfrm = { s, r, o ->
                scale = s
                rotation = r
                offset = o
            },
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center).background(color = Color.Cyan),
                text = "$scale $rotation $offset",
                fontSize = 40.sp,
            )
        }
    }
}
