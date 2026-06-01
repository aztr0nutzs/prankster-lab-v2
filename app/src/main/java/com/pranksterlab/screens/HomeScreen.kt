package com.pranksterlab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.components.GlassPanel
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.components.PrankstarHeader
import com.pranksterlab.components.ScanlineOverlay
import com.pranksterlab.components.bot.PrankstarBotMood
import com.pranksterlab.components.bot.PrankstarBotVideo
import com.pranksterlab.R
import com.pranksterlab.components.reactor.ReactorCorePanel
import com.pranksterlab.components.reactor.StatusReadout
import com.pranksterlab.components.reactor.QuickDeployPanel
import com.pranksterlab.components.reactor.TraceLogPanel
import com.pranksterlab.components.reactor.TraceEntry
import com.pranksterlab.components.reactor.KillAudioPanel
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.core.model.PrankSound
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.ErrorRed
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.GlassBackground
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OnBackground
import com.pranksterlab.theme.OrangeAccent
import com.pranksterlab.theme.OutlineDark
import com.pranksterlab.theme.PrimaryContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    audioPlayerController: AudioPlayerController,
    soundRepository: SoundRepository,
    onNavigate: (String) -> Unit = {}
) {
    var soundsList       by remember { mutableStateOf(emptyList<PrankSound>()) }
    var traceLog         by remember { mutableStateOf(contextualTraceLog()) }
    var lastSoundName    by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("FUNNY") }
    var playbackError    by remember { mutableStateOf<String?>(null) }
    var showWakeup       by remember { mutableStateOf(true) }

    val playbackState by audioPlayerController.playbackState.collectAsState()

    val botMood = when {
        showWakeup                                       -> PrankstarBotMood.WAKEUP
        playbackError != null
            || playbackState.lastError != null           -> PrankstarBotMood.ERROR
        playbackState.isPlaying                          -> PrankstarBotMood.PLAYING
        soundsList.isEmpty()                             -> PrankstarBotMood.THINKING
        else                                             -> PrankstarBotMood.ARMED
    }
    val botMessage = when (botMood) {
        PrankstarBotMood.WAKEUP   -> "Booting NEO assistant."
        PrankstarBotMood.PLAYING  -> "Signal deployed: ${playbackState.currentSoundTitle ?: lastSoundName ?: "chaos sample"}."
        PrankstarBotMood.ERROR    -> "Playback needs attention. Check the trace log."
        PrankstarBotMood.THINKING -> "Scanning the Sound Stash."
        else                      -> "Ready to deploy chaos."
    }

    LaunchedEffect(Unit) {
        delay(1600)
        showWakeup = false
    }

    LaunchedEffect(Unit) {
        val bundled = withContext(Dispatchers.IO) { soundRepository.getBundledSounds() }
        soundRepository.getCustomSoundsFlow().collect { custom ->
            soundsList = bundled + custom
        }
    }

    fun addLog(event: String) {
        val now = java.time.LocalTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
        traceLog = (listOf(LogData(event, now)) + traceLog).take(5)
    }

    // ── Core reactor trigger (same logic as original) ─────────────────────
    fun triggerReactor(category: String, intensity: Int) {
        val acceptsGenerated = category.equals("VOICE", true) || category.equals("GENERATED", true)
        val candidates = soundsList.filter {
            soundRepository.isSoundPlayable(it) &&
                it.isSafeForRandomMode &&
                (it.category.equals(category, ignoreCase = true) ||
                    (acceptsGenerated && soundRepository.isGeneratedVoiceClip(it))) &&
                (if (intensity > 1) it.intensityLevel >= intensity else true)
        }

        val sound = if (candidates.isNotEmpty()) candidates.random() else {
            soundsList.filter {
                soundRepository.isSoundPlayable(it) &&
                    (it.category.equals(category, ignoreCase = true) ||
                        (acceptsGenerated && soundRepository.isGeneratedVoiceClip(it)))
            }.randomOrNull()
        }

        if (sound != null) {
            playbackError = null
            val started = audioPlayerController.playPrankSound(sound)
            if (started) {
                lastSoundName = sound.name
            } else {
                playbackError = "INVALID_ASSET"
                addLog("CORE ERROR: ${sound.name} REJECTED")
            }
        } else {
            playbackError = "EMPTY_CAT"
            addLog("CORE ERROR: NO SAMPLES IN $category")
        }
    }

    LaunchedEffect(playbackState) {
        if (playbackState.isPlaying) {
            addLog("DEPLOYED: ${playbackState.currentSoundTitle ?: "Unknown"}")
            lastSoundName = playbackState.currentSoundTitle
            playbackError = null
        } else if (playbackState.lastError != null) {
            addLog("ERROR: ${playbackState.lastError}")
            playbackError = "IO_FAILURE"
        }
    }

    // ── Layout ───────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        ScanlineOverlay()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Screen header ────────────────────────────────────────────
            item {
                PrankstarHeader(
                    title       = "Core",
                    subtitle    = "Quick Play Reactor / Stash + Joke Launch",
                    imageRes    = R.drawable.prankstar_sn1,
                    statusLabel = if (playbackState.isPlaying) "LIVE" else "ARMED"
                )
            }

            // ── Waveform decoration ──────────────────────────────────────
            item { WaveformHeader() }

            // ── Prankstar Bot compact panel ──────────────────────────────
            item {
                PrankstarBotVideo(
                    mood     = botMood,
                    message  = botMessage,
                    compact  = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── CORE REACTOR (enhanced — with nav callbacks) ─────────────
            item {
                val hasCustom  = soundsList.any { it.isCustom }
                val safeCount  = soundsList.count { it.isSafeForRandomMode }
                ReactorCorePanel(
                    currentSoundName  = lastSoundName,
                    currentCategory   = selectedCategory,
                    isPlaying         = playbackState.isPlaying,
                    hasCustomSounds   = hasCustom,
                    playbackError     = playbackError,
                    loadedSoundCount  = soundsList.size,
                    safeSoundCount    = safeCount,
                    onTrigger         = { cat, intensity -> triggerReactor(cat, intensity) },
                    onStop            = {
                        audioPlayerController.stopAll()
                        addLog("KILLSWITCH ACTIVATED")
                    },
                    onCategoryChange  = { selectedCategory = it },
                    coreImageRes      = R.drawable.prankstar_core,
                    // New navigation callbacks wired to the action strip
                    onOpenStash       = { onNavigate("library") },
                    onOpenJokes       = { onNavigate("voice_lab") },
                    onOpenForge       = { onNavigate("forge") },
                )
            }

            // ── Telemetry readout ────────────────────────────────────────
            item {
                StatusReadout(
                    loadedCount     = soundsList.size,
                    safeCount       = soundsList.count { it.isSafeForRandomMode },
                    currentSound    = playbackState.currentSoundTitle ?: lastSoundName,
                    currentCategory = selectedCategory,
                    isPlaying       = playbackState.isPlaying,
                    safeMode        = true
                )
            }

            // ── Quick deploy row ─────────────────────────────────────────
            item {
                QuickDeployPanel(
                    onDeploy = { cat ->
                        val acceptsGenerated = cat.equals("VOICE", true) || cat.equals("GENERATED", true)
                        val possible = soundsList.filter {
                            soundRepository.isSoundPlayable(it) &&
                                (it.category.equals(cat, ignoreCase = true) ||
                                    (acceptsGenerated && soundRepository.isGeneratedVoiceClip(it)))
                        }
                        val sound = possible.randomOrNull()
                        if (sound != null) {
                            val started = audioPlayerController.playPrankSound(sound)
                            addLog(if (started) "QUICK DEPLOY: ${sound.name}" else "REJECTED: ${sound.name}")
                        } else {
                            addLog("EMPTY: $cat")
                        }
                    }
                )
            }

            // ── Trace log ────────────────────────────────────────────────
            item {
                TraceLogPanel(
                    entries = traceLog.map { TraceEntry(it.title, it.time) }
                )
            }

            // ── Mode grid (Open Stash / Create Joke / Forge / etc.) ──────
            item { ModeGridSection(soundsList, onNavigate) }

            // ── Kill switch panel ────────────────────────────────────────
            item {
                KillAudioPanel(
                    isPlaying = playbackState.isPlaying,
                    onKill    = {
                        audioPlayerController.stopAll()
                        addLog("KILLSWITCH ACTIVATED")
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Data + helpers (unchanged from original HomeScreen.kt)
// ─────────────────────────────────────────────────────────────────────────────

data class LogData(val title: String, val time: String)

fun contextualTraceLog() = listOf(
    LogData("SYSTEM BOOT", "00:00:01"),
    LogData("CATALOG VALIDATED", "00:00:02")
)

@Composable
fun WaveformHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        val colors = listOf(PrimaryContainer, CyanAccent, CyanAccent, FuchsiaAccent, PrimaryContainer, CyanAccent)
        List(12) { index ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(((index % 4 + 1) * 10).dp)
                    .padding(horizontal = 1.dp)
                    .background(colors[index % colors.size])
            )
        }
    }
}

@Composable
fun QuickDeploySection(
    audioPlayerController: AudioPlayerController,
    soundsList: List<PrankSound>,
    onLog: (String) -> Unit
) {
    fun playRandom(category: String) {
        val possible = soundsList.filter { it.category.equals(category, ignoreCase = true) }
        val sound = possible.randomOrNull()
        if (sound != null) {
            val started = audioPlayerController.playPrankSound(sound)
            onLog(if (started) "QUICK DEPLOY: ${sound.name}" else "REJECTED: ${sound.name}")
        }
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            HeadlineText(text = "QUICK DEPLOY", color = OnBackground)
            LabelCaps(text = "DIRECT_TRIGGER", color = CyanAccent.copy(alpha = 0.5f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item { QuickAction(icon = Icons.Default.Air,             title = "FUNNY",   color = PrimaryContainer) { playRandom("FUNNY") } }
            item { QuickAction(icon = Icons.Default.DoorFront,       title = "CREEPY",  color = FuchsiaAccent)    { playRandom("CREEPY") } }
            item { QuickAction(icon = Icons.Default.BugReport,       title = "ANIMAL",  color = LimeAccent)       { playRandom("ANIMAL") } }
            item { QuickAction(icon = Icons.Default.SmartToy,        title = "VOICE",   color = CyanAccent)       { playRandom("VOICE") } }
            item { QuickAction(icon = Icons.Default.Cyclone,         title = "FIGHTER", color = OrangeAccent)     { playRandom("FIGHTER") } }
            item { QuickAction(icon = Icons.Default.AutoFixHigh,     title = "CARTOON", color = Color.Yellow)     { playRandom("CARTOON") } }
        }
    }
}

@Composable
fun QuickAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color = CyanAccent,
    onClick: () -> Unit = {}
) {
    GlassPanel(modifier = Modifier.size(80.dp).clickable { onClick() }) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = Color(0xFFBAC9CC), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun TraceLogSection(logs: List<LogData>) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            HeadlineText("TRACE LOG", color = OnBackground)
            LabelCaps(text = "SYSTEM_STATUS: OK", color = LimeAccent.copy(alpha = 0.5f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(logs) { log -> LogItem(log.title, log.time) }
        }
    }
}

@Composable
fun LogItem(title: String, time: String) {
    Box(
        modifier = Modifier
            .background(GlassBackground, CircleShape)
            .border(1.dp, Color.White.copy(0.05f), CircleShape)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyanAccent))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, color = OnBackground)
            Spacer(modifier = Modifier.width(8.dp))
            LabelCaps(time, color = OutlineDark)
        }
    }
}

@Composable
fun ModeGridSection(soundsList: List<PrankSound>, onNavigate: (String) -> Unit) {
    val totalSamples = if (soundsList.isNotEmpty()) "${soundsList.size} SAMPLES" else "LOADING..."
    val voiceClip = soundsList
        .filter { it.category.equals("VOICE_GENERATED", true) || it.packId.equals("voice_lab", true) }
        .maxByOrNull { it.createdAt }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            ModeCard("Open Stash",  totalSamples,          Icons.Default.LibraryMusic,        Modifier.weight(1f)) { onNavigate("library") }
            Spacer(modifier = Modifier.width(16.dp))
            ModeCard("Create Joke", voiceClip?.name ?: "VOICE LAB", Icons.Default.RecordVoiceOver, Modifier.weight(1f)) { onNavigate("voice_lab") }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            ModeCard("Sound Forge", "ADVANCED FX",  Icons.Default.PrecisionManufacturing, Modifier.weight(1f)) { onNavigate("forge") }
            Spacer(modifier = Modifier.width(16.dp))
            ModeCard("Randomizer",  "SECONDARY",    Icons.Default.Shuffle,                Modifier.weight(1f)) { onNavigate("randomizer") }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            ModeCard("Timer",       "SECONDARY",    Icons.Default.Timer,                  Modifier.weight(1f)) { onNavigate("timer") }
            Spacer(modifier = Modifier.width(16.dp))
            ModeCard("Packs",       "STASH FILTERS", Icons.Default.Inventory2,            Modifier.weight(1f)) { onNavigate("lab") }
        }
    }
}

@Composable
fun ModeCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassPanel(modifier = modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = CyanAccent)
            Spacer(modifier = Modifier.height(12.dp))
            HeadlineText(title, color = OnBackground)
            LabelCaps(subtitle, color = OutlineDark)
        }
    }
}

@Composable
fun KillSwitchSection(audioPlayerController: AudioPlayerController, onKill: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ErrorRed.copy(0.3f), RoundedCornerShape(16.dp))
            .border(2.dp, ErrorRed.copy(0.5f), RoundedCornerShape(16.dp))
            .clickable {
                audioPlayerController.stopAll()
                onKill()
            }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Dangerous, null, tint = ErrorRed)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                HeadlineText("STOP ALL EMISSIONS", color = ErrorRed)
                LabelCaps("KILLSWITCH_PROTOCOL", color = ErrorRed.copy(0.6f))
            }
        }
    }
}
