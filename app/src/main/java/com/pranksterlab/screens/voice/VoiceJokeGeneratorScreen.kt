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
import com.pranksterlab.R
import com.pranksterlab.components.PrankstarHeader
import com.pranksterlab.components.ScanlineOverlay
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
import java.io.File

@Composable
fun VoiceJokeGeneratorScreen(soundRepository: SoundRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tts = remember { AndroidTextToSpeechEngine(context) }
    val generatedRepo = remember { GeneratedVoiceRepository(soundRepository) }
    val allPresets = VoicePresetLibrary.presets
    val ttsReadiness by tts.readiness.collectAsState()

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

    fun applyPreset(selected: VoicePreset) {
        preset = selected
        pitch = selected.pitch
        speed = selected.speechRate
        volume = selected.volume
    }

    val filteredPresets = allPresets.filter {
        (selectedCategory == null || it.category == selectedCategory) &&
            (searchQuery.isBlank() || it.displayName.contains(searchQuery, true) || it.description.contains(searchQuery, true))
    }

    DisposableEffect(Unit) { onDispose { tts.release() } }

    fun settings() = VoiceGeneratorSettings(preset, text, pitch, speed, volume, preset.toneStyle, effect, echo, outputName)
    fun isValidGeneratedFile(file: File?) = file != null && file.exists() && file.length() > 0L

    LaunchedEffect(ttsReadiness) {
        when (val readiness = ttsReadiness) {
            VoiceEngineReadiness.Initializing -> {
                status = "INITIALIZING VOICE ENGINE"
                statusDetail = "Preparing Android TextToSpeech."
            }
            VoiceEngineReadiness.Ready -> {
                if (status == "INITIALIZING VOICE ENGINE" || status == "ERROR") {
                    status = "READY"
                    statusDetail = "Voice engine is ready."
                }
            }
            VoiceEngineReadiness.Unavailable -> {
                status = "ERROR"
                statusDetail = "TextToSpeech engine is unavailable on this device."
            }
            is VoiceEngineReadiness.Error -> {
                status = "ERROR"
                statusDetail = readiness.message
            }
        }
    }

    val canGenerate = ttsReadiness is VoiceEngineReadiness.Ready && text.isNotBlank() && status != "GENERATING"
    val canUseGeneratedFile = isValidGeneratedFile(generatedFile)

    Box(Modifier.fillMaxSize().background(BackgroundDark)) {
        ScanlineOverlay()
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item { PrankstarHeader("Voice Lab", "Joke / Comment Generator", R.drawable.header_joke_gen, statusLabel = status, showTextOverlay = false) }
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

            item { OutlinedTextField(text, { if (it.length <= 300) text = it }, modifier = Modifier.fillMaxWidth().height(140.dp), placeholder = { Text("Try: \"Warning, this fridge is now sentient\"") }, label = { Text("Joke / Comment") }) }
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
                        if (ttsReadiness !is VoiceEngineReadiness.Ready) {
                            status = "ERROR"
                            statusDetail = "Voice engine is not ready."
                            return@Button
                        }
                        status = "PREVIEWING"
                        statusDetail = "Previewing ${preset.displayName}."
                        tts.preview(VoiceGeneratorSettings(preset, preset.samplePhrase, pitch, speed, volume, preset.toneStyle, effect, echo, outputName))
                    }, enabled = ttsReadiness is VoiceEngineReadiness.Ready) { Text("Preview Voice Style") }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        if (text.isBlank() || text.contains("police", true) || text.contains("emergency", true)) {
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
                                statusDetail = "Generated ${result.formatLabel} audio. Save to Library when ready."
                            } else {
                                generatedFile = null
                                generatedResult = result
                                status = "ERROR"
                                statusDetail = result.errorMessage ?: "Generated audio file is missing or empty."
                            }
                        }
                    }, enabled = canGenerate) { Text("Generate") }
                    Button(onClick = {
                        val file = generatedFile ?: return@Button
                        if (!isValidGeneratedFile(file)) {
                            status = "ERROR"
                            statusDetail = "Generated audio file is missing or empty."
                            return@Button
                        }
                        runCatching {
                            MediaPlayer().apply {
                                setDataSource(file.absolutePath)
                                setOnCompletionListener { player -> player.release() }
                                prepare()
                                start()
                            }
                        }.onSuccess {
                            status = "PREVIEWING"
                            statusDetail = "Previewing generated audio."
                        }.onFailure {
                            status = "ERROR"
                            statusDetail = it.message ?: "Unable to preview generated audio."
                        }
                    }, enabled = canUseGeneratedFile) { Text("Preview") }
                    Button(onClick = { tts.stopPreview() }) { Text("Stop") }
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
                        runCatching {
                            generatedRepo.saveGeneratedVoice(file, settings(), generatedResult?.durationMs)
                        }.onSuccess {
                            savedGeneratedFilePath = file.absolutePath
                            status = "SAVED TO LIBRARY"
                            statusDetail = "Generated audio was saved to Library."
                        }.onFailure {
                            status = "ERROR"
                            statusDetail = it.message ?: "Unable to save generated audio."
                        }
                    }
                }, enabled = canUseGeneratedFile && savedGeneratedFilePath != generatedFile?.absolutePath) { Text("Save to Library (WAV)") }
            }
            item { Text("Safety: Keep pranks harmless. No real-person or official-alert impersonation.", color = OrangeAccent) }
        }
    }
}
