package com.pranksterlab.components.reactor

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.theme.*

data class TraceEntry(val title: String, val time: String, val accent: Color = CyanAccent)

/**
 * Container that frames the entire control deck with HUD-style brackets and a subtle gunmetal panel.
 */
@Composable
fun NeonControlPanel(
    modifier: Modifier = Modifier,
    accent: Color = CyanAccent,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "panel_glow")
    val borderAlpha by transition.animateFloat(
        0.18f, 0.45f,
        infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
        label = "panel_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A0F12),
                        Color(0xFF06090B)
                    )
                ),
                RoundedCornerShape(18.dp)
            )
            .border(1.dp, accent.copy(alpha = borderAlpha), RoundedCornerShape(18.dp))
            .padding(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (title != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = accent
                    )
                    Spacer(Modifier.weight(1f))
                    Canvas(
                        modifier = Modifier
                            .height(2.dp)
                            .width(70.dp)
                    ) {
                        drawLine(
                            color = accent.copy(alpha = 0.5f),
                            start = Offset(0f, size.height / 2f),
                            end = Offset(size.width, size.height / 2f),
                            strokeWidth = size.height
                        )
                    }
                }
            }
            content()
        }
    }
}

/**
 * Reusable HUD button with neon border, optional pulse, and an icon + label.
 */
@Composable
fun ControlPanelButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    accent: Color = CyanAccent,
    enabled: Boolean = true,
    pulsing: Boolean = false,
    onClick: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "btn")
    val pulse by transition.animateFloat(
        0.65f, 1f,
        infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "btn_pulse"
    )
    val borderAlpha = if (pulsing) pulse else 0.55f
    val effectiveAccent = if (enabled) accent else accent.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.55f))
            .border(1.5.dp, effectiveAccent.copy(alpha = borderAlpha), RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = effectiveAccent,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = effectiveAccent.copy(alpha = if (enabled) 0.85f else 0.4f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Live status readout showing loaded/safe/playing/category/safe-mode telemetry.
 */
@Composable
fun StatusReadout(
    loadedCount: Int,
    safeCount: Int,
    currentSound: String?,
    currentCategory: String,
    isPlaying: Boolean,
    safeMode: Boolean,
    modifier: Modifier = Modifier
) {
    NeonControlPanel(modifier = modifier, accent = LimeAccent, title = "TELEMETRY") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusCell("ARSENAL", loadedCount.toString().padStart(3, '0'), LimeAccent, Modifier.weight(1f))
            StatusCell("SAFE", safeCount.toString().padStart(3, '0'), CyanAccent, Modifier.weight(1f))
            StatusCell(
                "STATE",
                if (isPlaying) "FIRING" else "READY",
                if (isPlaying) FuchsiaAccent else LimeAccent,
                Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                .border(1.dp, OutlineDark.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.GpsFixed,
                contentDescription = null,
                tint = getCategoryColor(currentCategory),
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(8.dp))
            LabelCaps(currentCategory, color = getCategoryColor(currentCategory))
            Spacer(Modifier.width(10.dp))
            Text(
                text = currentSound ?: "—",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = if (currentSound != null) Color.White else Color.White.copy(alpha = 0.35f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .background(
                        if (safeMode) LimeAccent.copy(alpha = 0.15f) else OrangeAccent.copy(alpha = 0.15f),
                        RoundedCornerShape(4.dp)
                    )
                    .border(
                        1.dp,
                        if (safeMode) LimeAccent.copy(alpha = 0.6f) else OrangeAccent.copy(alpha = 0.6f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (safeMode) "SAFE" else "OPEN",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = if (safeMode) LimeAccent else OrangeAccent
                )
            }
        }
    }
}

@Composable
private fun StatusCell(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
            color = color.copy(alpha = 0.65f)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = color
        )
    }
}

/**
 * Trace log with live-feed treatment.
 */
@Composable
fun TraceLogPanel(
    entries: List<TraceEntry>,
    modifier: Modifier = Modifier
) {
    NeonControlPanel(modifier = modifier, accent = CyanAccent, title = "TRACE_LOG") {
        if (entries.isEmpty()) {
            Text(
                text = "// AWAITING SIGNAL",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.35f)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                entries.take(5).forEach { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                            .border(1.dp, entry.accent.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(entry.accent)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = entry.title,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = entry.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = OutlineLight
                        )
                    }
                }
            }
        }
    }
}

/**
 * Big red Stop All / Kill Audio panel — pulses while audio is playing.
 */
@Composable
fun KillAudioPanel(
    isPlaying: Boolean,
    onKill: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "kill")
    val pulse by transition.animateFloat(
        0.6f, 1f,
        infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "kill_pulse"
    )
    val redLive = Color(0xFFFF1744)
    val activeAlpha = if (isPlaying) pulse else 0.45f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        redLive.copy(alpha = 0.18f),
                        Color(0xFF1A0407)
                    )
                )
            )
            .border(2.dp, redLive.copy(alpha = activeAlpha), RoundedCornerShape(14.dp))
            .clickable { onKill() }
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(redLive.copy(alpha = activeAlpha * 0.4f))
                    .border(1.5.dp, redLive, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Dangerous,
                    contentDescription = null,
                    tint = redLive,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = "STOP ALL EMISSIONS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    ),
                    color = redLive
                )
                Text(
                    text = if (isPlaying) "// LIVE — TAP TO TERMINATE" else "// KILLSWITCH ARMED",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = redLive.copy(alpha = 0.7f)
                )
            }
            Spacer(Modifier.weight(1f))
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .graphicsLayer { scaleX = pulse; scaleY = pulse }
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(redLive)
                )
            }
        }
    }
}

/**
 * Quick deploy row — six neon-colored category triggers.
 */
@Composable
fun QuickDeployPanel(
    onDeploy: (category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    data class Slot(val cat: String, val icon: ImageVector, val color: Color)
    val slots = listOf(
        Slot("FUNNY", Icons.Default.Air, PrimaryContainer),
        Slot("CREEPY", Icons.Default.DoorFront, FuchsiaAccent),
        Slot("ANIMAL", Icons.Default.Pets, LimeAccent),
        Slot("VOICE", Icons.Default.RecordVoiceOver, CyanAccent),
        Slot("FIGHTER", Icons.Default.Cyclone, OrangeAccent),
        Slot("CARTOON", Icons.Default.AutoFixHigh, Color(0xFFFFD400))
    )
    NeonControlPanel(modifier = modifier, accent = FuchsiaAccent, title = "QUICK_DEPLOY") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(slots) { slot ->
                ControlPanelButton(
                    icon = slot.icon,
                    label = slot.cat,
                    accent = slot.color,
                    modifier = Modifier.width(78.dp)
                ) { onDeploy(slot.cat) }
            }
        }
    }
}

