package com.pranksterlab.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.R
import com.pranksterlab.components.HUDCard
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.components.PrankstarHeader
import com.pranksterlab.components.ScanlineOverlay
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.model.SequenceStep
import com.pranksterlab.core.model.SoundSequencePreset
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.GlassBackground
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrangeAccent
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
    var repeatCount by remember { mutableStateOf(1) }
    var activeStepId by remember { mutableStateOf<String?>(null) }
    var sequenceJob by remember { mutableStateOf<Job?>(null) }
    var warning by remember { mutableStateOf<String?>(null) }
    var showPicker by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveName by remember { mutableStateOf("") }
    var presetToDelete by remember { mutableStateOf<SoundSequencePreset?>(null) }

    val invalidSoundIds by audioPlayerController.invalidSoundIds.collectAsState()

    LaunchedEffect(Unit) {
        bundledSounds = soundRepository.getBundledSounds()
    }

    LaunchedEffect(Unit) {
        soundRepository.getCustomSoundsFlow().collect { customSounds = it }
    }

    LaunchedEffect(Unit) {
        soundRepository.getSequencePresetsFlow().collect { loaded ->
            presets = loaded.sortedByDescending { it.updatedAt }
        }
    }

    val catalog = bundledSounds + customSounds
    val playableCatalog = remember(catalog, invalidSoundIds) {
        catalog.filter { sound ->
            sound.id !in invalidSoundIds && (sound.isCustom || soundRepository.isCatalogSoundPlayable(sound))
        }
    }
    val blockedCount = catalog.size - playableCatalog.size
    val soundById = remember(catalog) { catalog.associateBy { it.id } }
    val isPlaying = sequenceJob != null

    LaunchedEffect(playableCatalog) {
        if (catalog.isEmpty()) return@LaunchedEffect
        val pendingSoundId = soundRepository.consumePendingSequenceSoundId() ?: return@LaunchedEffect
        playableCatalog.firstOrNull { it.id == pendingSoundId }?.let { sound ->
            sequence = sequence + sound.toSequenceStep()
            warning = "Added '${sound.name}' from Library."
        } ?: run {
            warning = "Library handoff sound is no longer playable."
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        ScanlineOverlay()
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 18.dp, bottom = 110.dp)
        ) {
            item {
                PrankstarHeader(
                    title = "Sequence Builder",
                    subtitle = "Multi-Stage Prank Timeline",
                    imageRes = R.drawable.prankstar_sn3,
                    statusLabel = if (isPlaying) "RUNNING" else "${sequence.size} STEPS"
                )
            }

            item {
                SequenceHeader(
                    stepCount = sequence.size,
                    presetCount = presets.size,
                    blockedCount = blockedCount
                )
            }

            item {
                PlaybackConsole(
                    repeatCount = repeatCount,
                    isPlaying = isPlaying,
                    canPlay = sequence.isNotEmpty(),
                    onRepeatChange = { repeatCount = it },
                    onPlay = {
                        warning = null
                        sequenceJob = scope.launch {
                            try {
                                val result = runSequence(
                                    steps = sequence,
                                    repeatCount = repeatCount,
                                    catalogById = soundById,
                                    audioPlayerController = audioPlayerController,
                                    onActiveStep = { activeStepId = it }
                                )
                                warning = result
                            } finally {
                                audioPlayerController.stop()
                                activeStepId = null
                                sequenceJob = null
                            }
                        }
                    },
                    onStop = {
                        sequenceJob?.cancel()
                        sequenceJob = null
                        audioPlayerController.stop()
                        activeStepId = null
                    },
                    onAddSound = { showPicker = true },
                    onSave = { showSaveDialog = true }
                )
            }

            if (warning != null) {
                item {
                    HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = OrangeAccent) {
                        Text(
                            text = warning.orEmpty(),
                            color = OrangeAccent,
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                LabelCaps("NEON TIMELINE", color = LimeAccent)
            }

            if (sequence.isEmpty()) {
                item {
                    EmptySequenceCard(onAddSound = { showPicker = true })
                }
            } else {
                itemsIndexed(sequence, key = { _, step -> step.id }) { index, step ->
                    SequenceTimelineCard(
                        index = index,
                        step = step,
                        isActive = activeStepId == step.id,
                        canMoveUp = index > 0,
                        canMoveDown = index < sequence.lastIndex,
                        onDelayChange = { newDelay ->
                            sequence = sequence.toMutableList().also { steps ->
                                steps[index] = steps[index].copy(delayAfterMs = newDelay)
                            }
                        },
                        onMoveUp = {
                            sequence = sequence.toMutableList().also { java.util.Collections.swap(it, index, index - 1) }
                        },
                        onMoveDown = {
                            sequence = sequence.toMutableList().also { java.util.Collections.swap(it, index, index + 1) }
                        },
                        onDelete = {
                            sequence = sequence.filterNot { it.id == step.id }
                        }
                    )
                }
            }

            item {
                PresetConsole(
                    presets = presets,
                    onLoad = { preset ->
                        if (!isPlaying) {
                            sequence = preset.steps
                            repeatCount = preset.repeatCount.coerceIn(1, 20)
                            warning = "Loaded '${preset.name}' with ${preset.steps.size} steps."
                        }
                    },
                    onDelete = { presetToDelete = it }
                )
            }
        }
    }

    if (showPicker) {
        SoundPickerDialog(
            sounds = playableCatalog,
            blockedCount = blockedCount,
            onDismiss = { showPicker = false },
            onAdd = { sound ->
                sequence = sequence + sound.toSequenceStep()
            }
        )
    }

    if (showSaveDialog) {
        SavePresetDialog(
            name = saveName,
            onNameChange = { saveName = it },
            onDismiss = { showSaveDialog = false },
            onSave = {
                val now = System.currentTimeMillis()
                val preset = SoundSequencePreset(
                    id = UUID.randomUUID().toString(),
                    name = saveName.ifBlank { "Untitled Sequence" },
                    steps = sequence,
                    repeatCount = repeatCount.coerceIn(1, 20),
                    createdAt = now,
                    updatedAt = now
                )
                scope.launch { soundRepository.saveSequencePreset(preset) }
                saveName = ""
                showSaveDialog = false
            }
        )
    }

    presetToDelete?.let { preset ->
        ConfirmDeletePresetDialog(
            preset = preset,
            onDismiss = { presetToDelete = null },
            onDelete = {
                scope.launch { soundRepository.deleteSequencePreset(preset.id) }
                presetToDelete = null
            }
        )
    }
}

private fun PrankSound.toSequenceStep(): SequenceStep {
    return SequenceStep(
        id = UUID.randomUUID().toString(),
        soundId = id,
        soundName = name,
        assetPath = localUri ?: assetPath,
        delayAfterMs = 500L,
        category = category
    )
}

private suspend fun runSequence(
    steps: List<SequenceStep>,
    repeatCount: Int,
    catalogById: Map<String, PrankSound>,
    audioPlayerController: AudioPlayerController,
    onActiveStep: (String?) -> Unit
): String? {
    if (steps.isEmpty()) return null
    var skipped = 0
    repeat(repeatCount.coerceIn(1, 20)) {
        for (step in steps) {
            val sound = catalogById[step.soundId]
            if (sound == null || !audioPlayerController.canPlayPrankSound(sound)) {
                skipped++
                continue
            }

            onActiveStep(step.id)
            val started = audioPlayerController.playPrankSound(sound, isLooping = false)
            if (!started) {
                skipped++
                continue
            }

            delay(sound.durationMs.takeIf { it > 0L } ?: 900L)
            audioPlayerController.stop()
            delay(step.delayAfterMs.coerceAtLeast(0L))
        }
    }
    onActiveStep(null)
    return if (skipped > 0) "Skipped $skipped invalid, missing, or corrupt sequence step(s)." else null
}

@Composable
private fun SequenceHeader(stepCount: Int, presetCount: Int, blockedCount: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "SEQUENCE BUILDER",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 34.sp, letterSpacing = 2.sp),
            color = CyanAccent
        )
        Text(
            "MULTI-SOUND PRANK PLAYLIST HUD",
            color = Color.Gray,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            StatusChip("STEPS", stepCount.toString(), LimeAccent, Modifier.weight(1f))
            StatusChip("PRESETS", presetCount.toString(), CyanAccent, Modifier.weight(1f))
            StatusChip("BLOCKED", blockedCount.toString(), OrangeAccent, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatusChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LabelCaps(label, color = Color.Gray)
        Text(value, color = color, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun PlaybackConsole(
    repeatCount: Int,
    isPlaying: Boolean,
    canPlay: Boolean,
    onRepeatChange: (Int) -> Unit,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onAddSound: () -> Unit,
    onSave: () -> Unit
) {
    HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = if (isPlaying) FuchsiaAccent else CyanAccent) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    LabelCaps("PLAYBACK MATRIX", color = Color.Gray)
                    Text(if (isPlaying) "RUNNING" else "ARMED", color = if (isPlaying) FuchsiaAccent else LimeAccent)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("x$repeatCount", color = CyanAccent, style = MaterialTheme.typography.headlineSmall)
                    IconButton(onClick = { onRepeatChange((repeatCount - 1).coerceAtLeast(1)) }) {
                        Text("-", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    }
                    IconButton(onClick = { onRepeatChange((repeatCount + 1).coerceAtMost(20)) }) {
                        Text("+", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NeonButton(
                    label = if (isPlaying) "PLAYING" else "PLAY",
                    icon = Icons.Default.PlayArrow,
                    color = FuchsiaAccent,
                    enabled = canPlay && !isPlaying,
                    onClick = onPlay,
                    modifier = Modifier.weight(1f)
                )
                NeonButton(
                    label = "STOP",
                    icon = Icons.Default.Stop,
                    color = OrangeAccent,
                    enabled = isPlaying,
                    onClick = onStop,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NeonButton("ADD SOUND", Icons.Default.AddCircle, CyanAccent, true, onAddSound, Modifier.weight(1f))
                NeonButton("SAVE", Icons.Default.Save, LimeAccent, canPlay, onSave, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun EmptySequenceCard(onAddSound: () -> Unit) {
    HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = CyanAccent) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.LibraryMusic, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(34.dp))
            Text("NO STEPS LOCKED", color = CyanAccent, style = MaterialTheme.typography.headlineSmall)
            Text("Add playable catalog sounds to build a real prank sequence.", color = Color.Gray)
            NeonButton("ADD FIRST SOUND", Icons.Default.AddCircle, FuchsiaAccent, true, onAddSound)
        }
    }
}

@Composable
private fun SequenceTimelineCard(
    index: Int,
    step: SequenceStep,
    isActive: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onDelayChange: (Long) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit
) {
    val accent = if (isActive) FuchsiaAccent else CyanAccent
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(if (isActive) 16.dp else 4.dp, RoundedCornerShape(12.dp), ambientColor = accent, spotColor = accent)
                    .background(accent.copy(alpha = if (isActive) 0.22f else 0.08f), RoundedCornerShape(12.dp))
                    .border(1.dp, accent, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text((index + 1).toString().padStart(2, '0'), color = accent, style = MaterialTheme.typography.headlineSmall)
            }
            Box(modifier = Modifier.width(2.dp).height(74.dp).background(Brush.verticalGradient(listOf(accent.copy(alpha = 0.8f), Color.Transparent))))
        }

        HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = accent) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(step.soundName.uppercase(), color = accent, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("${step.category}  /  ${step.soundId}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                    if (isActive) {
                        LabelCaps("ACTIVE", color = FuchsiaAccent)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    LabelCaps("DELAY AFTER", color = Color.Gray)
                    Text("${step.delayAfterMs}ms", color = LimeAccent)
                    IconButton(onClick = { onDelayChange((step.delayAfterMs - 250L).coerceAtLeast(0L)) }) {
                        Text("-", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    }
                    IconButton(onClick = { onDelayChange((step.delayAfterMs + 250L).coerceAtMost(10_000L)) }) {
                        Text("+", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onMoveUp, enabled = canMoveUp) {
                        Icon(Icons.Default.ArrowUpward, "Move step up", tint = if (canMoveUp) CyanAccent else Color.DarkGray)
                    }
                    IconButton(onClick = onMoveDown, enabled = canMoveDown) {
                        Icon(Icons.Default.ArrowDownward, "Move step down", tint = if (canMoveDown) CyanAccent else Color.DarkGray)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Remove step", tint = Color(0xFFF87171))
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetConsole(
    presets: List<SoundSequencePreset>,
    onLoad: (SoundSequencePreset) -> Unit,
    onDelete: (SoundSequencePreset) -> Unit
) {
    HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = LimeAccent) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            LabelCaps("SAVED PRESETS", color = LimeAccent)
            if (presets.isEmpty()) {
                Text("No saved sequences yet.", color = Color.Gray)
            } else {
                presets.forEach { preset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GlassBackground, RoundedCornerShape(8.dp))
                            .border(1.dp, CyanAccent.copy(alpha = 0.24f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(preset.name, color = CyanAccent, fontWeight = FontWeight.Bold)
                            Text("${preset.steps.size} steps  /  x${preset.repeatCount}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { onLoad(preset) }) { Text("LOAD", color = LimeAccent) }
                        IconButton(onClick = { onDelete(preset) }) {
                            Icon(Icons.Default.Delete, "Delete preset", tint = Color(0xFFF87171))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoundPickerDialog(
    sounds: List<PrankSound>,
    blockedCount: Int,
    onDismiss: () -> Unit,
    onAdd: (PrankSound) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = sounds.filter { sound ->
        query.isBlank() ||
            sound.name.contains(query, true) ||
            sound.category.contains(query, true) ||
            sound.tags.any { it.contains(query, true) } ||
            (sound.packId?.contains(query, true) == true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF080B12),
        title = { Text("ADD CATALOG SOUND", color = CyanAccent) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search name, category, tag, pack") },
                    colors = neonTextFieldColors()
                )
                if (blockedCount > 0) {
                    Text("$blockedCount invalid or missing catalog sound(s) blocked from picker.", color = OrangeAccent, style = MaterialTheme.typography.bodySmall)
                }
                LazyColumn(modifier = Modifier.height(320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered, key = { it.id }) { sound ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GlassBackground, RoundedCornerShape(8.dp))
                                .border(1.dp, CyanAccent.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .clickable { onAdd(sound) }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(sound.name, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("${sound.category}  /  ${sound.packId ?: "UNPACKED"}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                            }
                            Icon(Icons.Default.AddCircle, contentDescription = "Add sound", tint = FuchsiaAccent)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("DONE", color = CyanAccent) } }
    )
}

@Composable
private fun SavePresetDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF080B12),
        title = { Text("SAVE SEQUENCE PRESET", color = LimeAccent) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Preset name") },
                colors = neonTextFieldColors()
            )
        },
        confirmButton = { TextButton(onClick = onSave) { Text("SAVE", color = LimeAccent) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL", color = Color.Gray) } }
    )
}

@Composable
private fun ConfirmDeletePresetDialog(
    preset: SoundSequencePreset,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF080B12),
        title = { Text("DELETE PRESET?", color = OrangeAccent) },
        text = { Text("Remove '${preset.name}' from saved sequence presets.", color = Color.White) },
        confirmButton = { TextButton(onClick = onDelete) { Text("DELETE", color = Color(0xFFF87171)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL", color = Color.Gray) } }
    )
}

@Composable
private fun NeonButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = color,
            disabledContentColor = Color.DarkGray
        ),
        border = BorderStroke(1.dp, if (enabled) color else Color.DarkGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(icon, contentDescription = label, tint = if (enabled) color else Color.DarkGray)
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, color = if (enabled) color else Color.DarkGray, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun neonTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = CyanAccent,
    unfocusedBorderColor = Color.Gray,
    cursorColor = FuchsiaAccent,
    focusedLabelColor = CyanAccent,
    unfocusedLabelColor = Color.Gray,
    focusedPlaceholderColor = Color.Gray,
    unfocusedPlaceholderColor = Color.Gray,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent
)
