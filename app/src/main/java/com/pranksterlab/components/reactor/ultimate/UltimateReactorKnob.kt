package com.pranksterlab.components.reactor.ultimate

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UltimateReactorKnob(
    title: String,
    label: String,
    value: Int,
    color: Color,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xE6050C19))
            .border(1.dp, Color(0xFF1A3050), RoundedCornerShape(6.dp))
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            title,
            color = Color(0xFF3A7AAA),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF243A5A), Color(0xFF071221))))
                .border(2.dp, color.copy(alpha = 0.65f), CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        val next = (value - dragAmount.y.toInt() + dragAmount.x.toInt() / 2).coerceIn(0, 100)
                        onValueChange(next)
                        change.consume()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.fillMaxSize().padding(5.dp)) {
                drawCircle(Color(0xFF101F35), radius = size.minDimension / 2f, style = Stroke(1.dp.toPx()))
                val deg = -135f + value * 2.7f
                rotate(deg, center) {
                    drawLine(
                        color,
                        Offset(center.x, center.y),
                        Offset(center.x, 5.dp.toPx()),
                        strokeWidth = 3.dp.toPx()
                    )
                }
                drawCircle(Color.Black.copy(alpha = 0.45f), radius = size.minDimension * 0.18f)
            }
        }
        Text(value.toString(), color = color, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Black))
        Text(label, color = Color(0xFF3A6A8A), style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 0.7.sp), maxLines = 1)
    }
}
