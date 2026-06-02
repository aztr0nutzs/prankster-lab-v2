package com.pranksterlab.components.reactor.ultimate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UltimateReactorControls(
    state: UltimateReactorState,
    modifier: Modifier = Modifier,
    onStateChange: (UltimateReactorState) -> Unit,
    onDeploy: () -> Unit,
    onPowerToggle: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFA030810))
            .border(1.dp, Color(0xFF102035))
            .padding(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.fillMaxWidth()) {
            UltimateReactorTab.entries.forEach { tab ->
                TabButton(tab.name, state.activeTab == tab, Modifier.weight(1f)) {
                    onStateChange(state.copy(activeTab = tab, lastLogMessage = "TAB ${tab.name} ACTIVE"))
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().height(190.dp).verticalScroll(rememberScrollState())) {
            when (state.activeTab) {
                UltimateReactorTab.CORE -> CorePanel(state, onStateChange)
                UltimateReactorTab.MODE -> ModePanel(state, onStateChange, onDeploy)
                UltimateReactorTab.SENSOR -> SensorPanel(state, onStateChange)
                UltimateReactorTab.LOG -> LogPanel(state, onDeploy, onPowerToggle)
            }
        }
    }
}

@Composable
private fun CorePanel(state: UltimateReactorState, onStateChange: (UltimateReactorState) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            UltimateReactorKnob("CHAOS", "INTENSITY", state.chaos, Color(0xFF00E8FF), {
                onStateChange(state.copy(chaos = it, intensity = it, lastLogMessage = "CHAOS INTENSITY $it%"))
            }, Modifier.weight(1f))
            UltimateReactorKnob("SNEAK", "STEALTH", state.sneak, Color(0xFF00AAFF), {
                onStateChange(state.copy(sneak = it, lastLogMessage = "STEALTH FIELD $it%"))
            }, Modifier.weight(1f))
            UltimateReactorKnob("BOOM", "PAYLOAD", state.boom, Color(0xFFFF6600), {
                onStateChange(state.copy(boom = it, lastLogMessage = "PAYLOAD BOOM $it%"))
            }, Modifier.weight(1f))
        }
        ReactorSlider("INTENSITY", state.intensity) {
            onStateChange(state.copy(intensity = it, chaos = it, lastLogMessage = "INTENSITY SET $it%"))
        }
        ReactorSlider("SYNC FREQUENCY", state.syncFrequency) {
            onStateChange(state.copy(syncFrequency = it, lastLogMessage = "SYNC FREQUENCY $it%"))
        }
    }
}

@Composable
private fun ModePanel(state: UltimateReactorState, onStateChange: (UltimateReactorState) -> Unit, onDeploy: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            UltimatePrankMode.entries.forEach { mode ->
                ModeButton(mode.name, state.mode == mode, mode.accentColor(), Modifier.weight(1f)) {
                    onStateChange(state.copy(mode = mode, lastLogMessage = "${mode.name} MODE SELECTED"))
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            UltimatePrankType.entries.forEach { type ->
                ModeButton(type.name, state.prankType == type, Color(0xFFFF00CC), Modifier.weight(1f)) {
                    onStateChange(state.copy(prankType = type, lastLogMessage = "${type.name} TYPE ARMED"))
                }
            }
        }
        DeployButton(state.powered, onDeploy)
    }
}

@Composable
private fun SensorPanel(state: UltimateReactorState, onStateChange: (UltimateReactorState) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ToggleRow("AUDIO MOD", state.audioModEnabled) { onStateChange(state.copy(audioModEnabled = it, lastLogMessage = "AUDIO MOD ${if (it) "ON" else "OFF"}")) }
        ToggleRow("HOLO PROJ", state.holoProjectorEnabled) { onStateChange(state.copy(holoProjectorEnabled = it, lastLogMessage = "HOLO PROJECTOR ${if (it) "ON" else "OFF"}")) }
        ToggleRow("MISCHIEF AI", state.mischiefAiEnabled) { onStateChange(state.copy(mischiefAiEnabled = it, lastLogMessage = "MISCHIEF AI ${if (it) "ONLINE" else "OFFLINE"}")) }
        ToggleRow("NERD MODE", state.nerdModeEnabled) { onStateChange(state.copy(nerdModeEnabled = it, lastLogMessage = "NERD MODE ${if (it) "ON" else "OFF"}")) }
        ToggleRow("REMOTE ARM", state.remoteArmEnabled) { onStateChange(state.copy(remoteArmEnabled = it, lastLogMessage = "REMOTE ARM ${if (it) "READY" else "LOCKED"}")) }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            UltimateReactorRadar(state.powered, Modifier.width(86.dp))
            VuMeterRow(state.powered, Modifier.weight(1f))
        }
        WaveformStrip(state.powered, state.intensity)
    }
}

@Composable
private fun LogPanel(state: UltimateReactorState, onDeploy: () -> Unit, onPowerToggle: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            state.prankCount.toString().padStart(4, '0'),
            color = if (state.isOverloaded) Color(0xFFFF2200) else Color(0xFF66FF00),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 3.sp)
        )
        Text("PRANKS DEPLOYED", color = Color(0xFF3A6A3A), style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.5.sp))
        Text("${state.tempCelsius}C", color = Color(0xFFFFCC00), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp))
        DeployButton(state.powered, onDeploy)
        PowerBar("MAIN", if (state.powered) 0.82f else 0.03f, Color(0xFF66FF00))
        PowerBar("AUX", if (state.powered) 0.62f else 0.02f, Color(0xFF00E8FF))
        PowerBar("EMRG", if (state.powered) 0.38f else 0.01f, Color(0xFFFF2200))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatusText("REACTOR ${if (state.powered) "ONLINE" else "OFFLINE"}", state.powered)
            StatusText("AI CORE ${if (state.mischiefAiEnabled) "ACTIVE" else "PAUSED"}", state.mischiefAiEnabled)
        }
        OutlinedButton(onClick = onPowerToggle, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF2200))) {
            Text(if (state.powered) "POWER DOWN" else "POWER UP")
        }
        Text(
            state.lastLogMessage,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(Color.Black.copy(alpha = 0.45f)).padding(6.dp),
            color = Color(0xFF66FF00),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.8.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DeployButton(powered: Boolean, onDeploy: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Brush.radialGradient(listOf(Color(0xFF260000), Color(0xFF080000))))
            .border(2.dp, if (powered) Color(0xFFFF2200) else Color(0xFF331111), RoundedCornerShape(7.dp))
            .clickable(enabled = powered, onClick = onDeploy),
        contentAlignment = Alignment.Center
    ) {
        Text("DEPLOY PRANK", color = if (powered) Color(0xFFFF2200) else Color(0xFF664444), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 3.sp))
    }
}

@Composable
private fun TabButton(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(28.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) Color(0xFF071525) else Color.Transparent)
            .border(1.dp, if (selected) Color(0xFF00E8FF) else Color(0xFF1A3050), RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) Color(0xFF00E8FF) else Color(0xFF3A6A8A), style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun ModeButton(text: String, selected: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(if (selected) color.copy(alpha = 0.16f) else Color(0xCC050C16))
            .border(1.dp, if (selected) color else Color(0xFF1A3050), RoundedCornerShape(5.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) color else Color(0xFF3A6A8A), style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Black), textAlign = TextAlign.Center)
    }
}

@Composable
private fun ReactorSlider(label: String, value: Int, onChange: (Int) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color(0xFF3A5A7A), style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 1.sp))
            Text("$value%", color = Color(0xFF00E8FF), style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Black))
        }
        Slider(value = value.toFloat(), onValueChange = { onChange(it.toInt().coerceIn(0, 100)) }, valueRange = 0f..100f)
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF3A7A5A), style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun VuMeterRow(powered: Boolean, modifier: Modifier = Modifier) {
    Row(modifier = modifier.height(58.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
        val colors = listOf(Color(0xFF66FF00), Color(0xFFAAFF00), Color(0xFFFFCC00), Color(0xFFFF6600), Color(0xFFFF2200), Color(0xFFFF00CC), Color(0xFF00E8FF), Color(0xFF66FF00))
        colors.forEachIndexed { index, color -> UltimateReactorVuMeter(powered, color, Modifier.weight(1f), index) }
    }
}

@Composable
private fun WaveformStrip(powered: Boolean, intensity: Int) {
    Row(
        Modifier.fillMaxWidth().height(28.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF020A10)).padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(26) { i ->
            val h = if (powered) 4 + ((i * 7 + intensity) % 22) else 2
            Box(Modifier.weight(1f).height(h.dp).background(if (i % 2 == 0) Color(0xFF00FF88) else Color(0xFF00E8FF)))
        }
    }
}

@Composable
private fun PowerBar(label: String, value: Float, color: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color(0xFF3A5A7A), style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), modifier = Modifier.width(34.dp))
        LinearProgressIndicator(progress = { value }, modifier = Modifier.weight(1f).height(6.dp), color = color, trackColor = Color(0xFF040C18))
    }
}

@Composable
private fun StatusText(text: String, enabled: Boolean) {
    Text(text, color = if (enabled) Color(0xFF66FF00) else Color(0xFF555555), style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.8.sp))
}
