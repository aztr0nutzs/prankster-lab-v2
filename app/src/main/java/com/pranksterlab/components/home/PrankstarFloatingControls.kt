package com.pranksterlab.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun PrankstarFloatingControls(
    isPlaying: Boolean,
    onOpenStash: () -> Unit,
    onOpenJokes: () -> Unit,
    onOpenForge: () -> Unit,
    onStopAll: () -> Unit,
    onDeploy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloatingControlButton(Icons.Default.LibraryMusic, "Open Sound Stash", Color(0xFF00E8FF), onOpenStash)
        FloatingControlButton(Icons.Default.RecordVoiceOver, "Open Joke Gen", Color(0xFFFF00CC), onOpenJokes)
        FloatingControlButton(Icons.Default.PlayArrow, "Deploy random prank", Color(0xFF66FF00), onDeploy)
        FloatingControlButton(Icons.Default.Stop, "Stop all audio", if (isPlaying) Color(0xFFFF2200) else Color(0xFFFF6600), onStopAll)
        FloatingControlButton(Icons.Default.PrecisionManufacturing, "Open Sound Forge", Color(0xFFFFCC00), onOpenForge)
    }
}

@Composable
private fun FloatingControlButton(
    icon: ImageVector,
    description: String,
    color: Color,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(color.copy(alpha = 0.24f), Color.Black.copy(alpha = 0.78f))
                )
            )
            .border(1.dp, color.copy(alpha = 0.72f), CircleShape)
            .semantics { contentDescription = description }
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(21.dp))
    }
}
