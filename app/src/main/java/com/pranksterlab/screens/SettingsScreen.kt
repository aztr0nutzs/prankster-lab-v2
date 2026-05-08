package com.pranksterlab.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pranksterlab.BuildConfig
import com.pranksterlab.R
import com.pranksterlab.components.HUDCard
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.components.PrankstarHeader
import com.pranksterlab.components.ScanlineOverlay
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.repository.AudioDiagnostics
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrangeAccent
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun SettingsScreen(soundRepository: SoundRepository, audioPlayerController: AudioPlayerController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val savedVolume by soundRepository.getMasterVolumeFlow().collectAsState(initial = audioPlayerController.getMasterVolume())
    val safeModeDefault by soundRepository.getSafeRandomModeDefaultFlow().collectAsState(initial = true)
    val hapticsEnabled by soundRepository.getHapticsEnabledFlow().collectAsState(initial = true)
    val animationIntensity by soundRepository.getAnimationIntensityFlow().collectAsState(initial = "FULL")
    val safetyAck by soundRepository.getSafetyAckFlow().collectAsState(initial = false)
    val playbackState by audioPlayerController.playbackState.collectAsState()

    var diagnostics by remember { mutableStateOf<AudioDiagnostics?>(null) }
    var confirmAction by remember { mutableStateOf<String?>(null) }
    var actionResult by remember { mutableStateOf<String?>(null) }
    var showDeveloperDiagnostics by remember { mutableStateOf(false) }

    LaunchedEffect(savedVolume) {
        audioPlayerController.setMasterVolume(savedVolume)
    }
    LaunchedEffect(Unit) {
        diagnostics = soundRepository.getAudioDiagnostics()
    }

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        ScanlineOverlay()

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                PrankstarHeader(
                    title = "System Setup",
                    subtitle = "Diagnostics / Safety / App Control",
                    imageRes = R.drawable.prankstar_sn2,
                    statusLabel = if ((diagnostics?.invalidCatalogSounds ?: 0) > 0) "ALERT" else "STABLE"
                )
            }
            item {
                HeadlineText("SYSTEM SETUP", color = CyanAccent)
                Text("DIAGNOSTICS / SAFETY / APP CONTROL", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }

            item {
                HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = CyanAccent) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LabelCaps("MASTER VOLUME", color = CyanAccent)
                    Slider(
                        value = savedVolume,
                        onValueChange = { scope.launch { soundRepository.setMasterVolume(it) } },
                        colors = SliderDefaults.colors(
                            thumbColor = FuchsiaAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )
                    Text("${(savedVolume * 100).toInt()}%", color = Color.Gray)
                    NeonSwitchRow("Safe Random Mode default", safeModeDefault) { scope.launch { soundRepository.setSafeRandomModeDefault(it) } }
                    NeonSwitchRow("Haptic feedback", hapticsEnabled) { scope.launch { soundRepository.setHapticsEnabled(it) } }
                }
            }
        }

        item {
            HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = LimeAccent) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LabelCaps("ANIMATION INTENSITY", color = LimeAccent)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("FULL", "REDUCED", "MINIMAL").forEach { value ->
                            val selected = animationIntensity == value
                            Text(
                                value,
                                color = if (selected) Color.Black else LimeAccent,
                                modifier = Modifier
                                    .background(if (selected) LimeAccent else Color.Transparent, RoundedCornerShape(12.dp))
                                    .border(1.dp, LimeAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .clickable { scope.launch { soundRepository.setAnimationIntensity(value) } }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = FuchsiaAccent) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        LabelCaps("AUDIO DIAGNOSTICS", color = FuchsiaAccent)
                        Text(
                            if (showDeveloperDiagnostics) "HIDE DEV" else "SHOW DEV",
                            color = FuchsiaAccent,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.clickable { showDeveloperDiagnostics = !showDeveloperDiagnostics }
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DiagnosticReadout("CATALOG", diagnostics?.totalCatalogSounds ?: 0, CyanAccent, Modifier.weight(1f))
                        DiagnosticReadout("PLAYABLE", diagnostics?.playableCatalogSounds ?: 0, LimeAccent, Modifier.weight(1f))
                        DiagnosticReadout("INVALID", diagnostics?.invalidCatalogSounds ?: 0, OrangeAccent, Modifier.weight(1f))
                    }
                    if (showDeveloperDiagnostics) {
                        DiagnosticLine("Missing assets", "${diagnostics?.missingAssets ?: 0}")
                        DiagnosticLine("Uncataloged assets", "${diagnostics?.uncatalogedAssets ?: 0}")
                        DiagnosticLine("Last validation", diagnostics?.lastValidationResult ?: "Not run")
                        DiagnosticLine("Last playback error", playbackState.lastError ?: "None", if (playbackState.lastError == null) Color.Gray else Color(0xFFFCA5A5))
                    }
                    Button(onClick = { diagnostics = soundRepository.getAudioDiagnostics() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, FuchsiaAccent.copy(alpha = 0.4f))) {
                        Icon(Icons.Default.Refresh, "Refresh diagnostics", tint = FuchsiaAccent)
                        Text("REFRESH SCAN", color = FuchsiaAccent, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }

        item {
            HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = CyanAccent) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LabelCaps("DATA CONTROL", color = CyanAccent)
                    ActionButton("Clear recent sounds") { confirmAction = "clear_recent" }
                    ActionButton("Clear favorites") { confirmAction = "clear_favorites" }
                    ActionButton("Delete generated sounds") { confirmAction = "delete_generated" }
                    ActionButton("Reset Sound Forge presets") { confirmAction = "reset_forge" }
                    ActionButton("Open validation report") {
                        val report = findValidationReport(context.filesDir, context.cacheDir, context.getExternalFilesDir(null))
                        if (report != null) {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(Uri.fromFile(report), if (report.extension == "html") "text/html" else "text/plain")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            runCatching { context.startActivity(intent) }
                                .onFailure { actionResult = "Validation report found at ${report.absolutePath}, but no viewer is available." }
                        } else {
                            actionResult = "Validation report unavailable in current build artifacts."
                        }
                    }
                }
            }
        }

        item {
            HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = OrangeAccent) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFFACC15))
                        LabelCaps("RESPONSIBLE USE", color = Color(0xFFFACC15), modifier = Modifier.padding(start = 8.dp))
                    }
                    Text("- Keep pranks harmless and consensual.", color = Color.White)
                    Text("- Do not use emergency/panic sounds publicly.", color = Color.White)
                    Text("- Do not impersonate people or official alerts.", color = Color.White)
                    Text("- Prank messaging must not spoof identity.", color = Color.White)
                    NeonSwitchRow("I understand", safetyAck) { scope.launch { soundRepository.setSafetyAck(it) } }
                }
            }
        }

        item {
            HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = CyanAccent) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = CyanAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("APP INFO", color = CyanAccent)
                    }
                    Text("Version: ${BuildConfig.VERSION_NAME}", color = Color.White)
                    Text("Build: ${BuildConfig.VERSION_CODE}", color = Color.White)
                    Text("Package: ${context.packageName}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
    }

    if (confirmAction != null) {
        AlertDialog(
            onDismissRequest = { confirmAction = null },
            containerColor = Color(0xFF080B12),
            title = { Text("CONFIRM ACTION", color = OrangeAccent) },
            text = { Text("This action may remove user data. Continue?", color = Color.White) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        when (confirmAction) {
                            "clear_recent" -> {
                                soundRepository.clearRecentSounds()
                                actionResult = "Recent sounds cleared."
                            }
                            "clear_favorites" -> {
                                soundRepository.clearFavorites()
                                actionResult = "Favorites cleared."
                            }
                            "delete_generated" -> {
                                val removed = soundRepository.deleteGeneratedSounds()
                                actionResult = "Deleted $removed generated sounds."
                            }
                            "reset_forge" -> {
                                val removed = soundRepository.resetSoundForgePresets()
                                actionResult = if (removed > 0) {
                                    "Reset $removed Sound Forge preset store(s)."
                                } else {
                                    "No persisted Sound Forge presets found to reset."
                                }
                            }
                        }
                        diagnostics = soundRepository.getAudioDiagnostics()
                        confirmAction = null
                    }
                }) { Text("CONFIRM", color = Color(0xFFF87171)) }
            },
            dismissButton = { TextButton(onClick = { confirmAction = null }) { Text("CANCEL", color = Color.Gray) } }
        )
    }

    if (actionResult != null) {
        AlertDialog(
            onDismissRequest = { actionResult = null },
            containerColor = Color(0xFF080B12),
            title = { Text("STATUS", color = CyanAccent) },
            text = { Text(actionResult!!, color = Color.White) },
            confirmButton = { TextButton(onClick = { actionResult = null }) { Text("OK", color = CyanAccent) } }
        )
    }
}

@Composable
private fun ActionButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFF87171))
        Text(label.uppercase(), color = Color(0xFFF87171), modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun NeonSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.White)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = CyanAccent,
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF1E293B)
            )
        )
    }
}

@Composable
private fun DiagnosticReadout(label: String, value: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LabelCaps(label, color = Color.Gray)
        Text(value.toString(), color = color, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun DiagnosticLine(label: String, value: String, valueColor: Color = Color.Gray) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White)
        Text(value, color = valueColor)
    }
}

private fun findValidationReport(vararg roots: File?): File? {
    val names = setOf(
        "validation_report.html",
        "validation_report.json",
        "sound_validation_report.html",
        "sound_validation_report.json",
        "audio_validation_report.txt"
    )
    return roots.filterNotNull()
        .flatMap { root -> names.map { File(root, it) } }
        .firstOrNull { it.exists() && it.length() > 0L }
}
