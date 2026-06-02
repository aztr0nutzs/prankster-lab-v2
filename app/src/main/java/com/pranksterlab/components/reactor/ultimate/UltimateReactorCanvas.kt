package com.pranksterlab.components.reactor.ultimate

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun UltimateReactorCanvas(
    state: UltimateReactorState,
    modifier: Modifier = Modifier,
    onTap: (Offset) -> Unit
) {
    val transition = rememberInfiniteTransition(label = "ultimate-reactor-canvas")
    val ringAngle by transition.animateFloat(
        0f,
        360f,
        infiniteRepeatable(tween(if (state.powered) 5200 else 16000, easing = LinearEasing), RepeatMode.Restart),
        label = "blue-ring"
    )
    val pinkAngle by transition.animateFloat(
        360f,
        0f,
        infiniteRepeatable(tween(if (state.powered) 3600 else 14000, easing = LinearEasing), RepeatMode.Restart),
        label = "pink-ring"
    )
    val knurlAngle by transition.animateFloat(
        0f,
        360f,
        infiniteRepeatable(tween(30000, easing = LinearEasing), RepeatMode.Restart),
        label = "knurl"
    )
    val wavePhase by transition.animateFloat(
        0f,
        (2f * PI).toFloat(),
        infiniteRepeatable(tween(if (state.powered) 1100 else 3800, easing = LinearEasing), RepeatMode.Restart),
        label = "core-wave"
    )
    val pulse by transition.animateFloat(
        0.65f,
        1f,
        infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "face-pulse"
    )
    val activeAlpha = if (state.powered) 1f else 0.32f
    val accent = state.mode.accentColor()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.95f)
            .pointerInput(state.powered) {
                detectTapGestures { offset -> onTap(offset) }
            }
    ) {
        val c = center
        val radius = min(size.width, size.height) * 0.43f
        val outer = radius
        val inner = radius * 0.45f

        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF2A2A2A), Color(0xFF181818), Color(0xFF070707)),
                c,
                outer
            ),
            radius = outer,
            center = c
        )
        drawCircle(Color(0xFF555555).copy(alpha = activeAlpha), outer, c, style = Stroke(2.dp.toPx()))

        rotate(knurlAngle, c) {
            repeat(80) { i ->
                val a = i / 80f * 2f * PI.toFloat()
                val r1 = outer * 0.94f
                val r2 = outer * 1.0f
                val color = when {
                    i % 4 == 0 -> Color(0xFF666666)
                    i % 2 == 0 -> Color(0xFF444444)
                    else -> Color(0xFF2A2A2A)
                }.copy(alpha = activeAlpha)
                drawLine(
                    color,
                    Offset(c.x + cos(a) * r1, c.y + sin(a) * r1),
                    Offset(c.x + cos(a) * r2, c.y + sin(a) * r2),
                    2.dp.toPx()
                )
            }
        }

        repeat(16) { i ->
            val a = i / 16f * 2f * PI.toFloat()
            val r1 = outer * 0.96f
            val r2 = outer * 1.02f
            drawLine(
                listOf(Color(0xFFFFCC00), Color(0xFFFF8800), Color(0xFFFF6600))[i % 3].copy(alpha = activeAlpha),
                Offset(c.x + cos(a) * r1, c.y + sin(a) * r1),
                Offset(c.x + cos(a) * r2, c.y + sin(a) * r2),
                5.dp.toPx()
            )
        }

        repeat(40) { i ->
            val a = i / 40f * 2f * PI.toFloat()
            val ledColor = listOf(Color.Red, Color(0xFFFF8800), Color(0xFFFFCC00), Color(0xFF00FF88), Color(0xFF00CCFF), Color(0xFFFF00CC))[i % 6]
            val blink = if (state.powered && i % 2 == 0) 0.35f + ((sin(wavePhase + i) + 1f) * 0.25f) else 0.08f
            drawCircle(
                ledColor.copy(alpha = blink),
                2.8.dp.toPx(),
                Offset(c.x + cos(a) * outer * 0.985f, c.y + sin(a) * outer * 0.985f)
            )
        }

        drawCircle(Color(0xFF003355).copy(alpha = activeAlpha), outer * 0.91f, c, style = Stroke(8.dp.toPx()))
        rotate(ringAngle, c) {
            drawArcOnCircle(c, outer * 0.91f, 8.dp.toPx(), 0f, 78f, Color(0xFF00AAFF).copy(alpha = activeAlpha))
            drawArcOnCircle(c, outer * 0.91f, 8.dp.toPx(), 135f, 52f, Color(0xFF00E8FF).copy(alpha = activeAlpha))
            drawArcOnCircle(c, outer * 0.91f, 8.dp.toPx(), 250f, 88f, accent.copy(alpha = activeAlpha))
        }

        drawTopConnector(c, outer, activeAlpha)
        drawBolts(c, outer * 0.82f, activeAlpha)

        drawCircle(Color(0xFF0C0C0C).copy(alpha = activeAlpha), outer * 0.73f, c)
        repeat(48) { i ->
            val a = i / 48f * 2f * PI.toFloat()
            val r1 = outer * 0.67f
            val r2 = outer * 0.73f
            drawLine(
                Color(0xFF303030).copy(alpha = activeAlpha),
                Offset(c.x + cos(a) * r1, c.y + sin(a) * r1),
                Offset(c.x + cos(a) * r2, c.y + sin(a) * r2),
                1.5.dp.toPx()
            )
        }

        drawSpeaker(c + Offset(-outer * 0.38f, -outer * 0.28f), outer * 0.07f, activeAlpha)
        drawSpeaker(c + Offset(-outer * 0.42f, outer * 0.28f), outer * 0.07f, activeAlpha)
        drawCog(c + Offset(outer * 0.38f, -outer * 0.29f), outer * 0.065f, ringAngle, activeAlpha)
        drawCog(c + Offset(outer * 0.42f, outer * 0.3f), outer * 0.055f, -ringAngle, activeAlpha)

        drawArcOnCircle(c, outer * 0.58f, 12.dp.toPx(), pinkAngle, 240f, Color(0xFFFF00CC).copy(alpha = activeAlpha * 0.82f))
        drawArcOnCircle(c, outer * 0.51f, 8.dp.toPx(), -ringAngle, 280f, Color(0xFF00E8FF).copy(alpha = activeAlpha * 0.9f))
        drawCircle(Color(0xFF040404), inner * 1.28f, c)
        drawCircle(Color(0x4400FFCC).copy(alpha = activeAlpha), inner * 1.42f, c)

        repeat(60) { i ->
            val a = i / 60f * 2f * PI.toFloat()
            val big = i % 5 == 0
            drawLine(
                if (big) Color(0xFF00AAFF).copy(alpha = activeAlpha) else Color(0xFF003355).copy(alpha = activeAlpha),
                Offset(c.x + cos(a) * inner * 1.43f, c.y + sin(a) * inner * 1.43f),
                Offset(c.x + cos(a) * inner * if (big) 1.30f else 1.36f, c.y + sin(a) * inner * if (big) 1.30f else 1.36f),
                if (big) 2.dp.toPx() else 1.dp.toPx()
            )
        }

        drawCoreWaveform(c, inner, wavePhase, activeAlpha)
        drawFace(c, inner, pulse, activeAlpha)
        drawNerdConsole(c, outer, wavePhase, activeAlpha)

        if (state.chargeLevel > 0f) {
            repeat(4) { i ->
                val rr = inner * (1.05f + state.chargeLevel * (0.55f + i * 0.18f))
                drawCircle(
                    listOf(Color(0xFF00FFFF), Color(0xFFFF00CC), Color(0xFFFFCC00), Color(0xFF66FF00))[i].copy(alpha = (1f - state.chargeLevel).coerceIn(0.05f, 0.7f)),
                    rr,
                    c,
                    style = Stroke((2 + i).dp.toPx())
                )
            }
        }
        if (state.isOverloaded) {
            drawCircle(Color(0x55FF2200), outer * 0.96f, c)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawArcOnCircle(center: Offset, radius: Float, width: Float, start: Float, sweep: Float, color: Color) {
    drawArc(
        color = color,
        startAngle = start,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2f, radius * 2f),
        style = Stroke(width = width)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTopConnector(c: Offset, outer: Float, alpha: Float) {
    drawRoundRect(
        Color(0xFF2A2A2A).copy(alpha = alpha),
        topLeft = Offset(c.x - 28.dp.toPx(), c.y - outer - 18.dp.toPx()),
        size = Size(56.dp.toPx(), 22.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
    )
    drawRoundRect(
        Color(0xFF333333).copy(alpha = alpha),
        topLeft = Offset(c.x - 16.dp.toPx(), c.y - outer - 26.dp.toPx()),
        size = Size(32.dp.toPx(), 9.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBolts(c: Offset, radius: Float, alpha: Float) {
    val angles = listOf(0.18f, -0.18f, PI.toFloat() - 0.18f, PI.toFloat() + 0.18f, -PI.toFloat() / 2f + 0.14f, -PI.toFloat() / 2f - 0.14f, PI.toFloat() / 2f + 0.12f, PI.toFloat() / 2f - 0.12f)
    angles.forEach { a ->
        val p = Offset(c.x + cos(a) * radius, c.y + sin(a) * radius)
        drawCircle(Color(0xFF3A3A3A).copy(alpha = alpha), 7.dp.toPx(), p)
        drawLine(Color(0xFF888888).copy(alpha = alpha), p + Offset(-4.dp.toPx(), -4.dp.toPx()), p + Offset(4.dp.toPx(), 4.dp.toPx()), 1.2.dp.toPx())
        drawLine(Color(0xFF888888).copy(alpha = alpha), p + Offset(4.dp.toPx(), -4.dp.toPx()), p + Offset(-4.dp.toPx(), 4.dp.toPx()), 1.2.dp.toPx())
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSpeaker(c: Offset, radius: Float, alpha: Float) {
    drawCircle(Color(0xFF151515).copy(alpha = alpha), radius, c)
    drawCircle(Color(0xFF3A3A3A).copy(alpha = alpha), radius, c, style = Stroke(2.dp.toPx()))
    repeat(4) { ring -> drawCircle(Color(0xFF444444).copy(alpha = alpha * 0.5f), radius * (0.25f + ring * 0.17f), c, style = Stroke(0.8.dp.toPx())) }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCog(c: Offset, radius: Float, angle: Float, alpha: Float) {
    rotate(angle, c) {
        repeat(12) { i ->
            val a = i / 12f * 360f
            rotate(a, c) {
                drawRoundRect(
                    Color(0xFF2A2A2A).copy(alpha = alpha),
                    Offset(c.x - 2.dp.toPx(), c.y - radius - 5.dp.toPx()),
                    Size(4.dp.toPx(), 10.dp.toPx()),
                    androidx.compose.ui.geometry.CornerRadius(1.dp.toPx())
                )
            }
        }
        drawCircle(Color(0xFF171717).copy(alpha = alpha), radius, c)
        drawCircle(Color(0xFF3A3A3A).copy(alpha = alpha), radius * 0.45f, c, style = Stroke(2.dp.toPx()))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCoreWaveform(c: Offset, inner: Float, phase: Float, alpha: Float) {
    val points = mutableListOf<Offset>()
    val start = c.x - inner * 1.05f
    val end = c.x + inner * 1.05f
    var x = start
    while (x <= end) {
        val rel = (x - c.x) / inner
        val y = c.y - inner * 0.18f + sin(rel * 5.5f + phase) * inner * 0.06f + sin(rel * 12f + phase * 1.8f) * inner * 0.025f
        points.add(Offset(x, y))
        x += 4.dp.toPx()
    }
    points.zipWithNext().forEach { (a, b) -> drawLine(Color(0xFF00FFCC).copy(alpha = alpha * 0.55f), a, b, 1.4.dp.toPx()) }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFace(c: Offset, inner: Float, pulse: Float, alpha: Float) {
    drawCircle(
        Brush.radialGradient(listOf(Color(0xFF99FF22).copy(alpha = alpha * pulse), Color(0xFF55CC00).copy(alpha = alpha), Color(0xFF1A6600).copy(alpha = alpha)), c, inner * 0.88f),
        inner * 0.83f,
        c
    )
    val eyeY = c.y - inner * 0.2f
    drawCircle(Color.White.copy(alpha = alpha), inner * 0.12f, Offset(c.x - inner * 0.22f, eyeY))
    drawCircle(Color.White.copy(alpha = alpha), inner * 0.12f, Offset(c.x + inner * 0.22f, eyeY))
    drawCircle(Color.Black.copy(alpha = alpha), inner * 0.045f, Offset(c.x - inner * 0.2f, eyeY + inner * 0.015f))
    drawCircle(Color.Black.copy(alpha = alpha), inner * 0.045f, Offset(c.x + inner * 0.24f, eyeY + inner * 0.015f))
    drawArc(
        Color.Black.copy(alpha = alpha),
        startAngle = 18f,
        sweepAngle = 144f,
        useCenter = false,
        topLeft = Offset(c.x - inner * 0.35f, c.y - inner * 0.02f),
        size = Size(inner * 0.7f, inner * 0.5f),
        style = Stroke(4.dp.toPx())
    )
    drawNativeText("PRANKSTAR", c.x, c.y + inner * 0.52f, 10.dp.toPx(), Color.Black.copy(alpha = alpha))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawNerdConsole(c: Offset, outer: Float, phase: Float, alpha: Float) {
    val panelTop = c.y + outer * 0.47f
    drawRoundRect(
        Color.Black.copy(alpha = alpha * 0.68f),
        Offset(c.x - outer * 0.34f, panelTop),
        Size(outer * 0.68f, outer * 0.17f),
        androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
    )
    drawNativeText("NERD CONSOLE", c.x, panelTop + 10.dp.toPx(), 8.dp.toPx(), Color(0xFF00E8FF).copy(alpha = alpha))
    repeat(7) { i ->
        val h = (sin(phase + i * 0.8f) + 1f) * 7.dp.toPx() + 4.dp.toPx()
        drawRoundRect(
            if (i % 2 == 0) Color(0xFF66FF00).copy(alpha = alpha) else Color(0xFF00E8FF).copy(alpha = alpha),
            Offset(c.x - 38.dp.toPx() + i * 12.dp.toPx(), panelTop + 31.dp.toPx() - h),
            Size(6.dp.toPx(), h),
            androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
        )
    }
    drawNativeText("AI", c.x + outer * 0.22f, panelTop + 35.dp.toPx(), 9.dp.toPx(), Color(0xFFFF00CC).copy(alpha = alpha))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawNativeText(text: String, x: Float, y: Float, sizePx: Float, color: Color) {
    drawContext.canvas.nativeCanvas.drawText(
        text,
        x,
        y,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            textSize = sizePx
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            this.color = android.graphics.Color.argb((color.alpha * 255).toInt(), (color.red * 255).toInt(), (color.green * 255).toInt(), (color.blue * 255).toInt())
        }
    )
}
