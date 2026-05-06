package com.pranksterlab.screens

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
import androidx.compose.material3.Switch
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
import com.pranksterlab.components.GlassPanel
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.repository.AudioDiagnostics
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import kotlinx.coroutines.launch

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

    LaunchedEffect(savedVolume) {
        audioPlayerController.setMasterVolume(savedVolume)
    }
    LaunchedEffect(Unit) {
        diagnostics = soundRepository.getAudioDiagnostics()
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            HeadlineText("SYSTEM SETUP", color = CyanAccent)
            Text("Diagnostics & safety console", color = Color.Gray)
        }

        item {
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("MASTER VOLUME", color = CyanAccent)
                    Slider(value = savedVolume, onValueChange = { scope.launch { soundRepository.setMasterVolume(it) } })
                    Text("${(savedVolume * 100).toInt()}%", color = Color.Gray)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Safe Random Mode default", color = Color.White)
                        Switch(checked = safeModeDefault, onCheckedChange = { scope.launch { soundRepository.setSafeRandomModeDefault(it) } })
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Haptic feedback", color = Color.White)
                        Switch(checked = hapticsEnabled, onCheckedChange = { scope.launch { soundRepository.setHapticsEnabled(it) } })
                    }
                }
            }
        }

        item {
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ANIMATION INTENSITY", color = CyanAccent)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("FULL", "REDUCED", "MINIMAL").forEach { value ->
                            val selected = animationIntensity == value
                            Text(
                                value,
                                color = if (selected) Color.Black else CyanAccent,
                                modifier = Modifier
                                    .background(if (selected) CyanAccent else Color.Transparent, RoundedCornerShape(12.dp))
                                    .border(1.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .clickable { scope.launch { soundRepository.setAnimationIntensity(value) } }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("AUDIO DIAGNOSTICS", color = FuchsiaAccent)
                    Text("Total catalog sounds: ${diagnostics?.totalCatalogSounds ?: 0}", color = Color.White)
                    Text("Valid/playable sounds: ${diagnostics?.playableCatalogSounds ?: 0}", color = Color.White)
                    Text("Invalid sounds: ${diagnostics?.invalidCatalogSounds ?: 0}", color = Color.White)
                    Text("Missing assets: ${diagnostics?.missingAssets ?: 0}", color = Color.White)
                    Text("Uncataloged assets: ${diagnostics?.uncatalogedAssets ?: 0}", color = Color.White)
                    Text("Last validation: ${diagnostics?.lastValidationResult ?: "Not run"}", color = Color.Gray)
                    Text("Last playback error: ${playbackState.lastError ?: "None"}", color = if (playbackState.lastError == null) Color.Gray else Color(0xFFFCA5A5))
                    Button(onClick = { diagnostics = soundRepository.getAudioDiagnostics() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, FuchsiaAccent.copy(alpha = 0.4f))) {
                        Icon(Icons.Default.Refresh, "Refresh diagnostics", tint = FuchsiaAccent)
                        Text("REFRESH", color = FuchsiaAccent, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }

        item {
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("DATA CONTROL", color = CyanAccent)
                    ActionButton("Clear recent sounds") { confirmAction = "clear_recent" }
                    ActionButton("Clear favorites") { confirmAction = "clear_favorites" }
                    ActionButton("Delete generated sounds") { confirmAction = "delete_generated" }
                    ActionButton("Reset Sound Forge presets") { confirmAction = "reset_forge" }
                    ActionButton("Open validation report") { actionResult = "Validation report unavailable in current build artifacts." }
                }
            }
        }

        item {
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFFACC15))
                        Text("RESPONSIBLE USE", color = Color(0xFFFACC15), modifier = Modifier.padding(start = 8.dp))
                    }
                    Text("• Keep pranks harmless and consensual.", color = Color.White)
                    Text("• Do not use emergency/panic sounds publicly.", color = Color.White)
                    Text("• Do not impersonate people or official alerts.", color = Color.White)
                    Text("• Prank messaging must not spoof identity.", color = Color.White)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("I understand", color = Color.Gray)
                        Switch(checked = safetyAck, onCheckedChange = { scope.launch { soundRepository.setSafetyAck(it) } })
                    }
                }
            }
        }

        item {
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
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

    if (confirmAction != null) {
        AlertDialog(
            onDismissRequest = { confirmAction = null },
            title = { Text("Confirm action") },
            text = { Text("This action may remove user data. Continue?") },
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
                                actionResult = "No persisted Sound Forge presets found to reset."
                            }
                        }
                        diagnostics = soundRepository.getAudioDiagnostics()
                        confirmAction = null
                    }
                }) { Text("CONFIRM") }
            },
            dismissButton = { TextButton(onClick = { confirmAction = null }) { Text("CANCEL") } }
        )
    }

    if (actionResult != null) {
        AlertDialog(
            onDismissRequest = { actionResult = null },
            title = { Text("Status") },
            text = { Text(actionResult!!) },
            confirmButton = { TextButton(onClick = { actionResult = null }) { Text("OK") } }
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
