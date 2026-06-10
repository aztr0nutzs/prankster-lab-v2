package com.pranksterlab.screens.voice

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pranksterlab.BuildConfig
import com.pranksterlab.R
import com.pranksterlab.components.PrankstarHeader
import com.pranksterlab.components.ScanlineOverlay
import com.pranksterlab.components.bot.PrankstarBotMood
import com.pranksterlab.components.bot.PrankstarBotPanel
import com.pranksterlab.components.bot.PrankstarBotVideo
import com.pranksterlab.components.twak.TwakAttackHeader
import com.pranksterlab.components.twak.TwakBotVideo
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.billing.FeatureGate
import com.pranksterlab.core.bot.PrankstarBotAction
import com.pranksterlab.core.bot.PrankstarBotController
import com.pranksterlab.core.bot.PrankstarBotMessage
import com.pranksterlab.core.bot.PrankstarBotState
import com.pranksterlab.core.bot.PrankstarBotVoiceLabBridge
import com.pranksterlab.core.elevenlabs.ElevenLabsTtsService
import com.pranksterlab.core.elevenlabs.TWAK_ATTACKS_FEATURE
import com.pranksterlab.core.elevenlabs.TWEAKER_GEOGRAPHIC_VOICE_ID
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.narration.TweakerGeographicNarrator
import com.pranksterlab.core.narration.TweakerGeographicRequest
import com.pranksterlab.core.narration.TweakerGeographicResult
import com.pranksterlab.core.narration.TweakerGeographicTone
import com.pranksterlab.core.narration.TwakBotMood
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.core.voice.AndroidTextToSpeechEngine
import com.pranksterlab.core.voice.DebugElevenLabsDirectProvider
import com.pranksterlab.core.voice.GeneratedVoiceRepository
import com.pranksterlab.core.voice.LocalAndroidTtsVoiceProvider
import com.pranksterlab.core.voice.NarrationVoiceProvider
import com.pranksterlab.core.voice.NarrationVoiceResult
import com.pranksterlab.core.voice.ProductionBackendVoiceProvider
import com.pranksterlab.core.voice.VoiceCategory
import com.pranksterlab.core.voice.VoiceEngineReadiness
import com.pranksterlab.core.voice.VoiceGenerationMode
import com.pranksterlab.core.voice.VoiceGeneratorSettings
import com.pranksterlab.core.voice.VoicePreset
import com.pranksterlab.core.voice.VoicePresetLibrary
import com.pranksterlab.core.voice.VoiceSynthesisResult
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.GlassBackground
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrangeAccent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

private class ManagedPreviewPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun play(
        file: File,
        onStarted: () -> Unit,
        onError: (String) -> Unit,
        onComplete: () -> Unit
    ): Boolean {
        stop()
        if (!file.exists() || file.length() <= 0L) {
            onError("Preview source is missing or empty.")
            return false
        }
        return try {
            val player = MediaPlayer()
            mediaPlayer = player
            player.setDataSource(file.absolutePath)
            player.setOnPreparedListener { prepared ->
                if (mediaPlayer !== prepared) return@setOnPreparedListener
                try {
                    prepared.start()
                    onStarted()
                } catch (t: Throwable) {
                    stop()
                    onError(t.message ?: "Unable to start preview playback.")
                }
            }
            player.setOnCompletionListener {
                stop()
                onComplete()
            }
            player.setOnErrorListener { _, what, extra ->
                stop()
                onError("Preview playback failed ($what/$extra).")
                true
            }
            player.prepareAsync()
            true
        } catch (t: Throwable) {
            stop()
            onError(t.message ?: "Unable to preview generated audio.")
            false
        }
    }

    fun stop() {
        val player = mediaPlayer ?: return
        mediaPlayer = null
        releasePlayer(player)
    }

    private fun releasePlayer(player: MediaPlayer) {
        try { if (player.isPlaying) player.stop() } catch (_: Throwable) {}
        try { player.reset() } catch (_: Throwable) {}
        try { player.release() } catch (_: Throwable) {}
    }
}

private enum class VoiceSourceMode { LOCAL_ANDROID_TTS, TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR }

@Composable
fun VoiceJokeGeneratorScreen(
    soundRepository: SoundRepository,
    audioPlayerController: AudioPlayerController,
    onNavigate: (String) -> Unit,
    featureGate: FeatureGate = FeatureGate.unconfiguredFree()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tts = remember { AndroidTextToSpeechEngine(context) }
    val narrationHttpClient = remember {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    val elevenLabsTtsService = remember(narrationHttpClient) {
        ElevenLabsTtsService(
            client = narrationHttpClient,
            apiKeyProvider = { BuildConfig.ELEVENLABS_API_KEY }
        )
    }
    val narrationOutputDir = remember(context) { File(context.filesDir, "generated/elevenlabs") }
    val voiceGenerationMode = remember { VoiceGenerationMode.fromBuildConfig(BuildConfig.VOICE_GENERATION_MODE) }
    val generatedRepo = remember { GeneratedVoiceRepository(soundRepository) }
    val allPresets = VoicePresetLibrary.presets
    val ttsReadiness by tts.readiness.collectAsState()
    val previewPlayer = remember { ManagedPreviewPlayer() }
    val botController = remember(featureGate) { PrankstarBotController(featureGate = featureGate) }
    val tweakographicNarrator = remember { TweakerGeographicNarrator() }
    val pendingBotDraft by PrankstarBotVoiceLabBridge.pendingDraft.collectAsState()
    val customSounds by soundRepository.getCustomSoundsFlow().collectAsState(initial = emptyList())

    var preset by remember { mutableStateOf(allPresets.first()) }
    var selectedCategory by remember { mutableStateOf<VoiceCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var outputName by remember { mutableStateOf("") }
    var pitch by remember { mutableStateOf(preset.pitch) }
    var speed by remember { mutableStateOf(preset.speechRate) }
    var volume by remember { mutableStateOf(preset.volume) }
    var effect by remember { mutableStateOf(0.2f) }
    var echo by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("INITIALIZING VOICE ENGINE") }
    var statusDetail by remember { mutableStateOf("Preparing Android TextToSpeech.") }
    var generatedFile by remember { mutableStateOf<File?>(null) }
    var generatedResult by remember { mutableStateOf<VoiceSynthesisResult?>(null) }
    var savedGeneratedFilePath by remember { mutableStateOf<String?>(null) }
    var bundledSounds by remember { mutableStateOf(emptyList<PrankSound>()) }
    var botAgentState by remember { mutableStateOf(PrankstarBotState()) }
    var fieldAction by remember { mutableStateOf("") }
    var fieldSetting by remember { mutableStateOf("") }
    var fieldTone by remember { mutableStateOf(TweakerGeographicTone.BALANCED) }
    var fieldIncludeSoundCue by remember { mutableStateOf(false) }
    var fieldResult by remember { mutableStateOf<TweakerGeographicResult?>(null) }
    var voiceSourceMode by remember { mutableStateOf(VoiceSourceMode.LOCAL_ANDROID_TTS) }
    var generatedTweakerNarrationTitle by remember { mutableStateOf<String?>(null) }
    var generatedTweakerNarrationText by remember { mutableStateOf<String?>(null) }
    var twakBotMood by remember { mutableStateOf(TwakBotMood.IDLE) }
    var twakTextGenerationCount by remember { mutableStateOf(0) }

    fun applyPreset(selected: VoicePreset) {
        preset = selected
        pitch = selected.pitch
        speed = selected.speechRate
        volume = selected.volume
    }

    fun settings() = VoiceGeneratorSettings(preset, text, pitch, speed, volume, preset.toneStyle, effect, echo, outputName)
    fun isValidGeneratedFile(file: File?) = file != null && file.exists() && file.length() > 0L
    fun narrationProvider(): NarrationVoiceProvider {
        return when (voiceGenerationMode) {
            VoiceGenerationMode.LOCAL_ONLY -> LocalAndroidTtsVoiceProvider()
            VoiceGenerationMode.DEBUG_ELEVENLABS_DIRECT -> DebugElevenLabsDirectProvider(narrationOutputDir, elevenLabsTtsService)
            VoiceGenerationMode.PRODUCTION_BACKEND -> ProductionBackendVoiceProvider(
                client = narrationHttpClient,
                backendBaseUrlProvider = { BuildConfig.VOICE_BACKEND_BASE_URL },
                authTokenProvider = { null },
                outputDirectory = narrationOutputDir,
                toneProvider = { fieldTone.name }
            )
        }
    }

    val filteredPresets = allPresets.filter {
        (selectedCategory == null || it.category == selectedCategory) &&
            (searchQuery.isBlank() || it.displayName.contains(searchQuery, true) || it.description.contains(searchQuery, true))
    }

    val botPlayableSounds by produceState(initialValue = emptyList<PrankSound>(), bundledSounds, customSounds) {
        value = withContext(Dispatchers.IO) {
            (bundledSounds + customSounds).filter { soundRepository.isSoundPlayable(it) }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            previewPlayer.stop()
            tts.stopPreview()
            tts.release()
        }
    }

    LaunchedEffect(soundRepository) {
        bundledSounds = withContext(Dispatchers.IO) { soundRepository.getBundledSounds() }
    }

    LaunchedEffect(pendingBotDraft) {
        val draft = pendingBotDraft ?: return@LaunchedEffect
        text = draft.text.take(300)
        draft.suggestedVoicePresetId?.let { presetId ->
            allPresets.firstOrNull { it.id == presetId }?.let { applyPreset(it) }
        }
        if (draft.preferBritishNarrator) {
            fieldAction = draft.text.take(120)
            fieldResult = TweakerGeographicResult(
                title = "Tweaker Geographic",
                narration = draft.text,
                tone = TweakerGeographicTone.BALANCED,
                suggestedVoicePresetId = draft.suggestedVoicePresetId ?: "overly_serious_narrator",
                suggestedSoundQuery = null,
                safetyNote = null
            )
            outputName = "Tweaker Geographic"
            voiceSourceMode = VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR
            status = "BOT FIELD REPORT LOADED"
            statusDetail = "Bot filled Tweaker Geographic narration. Review it, then tap Generate British Narration."
            twakBotMood = TwakBotMood.EXCITED
        } else {
            status = "BOT DRAFT LOADED"
            statusDetail = "Prankstar Bot filled the line. Review it, then tap Generate when ready."
        }
        PrankstarBotVoiceLabBridge.consume()
    }

    LaunchedEffect(ttsReadiness) {
        when (val readiness = ttsReadiness) {
            VoiceEngineReadiness.INITIALIZING -> {
                status = "INITIALIZING VOICE ENGINE"
                statusDetail = "Preparing Android TextToSpeech."
            }
            VoiceEngineReadiness.READY -> {
                if (status == "INITIALIZING VOICE ENGINE" || status == "ERROR") {
                    status = "READY"
                    statusDetail = "Voice engine is ready."
                }
            }
            VoiceEngineReadiness.UNAVAILABLE -> {
                status = "ERROR"
                statusDetail = "TextToSpeech engine is unavailable on this device."
            }
            is VoiceEngineReadiness.ERROR -> {
                status = "ERROR"
                statusDetail = readiness.message
            }
        }
    }

    val generatedVoiceClipCount = customSounds.count { soundRepository.isGeneratedVoiceClip(it) }
    val debugDirectNarrationEnabled = voiceGenerationMode == VoiceGenerationMode.DEBUG_ELEVENLABS_DIRECT &&
        BuildConfig.ELEVENLABS_API_KEY.isNotBlank()
    val canGenerate = ttsReadiness is VoiceEngineReadiness.READY && text.isNotBlank() && status != "GENERATING"
    val canUsePremiumNarration = featureGate.canUseElevenLabsNarrator || debugDirectNarrationEnabled
    val canUseGeneratedFile = isValidGeneratedFile(generatedFile) && generatedResult?.success == true
    val botMood = when (status) {
        "INITIALIZING VOICE ENGINE" -> PrankstarBotMood.THINKING
        "GENERATING", "RECORDING FIELD NARRATION" -> PrankstarBotMood.GENERATING
        "SAVING" -> PrankstarBotMood.PROCESSING
        "SAVED" -> PrankstarBotMood.SAVED
        "GENERATED" -> PrankstarBotMood.CELEBRATING
        "PREVIEWING" -> PrankstarBotMood.PLAYING
        "ERROR" -> PrankstarBotMood.ERROR
        else -> if (text.isNotBlank()) PrankstarBotMood.TYPING else PrankstarBotMood.HAPPY
    }
    val twakBotMessage = when (twakBotMood) {
        TwakBotMood.IDLE -> "Awaiting a harmless field report target."
        TwakBotMood.SEARCHING -> "Scanning the habitat for absurd behavior."
        TwakBotMood.GENERATING -> "Narrator circuits are building the field report."
        TwakBotMood.EXCITED -> "Narration ready for review."
        TwakBotMood.PREVIEWING -> "Previewing the Twak-Attacks audio."
        TwakBotMood.SAVED -> "Saved to Sound Stash."
        TwakBotMood.REFUSAL -> "Prompt softened. Keep it generic and harmless."
        TwakBotMood.ERROR -> "Twak Bot hit a generation problem."
    }

    val botMessage = when (botMood) {
        PrankstarBotMood.HAPPY -> "Type a line. I’ll make it weird."
        PrankstarBotMood.TYPING -> "Line loaded. Choose a voice."
        PrankstarBotMood.THINKING -> "Voice engine handshake in progress."
        PrankstarBotMood.GENERATING, PrankstarBotMood.PROCESSING -> "Cooking your clip."
        PrankstarBotMood.PLAYING -> "Previewing the voice payload."
        PrankstarBotMood.SAVED -> "Saved to Stash."
        PrankstarBotMood.CELEBRATING -> "Clip generated. Stash it or preview it."
        PrankstarBotMood.ERROR -> "Voice engine needs attention."
        else -> statusDetail
    }

    fun loadBotText(draftText: String, suggestedPresetId: String?) {
        text = draftText.take(300)
        suggestedPresetId?.let { presetId ->
            allPresets.firstOrNull { it.id == presetId }?.let { applyPreset(it) }
        }
        status = "BOT DRAFT LOADED"
        statusDetail = "Prankstar Bot filled the line. Review it, then tap Generate when ready."
    }

    fun submitBotCommand(command: String) {
        val result = botController.handle(command, botPlayableSounds)
        result.actions.forEach { action ->
            when (action) {
                is PrankstarBotAction.PlaySound -> audioPlayerController.playPrankSound(action.sound)
                PrankstarBotAction.StopAllSounds -> {
                    audioPlayerController.stopAll()
                    previewPlayer.stop()
                    tts.stopPreview()
                }
                is PrankstarBotAction.Navigate -> onNavigate(action.route)
                is PrankstarBotAction.FillVoiceLabText -> Unit
                is PrankstarBotAction.ShowMessage -> Unit
                is PrankstarBotAction.ShowSoundRecommendations -> Unit
                is PrankstarBotAction.ShowPrankPlan -> Unit
                is PrankstarBotAction.Refuse -> Unit
            }
        }
        val recommendations = result.actions
            .filterIsInstance<PrankstarBotAction.ShowSoundRecommendations>()
            .firstOrNull()
        botAgentState = PrankstarBotState(
            message = PrankstarBotMessage(result.message),
            mood = result.mood,
            suggestedChips = result.suggestedChips,
            recommendations = recommendations?.sounds ?: emptyList(),
            recommendationReason = recommendations?.reason,
            generatedText = result.generatedText,
            suggestedVoicePresetId = result.suggestedVoicePresetId,
            prankPlan = result.prankPlan,
            lastActions = result.actions
        )
    }

    Box(Modifier.fillMaxSize().background(BackgroundDark)) {
        ScanlineOverlay()
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                PrankstarHeader(
                    title = "Jokes",
                    subtitle = "Voice Lab / Meme Clip Generator",
                    imageRes = R.drawable.prankstar_header,
                    statusLabel = status,
                    showTextOverlay = false
                )
            }
            item {
                PrankstarBotVideo(
                    mood = botMood,
                    message = botMessage,
                    compact = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                PrankstarBotPanel(
                    state = botAgentState,
                    onSubmit = { submitBotCommand(it) },
                    onPlaySound = { audioPlayerController.playPrankSound(it) },
                    onStopAll = {
                        audioPlayerController.stopAll()
                        previewPlayer.stop()
                        tts.stopPreview()
                        status = if (canUseGeneratedFile) "GENERATED" else "READY"
                        statusDetail = "All playback stopped."
                    },
                    onOpenStash = { onNavigate("library") },
                    onSendToVoiceLab = { draftText, presetId -> loadBotText(draftText, presetId) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Column(
                    Modifier.fillMaxWidth()
                        .background(GlassBackground, RoundedCornerShape(14.dp))
                        .border(1.dp, CyanAccent.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TwakAttackHeader()
                    TwakBotVideo(
                        mood = twakBotMood,
                        message = twakBotMessage,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Tweaker Geographic / Twak-Attacks narrator for harmless fictional behavior.", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = fieldAction,
                        onValueChange = {
                            fieldAction = it.take(120)
                            twakBotMood = if (it.isBlank()) TwakBotMood.IDLE else TwakBotMood.SEARCHING
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Action") },
                        placeholder = { Text("looking for a lighter") }
                    )
                    OutlinedTextField(
                        value = fieldSetting,
                        onValueChange = {
                            fieldSetting = it.take(80)
                            twakBotMood = if (fieldAction.isBlank() && it.isBlank()) TwakBotMood.IDLE else TwakBotMood.SEARCHING
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Setting (optional)") },
                        placeholder = { Text("near the couch") }
                    )
                    TweakerGeographicTone.entries.chunked(3).forEach { toneRow ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            toneRow.forEach { tone ->
                                val toneAllowed = featureGate.isToneAllowed(tone)
                                FilterChip(
                                    selected = fieldTone == tone,
                                    onClick = {
                                        if (toneAllowed) {
                                            fieldTone = tone
                                            twakBotMood = TwakBotMood.SEARCHING
                                        } else {
                                            status = "PRO FEATURE LOCKED"
                                            statusDetail = "Advanced Twak-Attacks tones are planned for Prankstar Pro. Billing is not configured yet."
                                            twakBotMood = TwakBotMood.IDLE
                                        }
                                    },
                                    label = { Text(if (toneAllowed) tone.label else "${tone.label} Pro") }
                                )
                            }
                        }
                    }
                    Text(
                        "Plan: ${featureGate.entitlement.displayName} • ${featureGate.voiceCredits.statusLabel}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (!featureGate.entitlement.billingConfigured) {
                        Text("Prankstar Pro purchases are not configured yet.", color = OrangeAccent, style = MaterialTheme.typography.bodySmall)
                    }
                    Row {
                        Checkbox(fieldIncludeSoundCue, {
                            fieldIncludeSoundCue = it
                            twakBotMood = TwakBotMood.SEARCHING
                        })
                        Text("Suggest matching stash sound search", color = Color.White)
                    }
                    Text("Voice Source", color = CyanAccent, style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        FilterChip(
                            selected = voiceSourceMode == VoiceSourceMode.LOCAL_ANDROID_TTS,
                            onClick = { voiceSourceMode = VoiceSourceMode.LOCAL_ANDROID_TTS },
                            label = { Text("Local Android TTS") }
                        )
                        FilterChip(
                            selected = voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR,
                            onClick = { voiceSourceMode = VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR },
                            label = { Text("Tweaker Geographic British Narrator") }
                        )
                    }
                    Text("Dedicated ElevenLabs voice configured for this feature only: ${TWEAKER_GEOGRAPHIC_VOICE_ID.take(6)}…", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    listOf("looking for a lighter", "protecting the last slice", "hunting for a charger").forEach { example ->
                        Button(
                            onClick = {
                                fieldAction = example
                                fieldResult = null
                                twakBotMood = TwakBotMood.SEARCHING
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(example) }
                    }
                    Button(onClick = {
                        if (!featureGate.canGenerateTwakText(twakTextGenerationCount)) {
                            status = "PRO FEATURE LOCKED"
                            statusDetail = "Free Twak-Attacks text generations are used for this session. Prankstar Pro billing is not configured yet."
                            twakBotMood = TwakBotMood.IDLE
                            return@Button
                        }
                        if (!featureGate.isToneAllowed(fieldTone)) {
                            status = "PRO FEATURE LOCKED"
                            statusDetail = "Advanced Twak-Attacks tones are planned for Prankstar Pro. Billing is not configured yet."
                            twakBotMood = TwakBotMood.IDLE
                            return@Button
                        }
                        twakBotMood = TwakBotMood.GENERATING
                        val result = tweakographicNarrator.generate(
                            TweakerGeographicRequest(
                                action = fieldAction,
                                setting = fieldSetting.ifBlank { null },
                                tone = fieldTone,
                                includeSoundCue = fieldIncludeSoundCue
                            )
                        )
                        fieldResult = result
                        if (result.isAllowed) twakTextGenerationCount += 1
                        if (result.isAllowed) {
                            voiceSourceMode = VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR
                            status = "FIELD REPORT READY"
                            statusDetail = "Narration generated locally. Use British Narrator or send it to local Voice Lab."
                            twakBotMood = TwakBotMood.EXCITED
                        } else {
                            status = "ERROR"
                            statusDetail = result.safetyNote ?: "Use a harmless fictional setup."
                            twakBotMood = TwakBotMood.REFUSAL
                        }
                    }) { Text("Generate Narration") }
                    fieldResult?.let { result ->
                        Column(
                            Modifier.fillMaxWidth()
                                .background(BackgroundDark.copy(alpha = 0.42f), RoundedCornerShape(10.dp))
                                .border(1.dp, if (result.isAllowed) LimeAccent.copy(alpha = 0.55f) else OrangeAccent, RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(result.title, color = if (result.isAllowed) LimeAccent else OrangeAccent)
                            Text(result.narration, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                            result.safetyNote?.let { Text(it, color = OrangeAccent, style = MaterialTheme.typography.bodySmall) }
                            if (result.isAllowed) {
                                Button(onClick = {
                                    text = result.narration.take(300)
                                    outputName = result.title
                                    allPresets.firstOrNull { it.id == result.suggestedVoicePresetId }?.let { applyPreset(it) }
                                    voiceSourceMode = VoiceSourceMode.LOCAL_ANDROID_TTS
                                    status = "FIELD REPORT LOADED"
                                    statusDetail = "Tweakographic text loaded. Review it, then use Generate Voice Clip."
                                    twakBotMood = TwakBotMood.EXCITED
                                }) { Text("Send to Voice Lab") }
                                Button(onClick = {
                                    if (!canUsePremiumNarration) {
                                        status = "PRO FEATURE LOCKED"
                                        statusDetail = "British Narrator requires Prankstar Pro voice credits. Purchases and backend credits are not configured yet."
                                        twakBotMood = TwakBotMood.IDLE
                                        return@Button
                                    }
                                    voiceSourceMode = VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR
                                    text = result.narration.take(300)
                                    outputName = result.title
                                    scope.launch {
                                        status = "GENERATING"
                                        statusDetail = "Recording field narration…"
                                        twakBotMood = TwakBotMood.GENERATING
                                        generatedFile = null
                                        generatedResult = null
                                        savedGeneratedFilePath = null
                                        generatedTweakerNarrationTitle = result.title
                                        generatedTweakerNarrationText = result.narration
                                        val formatLabel = if (voiceGenerationMode == VoiceGenerationMode.PRODUCTION_BACKEND) {
                                            "MP3/Premium Narration"
                                        } else {
                                            "MP3/ElevenLabs"
                                        }
                                        when (val providerResult = narrationProvider().generateNarration(
                                            text = result.narration,
                                            feature = TWAK_ATTACKS_FEATURE,
                                            voiceId = TWEAKER_GEOGRAPHIC_VOICE_ID,
                                            outputHint = result.title
                                        )) {
                                            is NarrationVoiceResult.Success -> {
                                                generatedFile = providerResult.outputFile
                                                generatedResult = VoiceSynthesisResult(
                                                    outputFile = providerResult.outputFile,
                                                    formatLabel = formatLabel,
                                                    durationMs = providerResult.durationMs,
                                                    success = true
                                                )
                                                status = "GENERATED"
                                                statusDetail = providerResult.remainingCredits?.let {
                                                    "Narration generated. $it narration credits remain."
                                                } ?: "Narration generated."
                                                twakBotMood = TwakBotMood.EXCITED
                                            }
                                            is NarrationVoiceResult.Failure -> {
                                                generatedFile = null
                                                generatedResult = VoiceSynthesisResult(
                                                    outputFile = File(narrationOutputDir, "narration_failed.mp3"),
                                                    formatLabel = formatLabel,
                                                    durationMs = null,
                                                    success = false,
                                                    errorMessage = providerResult.userMessage
                                                )
                                                status = "ERROR"
                                                statusDetail = providerResult.remainingCredits?.let {
                                                    "${providerResult.userMessage} $it narration credits remain."
                                                } ?: providerResult.userMessage
                                                twakBotMood = TwakBotMood.ERROR
                                            }
                                        }
                                    }
                                }, enabled = fieldResult?.isAllowed == true && status != "GENERATING") { Text("Generate British Narration") }
                                if (!canUsePremiumNarration) {
                                    Text("British Narrator is a Prankstar Pro voice-credit feature.", color = OrangeAccent, style = MaterialTheme.typography.bodySmall)
                                    Button(onClick = {
                                        status = "BILLING NOT CONFIGURED"
                                        statusDetail = "Upgrade to Pro is coming soon. Google Play Billing is not connected in this build."
                                    }) { Text("Upgrade to Pro (Coming Soon)") }
                                }
                            }
                        }
                    }
                }
            }
            item { Text("Synthetic Presets", color = LimeAccent) }
            item { Text("Warning: All voices are synthetic styling presets, not real-person clones.", color = OrangeAccent) }
            item {
                Column(Modifier.fillMaxWidth().background(GlassBackground, RoundedCornerShape(14.dp)).border(1.dp, if (status == "ERROR") OrangeAccent else CyanAccent, RoundedCornerShape(14.dp)).padding(12.dp)) {
                    Text(status, color = if (status == "ERROR") OrangeAccent else LimeAccent)
                    Text(statusDetail, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    generatedResult?.let {
                        Text("Output: ${it.formatLabel} • ${it.outputFile.name} • ${it.outputFile.length()} bytes", color = CyanAccent, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = selectedCategory == null, onClick = { selectedCategory = null }, label = { Text("All") })
                    VoiceCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.name.replace('_', ' ')) }
                        )
                    }
                }
            }

            item { OutlinedTextField(searchQuery, { searchQuery = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Search voices") }) }
            item { Text("${filteredPresets.size} presets shown", color = CyanAccent) }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = GlassBackground)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Selected Voice: ${preset.displayName}", color = Color.White)
                        Text(preset.description, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                        Text("Sample: ${preset.samplePhrase}", color = LimeAccent, style = MaterialTheme.typography.bodySmall)
                        Text("Use: ${preset.recommendedUse}", color = CyanAccent, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            items(filteredPresets) { p ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        .clickable { applyPreset(p) }
                        .border(1.dp, if (preset.id == p.id) FuchsiaAccent else Color.DarkGray, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = GlassBackground)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(p.displayName, color = Color.White)
                        Text("${p.category.name.replace('_', ' ')} • ${p.toneStyle}/${p.effectStyle}", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item { OutlinedTextField(text, { if (it.length <= 300) text = it }, modifier = Modifier.fillMaxWidth().height(140.dp), placeholder = { Text("Try: \"This fridge is now sentient\"") }, label = { Text("Type a joke") }) }
            item { Text("${text.length}/300", color = if (text.isBlank()) OrangeAccent else LimeAccent) }
            item {
                Column(Modifier.fillMaxWidth().background(GlassBackground, RoundedCornerShape(14.dp)).padding(12.dp)) {
                    Text("Voice Settings", color = CyanAccent)
                    Slider(pitch, { pitch = it }, valueRange = 0.5f..1.8f); Text("Pitch ${"%.2f".format(pitch)}", color = Color.White)
                    Slider(speed, { speed = it }, valueRange = 0.5f..1.6f); Text("Speed ${"%.2f".format(speed)}", color = Color.White)
                    Slider(volume, { volume = it }, valueRange = 0.2f..1.2f); Text("Volume ${"%.2f".format(volume)}", color = Color.White)
                    Slider(effect, { effect = it }); Text("Effect ${"%.2f".format(effect)}", color = Color.White)
                    Row { Checkbox(echo, { echo = it }); Text("Echo/Reverb (simulated toggle)", color = Color.White) }
                }
            }
            item { OutlinedTextField(outputName, { outputName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Output Name") }, placeholder = { Text("Midnight prank check-in") }) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { applyPreset(allPresets.random()) }) { Text("Random Funny Voice") }
                    Button(onClick = {
                        val safe = allPresets.filter { it.isSafeForRandomMode }
                        if (safe.isNotEmpty()) applyPreset(safe.random())
                    }) { Text("Random Safe Preset") }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { applyPreset(preset) }) { Text("Quick Reset") }
                    Button(onClick = {
                        if (ttsReadiness !is VoiceEngineReadiness.READY) {
                            status = "ERROR"
                            statusDetail = "Voice engine is not ready."
                            return@Button
                        }
                        previewPlayer.stop()
                        status = "PREVIEWING"
                        statusDetail = "Previewing ${preset.displayName}."
                        tts.preview(VoiceGeneratorSettings(preset, preset.samplePhrase, pitch, speed, volume, preset.toneStyle, effect, echo, outputName))
                    }, enabled = ttsReadiness is VoiceEngineReadiness.READY && status != "GENERATING") { Text("Preview Voice Style") }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        if (text.isBlank() || containsRestrictedAlertTerm(text)) {
                            status = "ERROR"
                            statusDetail = "Enter harmless text that does not impersonate emergency or official alerts."
                            if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.REFUSAL
                            return@Button
                        }
                        scope.launch {
                            status = "GENERATING"
                            statusDetail = "Generating engine-specific audio output."
                            if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.GENERATING
                            generatedFile = null
                            generatedResult = null
                            savedGeneratedFilePath = null
                            val file = File(context.filesDir, "voice_${System.currentTimeMillis()}.wav")
                            val result = withContext(Dispatchers.IO) { tts.synthesizeToFile(settings(), file) }
                            if (result.success && isValidGeneratedFile(result.outputFile)) {
                                generatedFile = result.outputFile
                                generatedResult = result
                                status = "GENERATED"
                                statusDetail = "Generated ${result.formatLabel} audio. Save to Stash when ready."
                                if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.EXCITED
                            } else {
                                generatedFile = null
                                generatedResult = result
                                status = "ERROR"
                                statusDetail = result.errorMessage ?: "Generated audio file is missing or empty."
                                if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.ERROR
                            }
                        }
                    }, enabled = canGenerate) { Text("Generate Voice Clip") }
                    Button(onClick = {
                        val file = generatedFile ?: return@Button
                        if (!isValidGeneratedFile(file)) {
                            status = "ERROR"
                            statusDetail = "Generated audio file is missing or empty."
                            return@Button
                        }
                        tts.stopPreview()
                        val scheduled = previewPlayer.play(
                            file = file,
                            onStarted = {
                                status = "PREVIEWING"
                                statusDetail = "Previewing generated audio."
                                if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.PREVIEWING
                            },
                            onError = {
                                status = "ERROR"
                                statusDetail = it
                                if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.ERROR
                            },
                            onComplete = {
                                status = "GENERATED"
                                statusDetail = "Preview complete. Save to Stash when ready."
                                if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.EXCITED
                            }
                        )
                        if (scheduled) {
                            status = "PREVIEWING"
                            statusDetail = "Preparing preview..."
                            if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.PREVIEWING
                        }
                    }, enabled = canUseGeneratedFile && status != "GENERATING") { Text("Preview Clip") }
                    Button(onClick = {
                        previewPlayer.stop()
                        tts.stopPreview()
                        status = if (canUseGeneratedFile) "GENERATED" else "READY"
                        statusDetail = "Preview stopped."
                        if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = if (canUseGeneratedFile) TwakBotMood.EXCITED else TwakBotMood.IDLE
                    }) { Text("Stop Preview") }
                }
            }
            item {
                Button(onClick = {
                    val file = generatedFile ?: return@Button
                    if (!isValidGeneratedFile(file)) {
                        status = "ERROR"
                        statusDetail = "Cannot save an empty or missing generated file."
                            return@Button
                        }
                        if (!featureGate.canSaveGeneratedClip(generatedVoiceClipCount)) {
                            status = "PRO FEATURE LOCKED"
                            statusDetail = "${featureGate.generatedClipSaveLimitLabel()} used. Higher save limits are planned for Prankstar Pro; billing is not configured yet."
                            if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.IDLE
                            return@Button
                        }
                        scope.launch {
                        val generationIsValid = generatedResult?.success == true
                        if (!generationIsValid) {
                            status = "ERROR"
                            statusDetail = "Cannot save because generation did not complete successfully."
                            if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.ERROR
                            return@launch
                        }
                        status = "SAVING"
                        statusDetail = "Saving generated voice clip to Sound Stash."
                        if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.GENERATING
                        runCatching {
                            if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR && file.extension.equals("mp3", ignoreCase = true)) {
                                generatedRepo.saveTweakerGeographicElevenLabsVoice(
                                    file = file,
                                    title = generatedTweakerNarrationTitle ?: outputName,
                                    narrationText = generatedTweakerNarrationText ?: text,
                                    durationMs = generatedResult?.durationMs
                                )
                            } else {
                                generatedRepo.saveGeneratedVoice(file, settings(), generatedResult?.durationMs)
                            }
                        }.onSuccess {
                            savedGeneratedFilePath = file.absolutePath
                            status = "SAVED"
                            statusDetail = "Generated audio saved to Sound Stash."
                            if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.SAVED
                        }.onFailure {
                            status = "ERROR"
                            statusDetail = it.message ?: "Unable to save generated audio."
                            if (voiceSourceMode == VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR) twakBotMood = TwakBotMood.ERROR
                        }
                    }
                }, enabled = canUseGeneratedFile && savedGeneratedFilePath != generatedFile?.absolutePath) { Text("Save to Stash") }
            }
            item {
                Text(
                    if (ttsReadiness is VoiceEngineReadiness.READY) {
                        "Output: WAV/PCM generated locally on device, or MP3 for confirmed Tweaker Geographic British Narrator requests. ${featureGate.generatedClipSaveLimitLabel()}."
                    } else {
                        "TTS unavailable: install or enable an Android text-to-speech engine."
                    },
                    color = CyanAccent
                )
            }
            item { Text("Safety: Keep pranks harmless. No real-person or official-alert impersonation.", color = OrangeAccent) }
        }
    }
}

private fun containsRestrictedAlertTerm(value: String): Boolean {
    val restricted = listOf("police", "emergency", "ambulance", "fire department", "evacuate", "siren", "official alert")
    return restricted.any { value.contains(it, ignoreCase = true) }
}
