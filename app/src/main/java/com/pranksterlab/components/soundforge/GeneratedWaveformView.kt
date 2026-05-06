package com.pranksterlab.components.soundforge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent

@Composable
fun GeneratedWaveformView(
    peaks: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = CyanAccent
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val barWidth = width / peaks.size.coerceAtLeast(1)
        val centerY = height / 2

        peaks.forEachIndexed { index, peak ->
            val x = index * barWidth + barWidth / 2
            val barHeight = (peak * height).coerceAtLeast(4f)
            
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(color.copy(alpha = 0.5f), color, color.copy(alpha = 0.5f))
                ),
                start = Offset(x, centerY - barHeight / 2),
                end = Offset(x, centerY + barHeight / 2),
                strokeWidth = (barWidth * 0.8f).coerceAtLeast(2f),
                cap = StrokeCap.Round
            )
        }
    }
}
