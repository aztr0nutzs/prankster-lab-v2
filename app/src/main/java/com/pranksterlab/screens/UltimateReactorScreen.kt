package com.pranksterlab.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.components.ScanlineOverlay
import com.pranksterlab.components.bot.PrankstarBotMood
import com.pranksterlab.components.bot.PrankstarBotVideo
import com.pranksterlab.components.home.PrankstarFxOverlay
import com.pranksterlab.components.home.PrankstarFloatingControls
import com.pranksterlab.components.reactor.ReactorCorePanel
import com.pranksterlab.components.reactor.ultimate.UltimatePrankType
import com.pranksterlab.components.reactor.ultimate.UltimateReactorBottomPanel
import com.pranksterlab.components.reactor.ultimate.UltimateReactorCanvas
import com.pranksterlab.components.reactor.ultimate.UltimateReactorSide
import com.pranksterlab.components.reactor.ultimate.UltimateReactorSideStrip
import com.pranksterlab.components.reactor.ultimate.UltimateReactorState
import com.pranksterlab.components.reactor.ultimate.UltimateStripAction
import com.pranksterlab.components.reactor.ultimate.accentColor
import com.pranksterlab.components.video.PrankstarHeaderVideo
import com.pranksterlab.components.video.PrankstarVideoBackground
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.repository.SoundRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.sin

private enum class HomeReactorMode(val label: String) {
    ULTIMATE("ULTIMATE"),
    CLASSIC("CLASSIC"),
    COMPACT("COMPACT"),
    VISUALIZER("VISUALIZER")
}

@Composable
fun UltimateReactorScreen(
    audioPlayerController: AudioPlayerController,
    soundRepository: SoundRepository,
    onNavigate: (String) -> Unit = {}
) {
    var soundsList by remember { mutableStateOf(emptyList<PrankSound>()) }
    var reactorState by remember { mutableStateOf(UltimateReactorState()) }
    var selectedMode by rememberSaveable { mutableStateOf(HomeReactorMode.ULTIMATE.name) }
    val homeMode = remember(selectedMode) { HomeReactorMode.valueOf(selectedMode) }
    val playbackState by audioPlayerController.playbackState.collectAsState()
    val animatedCharge by animateFloatAsState(reactorState.chargeLevel.coerceIn(0f, 1f), label = "ultimate-charge")

    LaunchedEffect(Unit) {
        val bundled = withContext(Dispatchers.IO) { soundRepository.getBundledSounds() }
        soundRepository.getCustomSoundsFlow().collect { custom ->
            soundsList = bundled + custom
        }
    }

    LaunchedEffect(playbackState.isPlaying, playbackState.currentSoundId, playbackState.lastError) {
        reactorState = reactorState.copy(
            isPlaying = playbackState.isPlaying,
            currentSoundId = playbackState.currentSoundId,
            currentSoundName = playbackState.currentSoundTitle ?: reactorState.currentSoundName,
            lastLogMessage = when {
                playbackState.lastError != null -> "AUDIO ERROR: ${playbackState.lastError}"
                playbackState.isPlaying -> "DEPLOYED: ${playbackState.currentSoundTitle ?: "UNKNOWN SIGNAL"}"
                else -> reactorState.lastLogMessage
            },
            alertMessage = if (playbackState.lastError != null) "AUDIO FAULT" else reactorState.alertMessage
        )
    }

    LaunchedEffect(reactorState.powered, reactorState.intensity) {
        while (isActive) {
            val base = if (reactorState.powered) 800 + reactorState.intensity * 2 else 120
            val battery = if (reactorState.powered) (55 + (reactorState.intensity % 35)).coerceIn(0, 100) else 8
            reactorState = reactorState.copy(tempCelsius = base + (0..22).random(), batteryPercent = battery)
            delay(1200)
        }
    }

    fun log(message: String, alert: String? = null) {
        reactorState = reactorState.copy(lastLogMessage = message, alertMessage = alert)
    }

    fun triggerDeploy() {
        if (!reactorState.powered) {
            log("POWER OFF: DEPLOY BLOCKED", "REACTOR OFFLINE")
            return
        }
        val categoryHints = when (reactorState.prankType) {
            UltimatePrankType.SPLASH -> setOf("FUNNY", "CARTOON")
            UltimatePrankType.SOUND -> setOf("VOICE", "VOICE_GENERATED", "FUNNY")
            UltimatePrankType.SMOKE -> setOf("CREEPY", "ANIMAL")
            UltimatePrankType.ZAP -> setOf("FIGHTER", "CARTOON")
        }
        val safeCandidates = soundsList.filter {
            soundRepository.isSoundPlayable(it) &&
                it.isSafeForRandomMode &&
                it.intensityLevel <= (reactorState.intensity / 25).coerceAtLeast(1) + 1 &&
                (it.category.uppercase() in categoryHints || (reactorState.prankType == UltimatePrankType.SOUND && soundRepository.isGeneratedVoiceClip(it)))
        }
        val fallback = soundsList.filter {
            soundRepository.isSoundPlayable(it) &&
                (it.category.uppercase() in categoryHints || (reactorState.prankType == UltimatePrankType.SOUND && soundRepository.isGeneratedVoiceClip(it)))
        }
        val anyPlayable = soundsList.filter { soundRepository.isSoundPlayable(it) }
        val sound = (safeCandidates.ifEmpty { fallback }.ifEmpty { anyPlayable }).randomOrNull()
        if (sound == null) {
            log("NO PLAYABLE SOUND FOUND", "EMPTY SIGNAL")
            return
        }
        val started = audioPlayerController.playPrankSound(sound)
        val nextCount = reactorState.prankCount + 1
        reactorState = reactorState.copy(
            prankCount = nextCount,
            chargeLevel = (reactorState.chargeLevel + 0.3f).coerceAtMost(1f),
            currentSoundName = if (started) sound.name else reactorState.currentSoundName,
            currentSoundId = if (started) sound.id else reactorState.currentSoundId,
            isOverloaded = reactorState.chargeLevel > 0.82f || reactorState.prankType == UltimatePrankType.ZAP,
            lastLogMessage = if (started) "PRANK #${nextCount.toString().padStart(3, '0')} [${homeMode.label}] DEPLOYED - ${sound.name}" else "REJECTED: ${sound.name}",
            alertMessage = if (started) "PRANK DEPLOYED" else "PLAYBACK REJECTED"
        )
    }

    fun stopAll() {
        audioPlayerController.stopAll()
        log("KILLSWITCH ACTIVATED", "AUDIO STOPPED")
    }

    fun togglePower() {
        val nextPowered = !reactorState.powered
        if (!nextPowered) audioPlayerController.stopAll()
        reactorState = reactorState.copy(
            powered = nextPowered,
            isPlaying = if (nextPowered) reactorState.isPlaying else false,
            chargeLevel = if (nextPowered) reactorState.chargeLevel else 0f,
            isOverloaded = false,
            lastLogMessage = if (nextPowered) "REACTOR POWER ONLINE" else "REACTOR POWER OFFLINE",
            alertMessage = if (nextPowered) "ONLINE" else "OFFLINE"
        )
    }

    LaunchedEffect(reactorState.alertMessage) {
        if (reactorState.alertMessage != null) {
            delay(1300)
            reactorState = reactorState.copy(alertMessage = null, isOverloaded = false)
        }
    }

    LaunchedEffect(reactorState.chargeLevel) {
        if (reactorState.chargeLevel > 0f) {
            delay(900)
            reactorState = reactorState.copy(chargeLevel = (reactorState.chargeLevel - 0.08f).coerceAtLeast(0f))
        }
    }

    val botMood = when {
        !reactorState.powered -> PrankstarBotMood.SHUTDOWN
        reactorState.isOverloaded -> PrankstarBotMood.WARNING
        playbackState.lastError != null -> PrankstarBotMood.CONFUSED
        playbackState.isPlaying -> PrankstarBotMood.PLAYING
        reactorState.mischiefAiEnabled -> PrankstarBotMood.HAPPY
        else -> PrankstarBotMood.ARMED
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
        PrankstarVideoBackground()
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.46f),
                            Color(0xFF041326).copy(alpha = 0.38f),
                            Color.Black.copy(alpha = 0.52f)
                        )
                    )
                )
        )
        ScanlineOverlay()

        Column(Modifier.fillMaxSize()) {
            PrankstarHeaderVideo(height = 88.dp)
            ReactorModeChooser(
                selectedMode = homeMode,
                onModeSelected = { selectedMode = it.name },
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                StarfieldBackdrop(alpha = if (homeMode == HomeReactorMode.ULTIMATE) 0.18f else 0.10f)
                when (homeMode) {
                    HomeReactorMode.ULTIMATE -> UltimateStage(
                        state = reactorState,
                        animatedCharge = animatedCharge,
                        sampleCount = soundsList.size,
                        isPlaying = playbackState.isPlaying,
                        onStateChange = { reactorState = it },
                        onNavigate = onNavigate,
                        onDeploy = ::triggerDeploy,
                        onStopAll = ::stopAll,
                        onPowerToggle = ::togglePower
                    )
                    HomeReactorMode.CLASSIC -> ClassicStage(
                        state = reactorState,
                        sampleCount = soundsList.size,
                        playbackError = playbackState.lastError,
                        onDeploy = ::triggerDeploy,
                        onStopAll = ::stopAll,
                        onOpenStash = { onNavigate("library") },
                        onOpenJokes = { onNavigate("voice_lab") },
                        onOpenForge = { onNavigate("forge") }
                    )
                    HomeReactorMode.COMPACT -> CompactStage(
                        state = reactorState,
                        sampleCount = soundsList.size,
                        isPlaying = playbackState.isPlaying,
                        onDeploy = ::triggerDeploy,
                        onStopAll = ::stopAll,
                        onOpenStash = { onNavigate("library") },
                        onOpenJokes = { onNavigate("voice_lab") },
                        onOpenForge = { onNavigate("forge") }
                    )
                    HomeReactorMode.VISUALIZER -> VisualizerStage(
                        state = reactorState,
                        sampleCount = soundsList.size,
                        isPlaying = playbackState.isPlaying,
                        onDeploy = ::triggerDeploy,
                        onStopAll = ::stopAll,
                        onOpenStash = { onNavigate("library") },
                        onOpenJokes = { onNavigate("voice_lab") },
                        onOpenForge = { onNavigate("forge") }
                    )
                }
            }
            UltimateReactorBottomPanel(
                state = reactorState,
                onStateChange = { reactorState = it },
                onDeploy = ::triggerDeploy,
                onPowerToggle = ::togglePower
            )
        }

        PrankstarFxOverlay(
            active = reactorState.isPlaying || reactorState.chargeLevel > 0.05f || reactorState.isOverloaded,
            modifier = Modifier.fillMaxSize()
        )
        PrankstarBotVideo(
            mood = botMood,
            message = when {
                playbackState.isPlaying -> "Deploying chaos: ${reactorState.currentSoundName ?: "live signal"}."
                playbackState.lastError != null -> "Audio fault detected."
                !reactorState.powered -> "Core offline."
                else -> "Ready to deploy chaos."
            },
            compact = true,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 138.dp, end = 10.dp)
                .width(132.dp),
            onTap = { reactorState = reactorState.copy(mischiefAiEnabled = !reactorState.mischiefAiEnabled) }
        )
    }
}

@Composable
private fun ReactorModeChooser(
    selectedMode: HomeReactorMode,
    onModeSelected: (HomeReactorMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "REACTOR MODE",
            color = Color(0xFF7FE8FF),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 1.4.sp, fontWeight = FontWeight.Black),
            modifier = Modifier.width(74.dp)
        )
        HomeReactorMode.values().forEach { mode ->
            val selected = selectedMode == mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(if (selected) Color(0xFF003A44).copy(alpha = 0.82f) else Color.Black.copy(alpha = 0.42f))
                    .border(
                        1.dp,
                        if (selected) Color(0xFFFF00CC).copy(alpha = 0.9f) else Color(0xFF00E8FF).copy(alpha = 0.58f),
                        RoundedCornerShape(15.dp)
                    )
                    .clickable { onModeSelected(mode) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    mode.label,
                    color = if (selected) Color.White else Color(0xFF9EDFFF),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Black),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun UltimateStage(
    state: UltimateReactorState,
    animatedCharge: Float,
    sampleCount: Int,
    isPlaying: Boolean,
    onStateChange: (UltimateReactorState) -> Unit,
    onNavigate: (String) -> Unit,
    onDeploy: () -> Unit,
    onStopAll: () -> Unit,
    onPowerToggle: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxWidth(0.84f)
                .alpha(if (state.powered) 1f else 0.48f),
            contentAlignment = Alignment.Center
        ) {
            UltimateReactorCanvas(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                onTap = {
                    onStateChange(state.copy(chargeLevel = (state.chargeLevel + 0.18f).coerceAtMost(1f), lastLogMessage = "CORE TOUCH REGISTERED"))
                    onDeploy()
                }
            )
        }
        LinearCharge(animatedCharge, Modifier.align(Alignment.TopCenter).padding(top = 4.dp).width(210.dp))
        IconButton(
            onClick = onPowerToggle,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 36.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFF160404))
                .border(2.dp, if (state.powered) Color(0xFFFF2200) else Color(0xFF440000), CircleShape)
        ) {
            Icon(Icons.Default.PowerSettingsNew, contentDescription = "Power", tint = Color(0xFFFF2200))
        }
        UltimateReactorSideStrip(
            side = UltimateReactorSide.LEFT,
            state = state,
            modifier = Modifier.align(Alignment.CenterStart),
            onAction = { action ->
                when (action) {
                    UltimateStripAction.AUDIO -> onStateChange(state.copy(audioModEnabled = !state.audioModEnabled, lastLogMessage = "AUDIO MOD TOGGLED", alertMessage = "AUDIO MOD"))
                    UltimateStripAction.GEAR -> onNavigate("system")
                    UltimateStripAction.SPRING -> onStateChange(state.copy(chargeLevel = (state.chargeLevel + 0.22f).coerceAtMost(1f), lastLogMessage = "SPRING CHARGE LOADED", alertMessage = "SPRING LOADED"))
                    else -> Unit
                }
            }
        )
        UltimateReactorSideStrip(
            side = UltimateReactorSide.RIGHT,
            state = state,
            modifier = Modifier.align(Alignment.CenterEnd),
            onAction = { action ->
                when (action) {
                    UltimateStripAction.HOLO -> onStateChange(state.copy(holoProjectorEnabled = !state.holoProjectorEnabled, lastLogMessage = "HOLO TARGET ${if (!state.holoProjectorEnabled) "ACQUIRED" else "STANDBY"}", alertMessage = "HOLO TARGET"))
                    UltimateStripAction.ZAP -> {
                        onStateChange(state.copy(prankType = UltimatePrankType.ZAP, isOverloaded = true, alertMessage = "PRANK OVERLOAD"))
                        onDeploy()
                    }
                    UltimateStripAction.AI -> {
                        onStateChange(state.copy(mischiefAiEnabled = !state.mischiefAiEnabled, lastLogMessage = "MISCHIEF AI TOGGLED", alertMessage = "MISCHIEF AI"))
                        if (state.mischiefAiEnabled) onNavigate("voice_lab")
                    }
                    else -> Unit
                }
            }
        )
        ReactorReadout(
            state = state,
            sampleCount = sampleCount,
            modeLabel = "ULTIMATE",
            modifier = Modifier.align(Alignment.BottomCenter).padding(start = 48.dp, end = 48.dp, bottom = 4.dp)
        )
        PrankstarFloatingControls(
            isPlaying = isPlaying,
            onOpenStash = { onNavigate("library") },
            onOpenJokes = { onNavigate("voice_lab") },
            onOpenForge = { onNavigate("forge") },
            onStopAll = onStopAll,
            onDeploy = onDeploy,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp)
        )
    }
}

@Composable
private fun ClassicStage(
    state: UltimateReactorState,
    sampleCount: Int,
    playbackError: String?,
    onDeploy: () -> Unit,
    onStopAll: () -> Unit,
    onOpenStash: () -> Unit,
    onOpenJokes: () -> Unit,
    onOpenForge: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ReactorCorePanel(
            currentSoundName = state.currentSoundName,
            currentCategory = state.prankType.name,
            isPlaying = state.isPlaying,
            hasCustomSounds = true,
            playbackError = playbackError,
            loadedSoundCount = sampleCount,
            safeSoundCount = sampleCount,
            onTrigger = { _, _ -> onDeploy() },
            onStop = onStopAll,
            onCategoryChange = {},
            onOpenStash = onOpenStash,
            onOpenJokes = onOpenJokes,
            onOpenForge = onOpenForge,
            modifier = Modifier.fillMaxWidth(0.82f)
        )
        ReactorReadout(state, sampleCount, "CLASSIC", Modifier.align(Alignment.BottomCenter).padding(start = 44.dp, end = 44.dp, bottom = 8.dp))
    }
}

@Composable
private fun CompactStage(
    state: UltimateReactorState,
    sampleCount: Int,
    isPlaying: Boolean,
    onDeploy: () -> Unit,
    onStopAll: () -> Unit,
    onOpenStash: () -> Unit,
    onOpenJokes: () -> Unit,
    onOpenForge: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .fillMaxWidth(0.72f)
                .heightIn(max = 330.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.Black.copy(alpha = 0.42f))
                .border(1.dp, Color(0xFF66FF00).copy(alpha = 0.56f), RoundedCornerShape(22.dp))
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("COMPACT CHAOS CORE", color = Color(0xFF66FF00), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp))
            UltimateReactorCanvas(state, Modifier.fillMaxWidth(0.78f), onTap = { onDeploy() })
            ReactorReadout(state, sampleCount, "COMPACT")
        }
        PrankstarFloatingControls(isPlaying, onOpenStash, onOpenJokes, onOpenForge, onStopAll, onDeploy, Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp))
    }
}

@Composable
private fun VisualizerStage(
    state: UltimateReactorState,
    sampleCount: Int,
    isPlaying: Boolean,
    onDeploy: () -> Unit,
    onStopAll: () -> Unit,
    onOpenStash: () -> Unit,
    onOpenJokes: () -> Unit,
    onOpenForge: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        VuStrip(Modifier.align(Alignment.CenterStart).padding(start = 6.dp).fillMaxHeight(0.86f).width(36.dp), isPlaying, Color(0xFF00E8FF))
        VuStrip(Modifier.align(Alignment.CenterEnd).padding(end = 148.dp).fillMaxHeight(0.86f).width(36.dp), isPlaying, Color(0xFFFF00CC))
        VisualizerCore(state, Modifier.fillMaxWidth(0.78f).aspectRatio(1f).clickable { onDeploy() })
        ReactorReadout(state, sampleCount, "VISUALIZER", Modifier.align(Alignment.BottomCenter).padding(start = 42.dp, end = 154.dp, bottom = 8.dp))
        PrankstarFloatingControls(isPlaying, onOpenStash, onOpenJokes, onOpenForge, onStopAll, onDeploy, Modifier.align(Alignment.BottomCenter).padding(end = 136.dp, bottom = 46.dp))
    }
}

@Composable
private fun LinearCharge(animatedCharge: Float, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LinearProgressIndicator(
            progress = { animatedCharge },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF66FF00),
            trackColor = Color(0xFF001020)
        )
        Text("MISCHIEF CHARGE", color = Color(0xFF7BD17B), style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 2.sp))
    }
}

@Composable
private fun ReactorReadout(
    state: UltimateReactorState,
    sampleCount: Int,
    modeLabel: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.58f))
            .border(1.dp, state.mode.accentColor().copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(if (state.powered) Color(0xFF66FF00) else Color(0xFF555555)))
        Column(Modifier.weight(1f)) {
            Text(
                state.currentSoundName ?: "READY // $sampleCount SAMPLES",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "$modeLabel CORE // ${if (state.isPlaying) "PLAYING" else state.mode.name}",
                color = Color(0xFF7FE8FF),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 0.8.sp)
            )
        }
    }
}

@Composable
private fun StarfieldBackdrop(alpha: Float) {
    Box(Modifier.fillMaxSize().background(Color(0x2200E8FF).copy(alpha = alpha)))
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.SpaceEvenly) {
        repeat(9) { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                repeat(7) { col ->
                    val dotAlpha = (((row + 1) * (col + 3)) % 7) / 18f + 0.08f
                    Box(Modifier.size(1.dp).background(Color(0xFF8ADFFF).copy(alpha = dotAlpha)))
                }
            }
        }
    }
}

@Composable
private fun VuStrip(modifier: Modifier, active: Boolean, color: Color) {
    val transition = rememberInfiniteTransition(label = "home-vu-strip")
    val phase by transition.animateFloat(0f, 6.28f, infiniteRepeatable(tween(if (active) 520 else 1600), RepeatMode.Restart), label = "vu-phase")
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.36f))
            .border(1.dp, color.copy(alpha = 0.42f), RoundedCornerShape(18.dp))
            .padding(6.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(12) { index ->
            val level = if (active) 0.25f + ((sin(phase + index * 0.8f) + 1f) * 0.34f) else 0.18f + index * 0.015f
            Box(
                Modifier
                    .fillMaxWidth(level.coerceIn(0.12f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color.copy(alpha = 0.35f + level * 0.55f))
            )
        }
    }
}

@Composable
private fun VisualizerCore(state: UltimateReactorState, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "visualizer-core")
    val phase by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(if (state.isPlaying) 680 else 1800), RepeatMode.Restart), label = "visualizer-phase")
    Canvas(modifier) {
        val center = this.center
        val maxRadius = size.minDimension * 0.42f
        drawCircle(Brush.radialGradient(listOf(Color(0xFF00E8FF).copy(alpha = 0.28f), Color.Transparent), center, maxRadius), maxRadius, center)
        repeat(7) { index ->
            val radius = maxRadius * (0.22f + index * 0.115f + phase * 0.07f)
            val color = listOf(Color(0xFF00E8FF), Color(0xFFFF00CC), Color(0xFF66FF00), Color(0xFFFFAA00))[index % 4]
            drawCircle(color.copy(alpha = 0.70f - index * 0.07f), radius, center, style = Stroke((2 + index % 3).dp.toPx()))
        }
        drawCircle(Color.Black.copy(alpha = 0.62f), maxRadius * 0.28f, center)
        drawCircle(Color(0xFFFF00CC).copy(alpha = if (state.isPlaying) 0.9f else 0.48f), maxRadius * (0.12f + phase * 0.05f), center)
    }
}
