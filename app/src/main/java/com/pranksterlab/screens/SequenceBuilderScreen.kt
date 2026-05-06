package com.pranksterlab.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranksterlab.components.GlassPanel
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.model.SequenceStep
import com.pranksterlab.core.model.SoundSequencePreset
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.GlassBackground
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun SequenceBuilderScreen(soundRepository: SoundRepository, audioPlayerController: AudioPlayerController) {
    val scope = rememberCoroutineScope()
    var bundledSounds by remember { mutableStateOf(emptyList<PrankSound>()) }
    var customSounds by remember { mutableStateOf(emptyList<PrankSound>()) }
    var presets by remember { mutableStateOf(emptyList<SoundSequencePreset>()) }
    var sequence by remember { mutableStateOf(emptyList<SequenceStep>()) }
    var searchQuery by remember { mutableStateOf("") }
    var showPicker by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }
    var repeatCount by remember { mutableStateOf(1) }
    var activeStepId by remember { mutableStateOf<String?>(null) }
    var playbackWarning by remember { mutableStateOf<String?>(null) }
    var sequenceJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
        bundledSounds = soundRepository.getBundledSounds()
        soundRepository.getCustomSoundsFlow().collect { customSounds = it }
    }
    LaunchedEffect(Unit) {
        soundRepository.getSequencePresetsFlow().collect { presets = it.sortedByDescending { p -> p.updatedAt } }
    }

    val catalog = bundledSounds + customSounds

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("SEQUENCE BUILDER", style = MaterialTheme.typography.displayLarge, color = CyanAccent)
            Text("Timeline your prank payload.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (sequenceJob == null) {
                        sequenceJob = scope.launch {
                            playbackWarning = null
                            runSequence(sequence, repeatCount, audioPlayerController) { activeStepId = it }?.let { playbackWarning = it }
                            activeStepId = null
                            sequenceJob = null
                        }
                    }
                }, enabled = sequence.isNotEmpty() && sequenceJob == null, colors = ButtonDefaults.buttonColors(containerColor = FuchsiaAccent)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play sequence")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PLAY")
                }
                Button(onClick = {
                    sequenceJob?.cancel()
                    sequenceJob = null
                    audioPlayerController.stop()
                    activeStepId = null
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF831843))) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop sequence")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("STOP")
                }
                OutlinedTextField(
                    value = repeatCount.toString(),
                    onValueChange = { repeatCount = it.toIntOrNull()?.coerceIn(1, 20) ?: 1 },
                    label = { Text("Repeats") },
                    modifier = Modifier.width(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { showPicker = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent)) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add sound", tint = CyanAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    LabelCaps("ADD SIGNAL", color = CyanAccent)
                }
                Button(onClick = { showSaveDialog = true }, enabled = sequence.isNotEmpty()) { Text("SAVE PRESET") }
            }

            if (playbackWarning != null) {
                Text(playbackWarning!!, color = Color(0xFFFACC15), modifier = Modifier.padding(top = 8.dp))
            }

            if (sequence.isEmpty()) {
                GlassPanel(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NO STEPS LOCKED", color = CyanAccent)
                        Text("Add real sounds from catalog to start building timeline.", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    itemsIndexed(sequence, key = { _, step -> step.id }) { index, step ->
                        SequenceTimelineCard(
                            index = index,
                            step = step,
                            isActive = activeStepId == step.id,
                            onDelayChange = { newDelay ->
                                sequence = sequence.toMutableList().also { it[index] = it[index].copy(delayAfterMs = newDelay) }
                            },
                            onMoveUp = {
                                if (index > 0) sequence = sequence.toMutableList().also { java.util.Collections.swap(it, index, index - 1) }
                            },
                            onMoveDown = {
                                if (index < sequence.lastIndex) sequence = sequence.toMutableList().also { java.util.Collections.swap(it, index, index + 1) }
                            },
                            onDelete = { sequence = sequence.filterNot { it.id == step.id } }
                        )
                    }
                }
            }
        }
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text("Add Sound Step") },
            text = {
                Column {
                    OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, label = { Text("Search catalog") })
                    LazyColumn(modifier = Modifier.height(280.dp)) {
                        itemsIndexed(catalog.filter {
                            it.name.contains(searchQuery, true) || it.category.contains(searchQuery, true)
                        }) { _, sound ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    sequence = sequence + SequenceStep(
                                        id = UUID.randomUUID().toString(),
                                        soundId = sound.id,
                                        soundName = sound.name,
                                        assetPath = sound.localUri ?: sound.assetPath,
                                        delayAfterMs = sound.durationMs.coerceAtLeast(300L),
                                        category = sound.category
                                    )
                                }.padding(8.dp)
                            ) {
                                Text("${sound.name} · ${sound.category}", color = Color.White)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showPicker = false }) { Text("DONE") } }
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Sequence Preset") },
            text = { OutlinedTextField(value = presetName, onValueChange = { presetName = it }, label = { Text("Preset name") }) },
            confirmButton = {
                TextButton(onClick = {
                    val now = System.currentTimeMillis()
                    scope.launch {
                        soundRepository.saveSequencePreset(
                            SoundSequencePreset(UUID.randomUUID().toString(), presetName.ifBlank { "Untitled" }, sequence, repeatCount, now, now)
                        )
                    }
                    presetName = ""
                    showSaveDialog = false
                }) { Text("SAVE") }
            },
            dismissButton = { TextButton(onClick = { showSaveDialog = false }) { Text("CANCEL") } }
        )
    }

    if (presets.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            presets.take(3).forEach { preset ->
                Row(
                    modifier = Modifier.fillMaxWidth().background(GlassBackground, RoundedCornerShape(10.dp)).border(1.dp, CyanAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp)).padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(preset.name, color = CyanAccent)
                        Text("${preset.steps.size} steps · x${preset.repeatCount}", color = Color.Gray)
                    }
                    TextButton(onClick = { sequence = preset.steps; repeatCount = preset.repeatCount }) { Text("LOAD") }
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete preset",
                        tint = Color(0xFFF87171),
                        modifier = Modifier.clickable { scope.launch { soundRepository.deleteSequencePreset(preset.id) } }
                    )
                }
            }
        }
    }
}

private suspend fun runSequence(
    steps: List<SequenceStep>,
    repeatCount: Int,
    audioPlayerController: AudioPlayerController,
    onActiveStep: (String?) -> Unit
): String? {
    if (steps.isEmpty()) return null
    var invalidCount = 0
    repeat(repeatCount.coerceAtLeast(1)) {
        for (step in steps) {
            onActiveStep(step.id)
            val played = audioPlayerController.playSound(
                assetPath = step.assetPath,
                isLocalUri = step.assetPath.startsWith("/") || step.assetPath.startsWith("content://") || step.assetPath.startsWith("file://"),
                soundId = step.soundId,
                soundTitle = step.soundName,
                isLooping = false
            )
            if (!played) {
                invalidCount++
                continue
            }
            delay(step.delayAfterMs.coerceAtLeast(150L))
            audioPlayerController.stop()
        }
    }
    onActiveStep(null)
    return if (invalidCount > 0) "Warning: $invalidCount invalid/corrupt steps were skipped." else null
}

@Composable
private fun SequenceTimelineCard(
    index: Int,
    step: SequenceStep,
    isActive: Boolean,
    onDelayChange: (Long) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(44.dp).background(if (isActive) FuchsiaAccent.copy(alpha = 0.25f) else GlassBackground, RoundedCornerShape(12.dp)).border(1.dp, if (isActive) FuchsiaAccent else CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) { HeadlineText((index + 1).toString(), color = if (isActive) FuchsiaAccent else CyanAccent) }

        GlassPanel(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(step.soundName, color = if (isActive) FuchsiaAccent else CyanAccent)
                Text("${step.soundId} • ${step.category}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = step.delayAfterMs.toString(),
                        onValueChange = { onDelayChange(it.toLongOrNull()?.coerceAtLeast(100L) ?: step.delayAfterMs) },
                        label = { Text("Delay ms") },
                        modifier = Modifier.width(140.dp)
                    )
                    Icon(Icons.Default.ArrowUpward, "Move up", tint = Color.White, modifier = Modifier.clickable { onMoveUp() })
                    Icon(Icons.Default.ArrowDownward, "Move down", tint = Color.White, modifier = Modifier.clickable { onMoveDown() })
                    Icon(Icons.Default.Delete, "Delete step", tint = Color(0xFFF87171), modifier = Modifier.clickable { onDelete() })
                }
            }
        }
    }
}
