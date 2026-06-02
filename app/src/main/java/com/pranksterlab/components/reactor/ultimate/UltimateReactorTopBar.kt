package com.pranksterlab.components.reactor.ultimate

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UltimateReactorTopBar(
    state: UltimateReactorState,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "ultimate-topbar")
    val pulse by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(850), RepeatMode.Reverse),
        label = "online-dot"
    )
    val statusColor = if (state.powered) Color(0xFF00E8FF) else Color(0xFF44505C)
    val tempColor = when {
        state.tempCelsius >= 950 -> Color(0xFFFF2200)
        state.tempCelsius >= 880 -> Color(0xFFFF6600)
        else -> Color(0xFFFFCC00)
    }
    val batteryColor = when {
        state.batteryPercent > 60 -> Color(0xFF66FF00)
        state.batteryPercent > 30 -> Color(0xFFFFCC00)
        else -> Color(0xFFFF2200)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFA040C18))
            .border(1.dp, Color(0xFF102035))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (state.powered) Color(0xFF66FF00).copy(alpha = pulse) else Color(0xFF1F2A1F))
            )
            Column {
                TopLabel("REACTOR")
                TopValue(if (state.powered) "ONLINE" else "OFFLINE", statusColor)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TopLabel("PRANK*STAR", textAlign = TextAlign.Center)
            Text(
                "MISCHIEF AI v3.7",
                color = Color(0xFFFF00CC),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.End) {
                TopLabel("TEMP")
                TopValue("${state.tempCelsius}C", tempColor)
            }
            Spacer(Modifier.width(6.dp))
            Box(
                Modifier
                    .width(22.dp)
                    .height(11.dp)
                    .border(1.dp, Color(0xFF3A6A3A), RoundedCornerShape(2.dp))
                    .padding(1.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    Modifier
                        .fillMaxWidth((state.batteryPercent / 100f).coerceIn(0f, 1f))
                        .height(7.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(batteryColor)
                )
            }
        }
    }
}

@Composable
private fun TopLabel(text: String, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        color = Color(0xFF3A7AAA),
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.1.sp, fontWeight = FontWeight.Bold),
        textAlign = textAlign
    )
}

@Composable
private fun TopValue(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black)
    )
}
