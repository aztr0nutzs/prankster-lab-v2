package com.pranksterlab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.R
import com.pranksterlab.components.GlassPanel
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.components.PrankstarHeader
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.theme.*
import kotlinx.coroutines.delay

enum class TimerState {
    IDLE, COUNTDOWN, PLAYING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPrankScreen(soundRepository: SoundRepository, audioPlayerController: AudioPlayerController) {
    var soundsList by remember { mutableStateOf(emptyList<PrankSound>()) }
    var selectedSound by remember { mutableStateOf<PrankSound?>(null) }
    var delaySeconds by remember { mutableIntStateOf(5) }
    var remainingSeconds by remember { mutableIntStateOf(0) }
    var timerState by remember { mutableStateOf(TimerState.IDLE) }
    var showSoundPicker by remember { mutableStateOf(false) }

    val playbackState by audioPlayerController.playbackState.collectAsState()

    LaunchedEffect(Unit) {
        val bundled = soundRepository.getBundledSounds()
        soundRepository.getCustomSoundsFlow().collect { custom ->
            soundsList = (bundled + custom).filter { soundRepository.isSoundPlayable(it) }
        }
    }

    LaunchedEffect(soundsList) {
        if (soundsList.isEmpty()) return@LaunchedEffect
        val pendingSoundId = soundRepository.consumePendingTimerSoundId() ?: return@LaunchedEffect
        selectedSound = soundsList.firstOrNull { it.id == pendingSoundId }
    }

    LaunchedEffect(timerState, remainingSeconds) {
        if (timerState == TimerState.COUNTDOWN && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
            if (remainingSeconds == 0) {
                timerState = TimerState.PLAYING
                selectedSound?.let {
                    val started = audioPlayerController.playPrankSound(it, isLooping = it.loopable)
                    if (!started) {
                        timerState = TimerState.IDLE
                    }
                }
            }
        }
    }

    LaunchedEffect(playbackState.isPlaying) {
        if (timerState == TimerState.PLAYING && !playbackState.isPlaying) {
            timerState = TimerState.IDLE
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PrankstarHeader(
            title = "Timer Prank",
            subtitle = "Delayed Detonation Console",
            imageRes = R.drawable.prankstar_sn3,
            statusLabel = when (timerState) {
                TimerState.COUNTDOWN -> "ARMED"
                TimerState.PLAYING -> "LIVE"
                TimerState.IDLE -> "READY"
            }
        )
        Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp)) {
        HeadlineText("TIMER PRANK", color = CyanAccent)
        Spacer(modifier = Modifier.height(8.dp))
        LabelCaps("Set a delay, hide your device, and watch.", color = OnBackground.copy(alpha=0.6f))
        
        Spacer(modifier = Modifier.height(32.dp))

        // Timer Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(GlassBackground)
                .border(2.dp, if (timerState == TimerState.COUNTDOWN) FuchsiaAccent else CyanAccent.copy(alpha=0.2f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (timerState == TimerState.COUNTDOWN) {
                    Text(
                        text = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                        color = FuchsiaAccent
                    )
                    LabelCaps("DETONATION IMMINENT", color = FuchsiaAccent)
                } else if (timerState == TimerState.PLAYING) {
                    Icon(Icons.Default.VolumeUp, null, tint = CyanAccent, modifier = Modifier.size(64.dp))
                    LabelCaps("EXECUTING PRANK", color = CyanAccent)
                } else {
                    Text(
                        text = String.format("%02d:%02d", delaySeconds / 60, delaySeconds % 60),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                        color = CyanAccent.copy(alpha = 0.5f)
                    )
                    LabelCaps("TIMER ARMED", color = CyanAccent.copy(alpha = 0.5f))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Delay Selection
        HeadlineText("DELAY PRESETS", color = OnBackground)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5, 15, 30, 60, 300).forEach { sec ->
                PresetButton(
                    label = if (sec < 60) "${sec}S" else "${sec/60}M",
                    isSelected = delaySeconds == sec && timerState == TimerState.IDLE,
                    enabled = timerState == TimerState.IDLE,
                    onClick = { delaySeconds = sec }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sound Selection
        HeadlineText("SOUND PAYLOAD", color = OnBackground)
        Spacer(modifier = Modifier.height(16.dp))
        GlassPanel(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = timerState == TimerState.IDLE) { showSoundPicker = true }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, null, tint = CyanAccent)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = selectedSound?.name ?: "Select Sound...",
                        color = if (selectedSound != null) Color.White else Color.Gray,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (selectedSound != null) {
                        LabelCaps(text = selectedSound!!.category, color = FuchsiaAccent)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, null, tint = OnBackground.copy(alpha=0.5f))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Safety Copy
        Text(
            text = "Use responsibly. Avoid public spaces or emergencies.",
            color = OnBackground.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
        )

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth().height(56.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (timerState == TimerState.COUNTDOWN) {
                Button(
                    onClick = { timerState = TimerState.IDLE },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Cancel, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CANCEL")
                }
            } else {
                Button(
                    onClick = {
                        remainingSeconds = delaySeconds
                        timerState = TimerState.COUNTDOWN
                    },
                    enabled = selectedSound != null && timerState == TimerState.IDLE,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.HourglassEmpty, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("START TIMER")
                }
            }
            
            if (timerState == TimerState.PLAYING) {
                Button(
                    onClick = { audioPlayerController.stopAll() },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Dangerous, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("KILL SWITCH")
                }
            }
        }
        }
    }

    if (showSoundPicker) {
        ModalBottomSheet(onDismissRequest = { showSoundPicker = false }, containerColor = SurfaceDark) {
            Column(modifier = Modifier.fillMaxHeight(0.7f).padding(16.dp)) {
                HeadlineText("CHOOSE PAYLOAD", color = OnBackground)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(soundsList) { sound ->
                        GlassPanel(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSound = sound
                                    showSoundPicker = false
                                }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, null, tint = CyanAccent)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(sound.name, color = Color.White)
                                    LabelCaps(sound.category, color = OnBackground.copy(alpha=0.6f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.PresetButton(label: String, isSelected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) CyanAccent else GlassBackground)
            .border(1.dp, if (isSelected) Color.Transparent else CyanAccent.copy(alpha=0.3f), RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = if (isSelected) Color.Black else Color.White, style = MaterialTheme.typography.labelLarge)
    }
}
