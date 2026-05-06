package com.pranksterlab.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.GlassBackground
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrbitronFamily

@Composable
fun ScanlineOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val scanlineSpacing = 4.dp.toPx()
        val scanlineThickness = 1.dp.toPx()
        
        for (y in 0 until size.height.toInt() step scanlineSpacing.toInt()) {
            drawLine(
                color = Color.Black.copy(alpha = 0.15f),
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = scanlineThickness
            )
        }
    }
}

@Composable
fun HUDCard(
    modifier: Modifier = Modifier,
    accentColor: Color = LimeAccent,
    content: @Composable BoxScope.() -> Unit
) {
    val hudShape = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width - 20f, 0f)
        lineTo(size.width, 20f)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }

    Box(
        modifier = modifier
            .clip(hudShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF121212), Color(0xFF0A0A0A)),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(accentColor.copy(alpha = 0.3f), Color.Transparent)
                ),
                shape = hudShape
            )
    ) {
        // Left accent border
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
                .background(accentColor)
        )
        
        // Angular corner indicator at top right
        Canvas(modifier = Modifier.size(20.dp).align(androidx.compose.ui.Alignment.TopEnd)) {
            val path = Path().apply {
                moveTo(0f, 4f)
                lineTo(16f, 4f)
                lineTo(16f, 20f)
            }
            drawPath(
                path = path,
                color = accentColor,
                style = Stroke(width = 2.dp.toPx())
            )
        }
        
        content()
    }
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    borderColor: Color = Color.White.copy(alpha = 0.05f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassBackground)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        content = content
    )
}

@Composable
fun LabelCaps(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
    )
}

@Composable
fun HeadlineText(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = color,
        modifier = modifier
    )
}
