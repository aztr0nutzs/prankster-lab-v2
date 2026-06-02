package com.pranksterlab.components.reactor.ultimate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class UltimateStripAction {
    AUDIO,
    GEAR,
    SPRING,
    HOLO,
    ZAP,
    AI
}

@Composable
fun UltimateReactorSideStrip(
    side: UltimateReactorSide,
    state: UltimateReactorState,
    modifier: Modifier = Modifier,
    onAction: (UltimateStripAction) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (side == UltimateReactorSide.LEFT) {
            StripButton(Icons.Default.GraphicEq, state.audioModEnabled, Color(0xFF66FF00)) { onAction(UltimateStripAction.AUDIO) }
            LedColumn(state.powered)
            UltimateReactorVuMeter(state.powered, Color(0xFF66FF00), phaseOffset = 1)
            UltimateReactorVuMeter(state.powered, Color(0xFF00E8FF), phaseOffset = 2)
            StripButton(Icons.Default.Settings, false, Color(0xFF00E8FF)) { onAction(UltimateStripAction.GEAR) }
            StripButton(Icons.Default.Refresh, false, Color(0xFFFFCC00)) { onAction(UltimateStripAction.SPRING) }
        } else {
            StripButton(Icons.Default.MyLocation, state.holoProjectorEnabled, Color(0xFF00E8FF)) { onAction(UltimateStripAction.HOLO) }
            LedColumn(state.powered)
            UltimateReactorVuMeter(state.powered, Color(0xFFFF00CC), phaseOffset = 3)
            UltimateReactorVuMeter(state.powered, Color(0xFFFF6600), phaseOffset = 4)
            StripButton(Icons.Default.Bolt, false, Color(0xFFFF2200)) { onAction(UltimateStripAction.ZAP) }
            StripButton(Icons.Default.SmartToy, state.mischiefAiEnabled, Color(0xFFFF00CC)) { onAction(UltimateStripAction.AI) }
        }
    }
}

enum class UltimateReactorSide { LEFT, RIGHT }

@Composable
private fun StripButton(
    icon: ImageVector,
    active: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 32.dp, height = 28.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color(0xF2050F1E))
            .border(1.dp, if (active) color else Color(0xFF1A3050), RoundedCornerShape(5.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(17.dp))
    }
}

@Composable
private fun LedColumn(powered: Boolean) {
    val colors = if (powered) {
        listOf(Color(0xFF66FF00), Color(0xFF00E8FF), Color(0xFFFFCC00), Color(0xFF66FF00), Color(0xFFFF00CC))
    } else {
        List(5) { Color(0xFF111111) }
    }
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        colors.forEach { color ->
            Box(Modifier.size(6.dp).clip(CircleShape).background(color))
        }
    }
}
