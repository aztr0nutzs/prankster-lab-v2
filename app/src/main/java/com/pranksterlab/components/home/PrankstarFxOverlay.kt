package com.pranksterlab.components.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PrankstarFxOverlay(
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "home-fx")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(if (active) 900 else 2200), RepeatMode.Restart),
        label = "home-ripple"
    )

    Canvas(modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height * 0.48f)
        val base = size.minDimension * (0.18f + phase * 0.34f)
        val alpha = if (active) 0.42f * (1f - phase) else 0.14f * (1f - phase)
        repeat(3) { index ->
            drawCircle(
                color = listOf(Color(0xFF00E8FF), Color(0xFFFF00CC), Color(0xFF66FF00))[index].copy(alpha = alpha),
                radius = base + index * 28.dp.toPx(),
                center = center,
                style = Stroke(width = (1.2f + index).dp.toPx())
            )
        }
    }
}
