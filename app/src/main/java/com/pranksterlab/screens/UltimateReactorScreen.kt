package com.pranksterlab.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.components.ScanlineOverlay
import com.pranksterlab.components.bot.PrankstarBotMood
import com.pranksterlab.components.bot.PrankstarBotVideo
import com.pranksterlab.components.reactor.ultimate.UltimatePrankType
import com.pranksterlab.components.reactor.ultimate.UltimateReactorBottomPanel
import com.pranksterlab.components.reactor.ultimate.UltimateReactorCanvas
import com.pranksterlab.components.reactor.ultimate.UltimateReactorSide
import com.pranksterlab.components.reactor.ultimate.UltimateReactorSideStrip
import com.pranksterlab.components.reactor.ultimate.UltimateReactorState
import com.pranksterlab.components.reactor.ultimate.UltimateReactorTopBar
import com.pranksterlab.components.reactor.ultimate.UltimateStripAction
import com.pranksterlab.components.reactor.ultimate.accentColor
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.repository.SoundRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

@Composable
fun UltimateReactorScreen(
    audioPlayerController: AudioPlayerController,
    soundRepository: SoundRepository,
    onNavigate: (String) -> Unit = {}
) {
    var soundsList by remember { mutableStateOf(emptyList<PrankSound>()) }
    var reactorState by remember { mutableStateOf(UltimateReactorState()) }
    var showBot by remember { mutableStateOf(false) }
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
        val sound = (safeCandidates.ifEmpty { fallback }).randomOrNull()
        if (sound == null) {
            log("NO PLAYABLE ${reactorState.prankType.name} SOUND FOUND", "EMPTY SIGNAL")
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
            lastLogMessage = if (started) "PRANK #${nextCount.toString().padStart(3, '0')} [${reactorState.prankType.name}] DEPLOYED - ${sound.name}" else "REJECTED: ${sound.name}",
            alertMessage = if (started) "PRANK DEPLOYED" else "PLAYBACK REJECTED"
        )
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
        playbackState.lastError != null -> PrankstarBotMood.ERROR
        playbackState.isPlaying -> PrankstarBotMood.PLAYING
        reactorState.mischiefAiEnabled -> PrankstarBotMood.HAPPY
        else -> PrankstarBotMood.ARMED
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xFF08203A), Color(0xFF040810), Color.Black)))
    ) {
        ScanlineOverlay()
        Column(Modifier.fillMaxSize()) {
            UltimateReactorTopBar(reactorState)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                StarfieldBackdrop()
                Box(
                    Modifier
                        .fillMaxWidth(0.86f)
                        .alpha(if (reactorState.powered) 1f else 0.48f),
                    contentAlignment = Alignment.Center
                ) {
                    UltimateReactorCanvas(
                        state = reactorState,
                        modifier = Modifier.fillMaxWidth(),
                        onTap = {
                            reactorState = reactorState.copy(chargeLevel = (reactorState.chargeLevel + 0.18f).coerceAtMost(1f), lastLogMessage = "CORE TOUCH REGISTERED")
                            triggerDeploy()
                        }
                    )
                }
                Column(
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 6.dp)
                        .width(220.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { animatedCharge },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF66FF00),
                        trackColor = Color(0xFF001020)
                    )
                    Text("MISCHIEF CHARGE", color = Color(0xFF4A8A4A), style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 2.sp))
                }

                if (reactorState.alertMessage != null) {
                    Box(
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 38.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xF5140000))
                            .border(1.dp, if (reactorState.isOverloaded) Color(0xFFFF2200) else Color(0xFF00E8FF), RoundedCornerShape(5.dp))
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text(
                            reactorState.alertMessage ?: "",
                            color = if (reactorState.isOverloaded) Color(0xFFFF2200) else Color(0xFF00E8FF),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Black)
                        )
                    }
                }

                IconButton(
                    onClick = ::togglePower,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF160404))
                        .border(2.dp, if (reactorState.powered) Color(0xFFFF2200) else Color(0xFF440000), CircleShape)
                ) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = "Power", tint = Color(0xFFFF2200))
                }

                UltimateReactorSideStrip(
                    side = UltimateReactorSide.LEFT,
                    state = reactorState,
                    modifier = Modifier.align(Alignment.CenterStart),
                    onAction = { action ->
                        when (action) {
                            UltimateStripAction.AUDIO -> reactorState = reactorState.copy(audioModEnabled = !reactorState.audioModEnabled, lastLogMessage = "AUDIO MOD TOGGLED", alertMessage = "AUDIO MOD")
                            UltimateStripAction.GEAR -> onNavigate("system")
                            UltimateStripAction.SPRING -> reactorState = reactorState.copy(chargeLevel = (reactorState.chargeLevel + 0.22f).coerceAtMost(1f), lastLogMessage = "SPRING CHARGE LOADED", alertMessage = "SPRING LOADED")
                            else -> Unit
                        }
                    }
                )
                UltimateReactorSideStrip(
                    side = UltimateReactorSide.RIGHT,
                    state = reactorState,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onAction = { action ->
                        when (action) {
                            UltimateStripAction.HOLO -> reactorState = reactorState.copy(holoProjectorEnabled = !reactorState.holoProjectorEnabled, lastLogMessage = "HOLO TARGET ${if (!reactorState.holoProjectorEnabled) "ACQUIRED" else "STANDBY"}", alertMessage = "HOLO TARGET")
                            UltimateStripAction.ZAP -> {
                                reactorState = reactorState.copy(prankType = UltimatePrankType.ZAP, isOverloaded = true, alertMessage = "PRANK OVERLOAD")
                                triggerDeploy()
                            }
                            UltimateStripAction.AI -> {
                                reactorState = reactorState.copy(mischiefAiEnabled = !reactorState.mischiefAiEnabled, lastLogMessage = "MISCHIEF AI TOGGLED", alertMessage = "MISCHIEF AI")
                                if (reactorState.mischiefAiEnabled) onNavigate("voice_lab")
                            }
                            else -> Unit
                        }
                    }
                )

                ReactorReadout(
                    state = reactorState,
                    sampleCount = soundsList.size,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(start = 48.dp, end = 48.dp, bottom = 4.dp),
                    onBotToggle = { showBot = !showBot }
                )
            }
            UltimateReactorBottomPanel(
                state = reactorState,
                onStateChange = { reactorState = it },
                onDeploy = ::triggerDeploy,
                onPowerToggle = ::togglePower
            )
        }
        if (showBot) {
            PrankstarBotVideo(
                mood = botMood,
                message = "NEO: ${reactorState.lastLogMessage}",
                compact = true,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 46.dp, start = 24.dp, end = 24.dp).fillMaxWidth(),
                onTap = { showBot = false }
            )
        }
    }
}

@Composable
private fun ReactorReadout(
    state: UltimateReactorState,
    sampleCount: Int,
    modifier: Modifier = Modifier,
    onBotToggle: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.58f))
            .border(1.dp, state.mode.accentColor().copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .clickable(onClick = onBotToggle)
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
                "BOT PANEL TAP // ${if (state.isPlaying) "PLAYING" else state.mode.name}",
                color = Color(0xFF3A7AAA),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp, letterSpacing = 0.8.sp)
            )
        }
    }
}

@Composable
private fun StarfieldBackdrop() {
    Box(Modifier.fillMaxSize().background(Color(0x2200E8FF)))
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.SpaceEvenly) {
        repeat(9) { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                repeat(7) { col ->
                    val alpha = (((row + 1) * (col + 3)) % 7) / 18f + 0.08f
                    Box(Modifier.size(1.dp).background(Color(0xFF8ADFFF).copy(alpha = alpha)))
                }
            }
        }
    }
}
