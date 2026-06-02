package com.pranksterlab.components.reactor.ultimate

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun UltimateReactorRadar(
    powered: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "ultimate-radar")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(if (powered) 2400 else 6000, easing = LinearEasing), RepeatMode.Restart),
        label = "radar-sweep"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF020A02))
            .border(1.dp, Color(0xFF0A2A1A), RoundedCornerShape(8.dp))
    ) {
        val c = center
        val r = min(size.width, size.height) * 0.42f
        drawCircle(Color(0xFF001A08), r, c)
        listOf(0.33f, 0.67f, 1f).forEach {
            drawCircle(Color(0x5500AA44), r * it, c, style = Stroke(1.dp.toPx()))
        }
        drawLine(Color(0x4400AA44), Offset(c.x - r, c.y), Offset(c.x + r, c.y), 1.dp.toPx())
        drawLine(Color(0x4400AA44), Offset(c.x, c.y - r), Offset(c.x, c.y + r), 1.dp.toPx())
        rotate(angle, c) {
            drawArc(
                brush = Brush.radialGradient(listOf(Color(0x7700FF66), Color.Transparent), c, r),
                startAngle = -100f,
                sweepAngle = 44f,
                useCenter = true,
                topLeft = Offset(c.x - r, c.y - r),
                size = androidx.compose.ui.geometry.Size(r * 2, r * 2)
            )
            drawLine(Color(0xEE66FF88), c, Offset(c.x, c.y - r), 2.dp.toPx())
        }
        val blips = listOf(Pair(0.8f, 0.62f), Pair(2.2f, 0.48f), Pair(4.65f, 0.78f))
        blips.forEachIndexed { index, blip ->
            val alpha = if (powered) 0.45f + 0.35f * ((angle / 60f + index) % 1f) else 0.12f
            drawCircle(
                Color(0xFF66FF00).copy(alpha = alpha),
                3.dp.toPx(),
                Offset(c.x + cos(blip.first) * blip.second * r, c.y + sin(blip.first) * blip.second * r)
            )
        }
    }
}
