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
import com.pranksterlab.components.bot.PrankstarBotVideo
import com.pranksterlab.core.bot.PrankstarBotJokeGenerator
import com.pranksterlab.core.bot.PrankstarBotVoiceLabBridge
import com.pranksterlab.core.elevenlabs.ElevenLabsTtsResult
import com.pranksterlab.core.elevenlabs.ElevenLabsTtsService
import com.pranksterlab.core.elevenlabs.TWEAKER_GEOGRAPHIC_VOICE_ID
import com.pranksterlab.core.narration.TweakerGeographicNarrator
import com.pranksterlab.core.narration.TweakerGeographicRequest
import com.pranksterlab.core.narration.TweakerGeographicResult
import com.pranksterlab.core.narration.TweakerGeographicTone
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.core.voice.AndroidTextToSpeechEngine
import com.pranksterlab.core.voice.GeneratedVoiceRepository
import com.pranksterlab.core.voice.VoiceCategory
import com.pranksterlab.core.voice.VoiceEngineReadiness
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
fun VoiceJokeGeneratorScreen(soundRepository: SoundRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tts = remember { AndroidTextToSpeechEngine(context) }
    val elevenLabsTtsService = remember {
        ElevenLabsTtsService(
            client = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build(),
            apiKeyProvider = { BuildConfig.ELEVENLABS_API_KEY }
        )
    }
    val generatedRepo = remember { GeneratedVoiceRepository(soundRepository) }
    val allPresets = VoicePresetLibrary.presets
    val ttsReadiness by tts.readiness.collectAsState()
    val previewPlayer = remember { ManagedPreviewPlayer() }
    val botJokeGenerator = remember { PrankstarBotJokeGenerator() }
    val tweakographicNarrator = remember { TweakerGeographicNarrator() }
    val pendingBotDraft by PrankstarBotVoiceLabBridge.pendingDraft.collectAsState()

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
    var fieldAction by remember { mutableStateOf("") }
    var fieldSetting by remember { mutableStateOf("") }
    var fieldTone by remember { mutableStateOf(TweakerGeographicTone.BALANCED) }
    var fieldIncludeSoundCue by remember { mutableStateOf(false) }
    var fieldResult by remember { mutableStateOf<TweakerGeographicResult?>(null) }
    var voiceSourceMode by remember { mutableStateOf(VoiceSourceMode.LOCAL_ANDROID_TTS) }
    var generatedTweakerNarrationTitle by remember { mutableStateOf<String?>(null) }
    var generatedTweakerNarrationText by remember { mutableStateOf<String?>(null) }

    fun applyPreset(selected: VoicePreset) {
        preset = selected
        pitch = selected.pitch
        speed = selected.speechRate
        volume = selected.volume
    }

    fun settings() = VoiceGeneratorSettings(preset, text, pitch, speed, volume, preset.toneStyle, effect, echo, outputName)
    fun isValidGeneratedFile(file: File?) = file != null && file.exists() && file.length() > 0L

    val filteredPresets = allPresets.filter {
        (selectedCategory == null || it.category == selectedCategory) &&
            (searchQuery.isBlank() || it.displayName.contains(searchQuery, true) || it.description.contains(searchQuery, true))
    }

    DisposableEffect(Unit) {
        onDispose {
            previewPlayer.stop()
            tts.stopPreview()
            tts.release()
        }
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

    val canGenerate = ttsReadiness is VoiceEngineReadiness.READY && text.isNotBlank() && status != "GENERATING"
    val canGenerateBritishNarration = fieldResult?.isAllowed == true && status != "GENERATING"
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
                var botPrompt by remember { mutableStateOf("") }
                Column(Modifier.fillMaxWidth().background(GlassBackground, RoundedCornerShape(14.dp)).border(1.dp, FuchsiaAccent.copy(alpha = 0.45f), RoundedCornerShape(14.dp)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Bot Helper", color = FuchsiaAccent, style = MaterialTheme.typography.labelLarge)
                    Text("Ask for a harmless line. The bot fills this screen only; Generate stays user-controlled.", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = botPrompt,
                        onValueChange = { botPrompt = it.take(180) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Make a joke/comment about...") },
                        placeholder = { Text("my friend being late") }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            val generated = botJokeGenerator.generate(botPrompt)
                            text = generated.text.take(300)
                            allPresets.firstOrNull { it.id == generated.suggestedVoicePresetId }?.let { applyPreset(it) }
                            status = "BOT LINE READY"
                            statusDetail = "Generated locally from safe templates. Review before Generate."
                        }) { Text("Make Line") }
                        Button(onClick = {
                            val generated = botJokeGenerator.generate("robot announcement ${botPrompt}")
                            text = generated.text.take(300)
                            allPresets.firstOrNull { it.id == generated.suggestedVoicePresetId }?.let { applyPreset(it) }
                            status = "BOT ROBOT LINE READY"
                            statusDetail = "Robot-style line filled. Generate remains manual."
                        }) { Text("Robot") }
                        Button(onClick = {
                            fieldAction = botPrompt.ifBlank { "looking for a lighter" }.take(120)
                            val result = tweakographicNarrator.generate(
                                TweakerGeographicRequest(
                                    action = fieldAction,
                                    setting = null,
                                    tone = fieldTone,
                                    includeSoundCue = fieldIncludeSoundCue
                                )
                            )
                            fieldResult = result
                            if (result.isAllowed) {
                                text = result.narration.take(300)
                                outputName = result.title
                                voiceSourceMode = VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR
                                status = "BOT FIELD REPORT READY"
                                statusDetail = "Bot routed a Tweaker Geographic draft. Review it, then tap Generate British Narration."
                            } else {
                                status = "ERROR"
                                statusDetail = result.safetyNote ?: "Use a harmless fictional setup."
                            }
                        }) { Text("Tweakographic") }
                    }
                }
            }
            item {
                Column(
                    Modifier.fillMaxWidth()
                        .background(GlassBackground, RoundedCornerShape(14.dp))
                        .border(1.dp, CyanAccent.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Tweakographic Narrator", color = CyanAccent, style = MaterialTheme.typography.labelLarge)
                    Text("Original mock-documentary narration for harmless fictional behavior.", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = fieldAction,
                        onValueChange = { fieldAction = it.take(120) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Action") },
                        placeholder = { Text("looking for a lighter") }
                    )
                    OutlinedTextField(
                        value = fieldSetting,
                        onValueChange = { fieldSetting = it.take(80) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Setting (optional)") },
                        placeholder = { Text("near the couch") }
                    )
                    TweakerGeographicTone.entries.chunked(3).forEach { toneRow ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            toneRow.forEach { tone ->
                                FilterChip(
                                    selected = fieldTone == tone,
                                    onClick = { fieldTone = tone },
                                    label = { Text(tone.label) }
                                )
                            }
                        }
                    }
                    Row {
                        Checkbox(fieldIncludeSoundCue, { fieldIncludeSoundCue = it })
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
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(example) }
                    }
                    Button(onClick = {
                        val result = tweakographicNarrator.generate(
                            TweakerGeographicRequest(
                                action = fieldAction,
                                setting = fieldSetting.ifBlank { null },
                                tone = fieldTone,
                                includeSoundCue = fieldIncludeSoundCue
                            )
                        )
                        fieldResult = result
                        if (result.isAllowed) {
                            voiceSourceMode = VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR
                            status = "FIELD REPORT READY"
                            statusDetail = "Narration generated locally. Use British Narrator or send it to local Voice Lab."
                        } else {
                            status = "ERROR"
                            statusDetail = result.safetyNote ?: "Use a harmless fictional setup."
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
                                }) { Text("Send to Voice Lab") }
                                Button(onClick = {
                                    voiceSourceMode = VoiceSourceMode.TWEAKER_GEOGRAPHIC_BRITISH_NARRATOR
                                    text = result.narration.take(300)
                                    outputName = result.title
                                    scope.launch {
                                        status = "GENERATING"
                                        statusDetail = "Recording field narration…"
                                        generatedFile = null
                                        generatedResult = null
                                        savedGeneratedFilePath = null
                                        generatedTweakerNarrationTitle = result.title
                                        generatedTweakerNarrationText = result.narration
                                        val dir = File(context.filesDir, "generated/elevenlabs")
                                        val file = File(dir, "tweaker_geo_${System.currentTimeMillis()}.mp3")
                                        when (val ttsResult = elevenLabsTtsService.generateTweakerGeographicNarration(result.narration, file)) {
                                            is ElevenLabsTtsResult.Success -> {
                                                generatedFile = ttsResult.outputFile
                                                generatedResult = VoiceSynthesisResult(
                                                    outputFile = ttsResult.outputFile,
                                                    formatLabel = "MP3/ElevenLabs",
                                                    durationMs = ttsResult.durationMs,
                                                    success = true
                                                )
                                                status = "GENERATED"
                                                statusDetail = "Narration generated."
                                            }
                                            is ElevenLabsTtsResult.Failure -> {
                                                generatedFile = null
                                                generatedResult = VoiceSynthesisResult(
                                                    outputFile = file,
                                                    formatLabel = "MP3/ElevenLabs",
                                                    durationMs = null,
                                                    success = false,
                                                    errorMessage = ttsResult.error.userMessage
                                                )
                                                status = "ERROR"
                                                statusDetail = ttsResult.error.userMessage
                                            }
                                        }
                                    }
                                }, enabled = canGenerateBritishNarration) { Text("Generate British Narration") }
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
                            return@Button
                        }
                        scope.launch {
                            status = "GENERATING"
                            statusDetail = "Generating engine-specific audio output."
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
                            } else {
                                generatedFile = null
                                generatedResult = result
                                status = "ERROR"
                                statusDetail = result.errorMessage ?: "Generated audio file is missing or empty."
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
                            },
                            onError = {
                                status = "ERROR"
                                statusDetail = it
                            },
                            onComplete = {
                                status = "GENERATED"
                                statusDetail = "Preview complete. Save to Stash when ready."
                            }
                        )
                        if (scheduled) {
                            status = "PREVIEWING"
                            statusDetail = "Preparing preview..."
                        }
                    }, enabled = canUseGeneratedFile && status != "GENERATING") { Text("Preview Clip") }
                    Button(onClick = {
                        previewPlayer.stop()
                        tts.stopPreview()
                        status = if (canUseGeneratedFile) "GENERATED" else "READY"
                        statusDetail = "Preview stopped."
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
                    scope.launch {
                        val generationIsValid = generatedResult?.success == true
                        if (!generationIsValid) {
                            status = "ERROR"
                            statusDetail = "Cannot save because generation did not complete successfully."
                            return@launch
                        }
                        status = "SAVING"
                        statusDetail = "Saving generated voice clip to Sound Stash."
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
                        }.onFailure {
                            status = "ERROR"
                            statusDetail = it.message ?: "Unable to save generated audio."
                        }
                    }
                }, enabled = canUseGeneratedFile && savedGeneratedFilePath != generatedFile?.absolutePath) { Text("Save to Stash") }
            }
            item {
                Text(
                    if (ttsReadiness is VoiceEngineReadiness.READY) {
                        "Output: WAV/PCM generated locally on device, or MP3 for confirmed Tweaker Geographic British Narrator requests."
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
