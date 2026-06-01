package com.pranksterlab.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.R
import com.pranksterlab.components.HUDCard
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.components.PrankstarHeader
import com.pranksterlab.components.ScanlineOverlay
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.ErrorRed
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OnBackground
import com.pranksterlab.theme.OrangeAccent
import com.pranksterlab.theme.OutlineDark
import com.pranksterlab.theme.OutlineLight
import com.pranksterlab.theme.PrimaryContainer
import com.pranksterlab.theme.SurfaceBright
import com.pranksterlab.theme.SurfaceDark
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun RandomizerScreen(
    soundRepository: SoundRepository,
    audioPlayerController: AudioPlayerController
) {
    val viewModel = androidx.compose.runtime.remember {
        RandomizerViewModel(soundRepository, audioPlayerController)
    }

    LaunchedEffect(Unit) {
        viewModel.loadSounds()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.dispose()
        }
    }

    val state by viewModel.uiState
    val minDelay by viewModel.minDelaySeconds
    val maxDelay by viewModel.maxDelaySeconds
    val loopCount by viewModel.loopCount
    val filteredCount = viewModel.filteredSounds.size
    val categories = viewModel.categories

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        ScanlineOverlay()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PrankstarHeader(
                    title = "Randomizer",
                    subtitle = "Chaos Algorithm Engine",
                    imageRes = R.drawable.header_sound_gen,
                    statusLabel = if (state.isRunning) "RUNNING" else "ARMED",
                    showTextOverlay = false,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                RandomizerHeader(
                    isRunning = state.isRunning,
                    loadedCount = state.sounds.size,
                    filteredCount = filteredCount
                )
            }

            item {
                CategorySelectorPanel(
                    categories = categories,
                    selectedCategories = viewModel.selectedCategories,
                    filteredCount = filteredCount,
                    isRunning = state.isRunning,
                    onToggle = viewModel::toggleCategory
                )
            }

            item {
                DelayRangePanel(
                    minDelay = minDelay,
                    maxDelay = maxDelay,
                    loopCount = loopCount,
                    continuousMode = state.continuousMode,
                    isRunning = state.isRunning,
                    onMinDelay = viewModel::setMinDelay,
                    onMaxDelay = viewModel::setMaxDelay,
                    onContinuous = viewModel::setContinuousMode,
                    onLoopCount = viewModel::setLoopCount
                )
            }

            item {
                CurrentSoundPanel(
                    currentSoundName = state.currentSound?.name,
                    currentCategory = state.currentSound?.category,
                    countdownMs = state.upcomingDelayMs,
                    completedPlays = state.completedPlays,
                    skippedInvalid = state.skippedInvalid,
                    isRunning = state.isRunning,
                    status = state.status
                )
            }

            item {
                SafeModeCard(
                    safeMode = state.safeMode,
                    includeGeneratedVoiceClips = state.includeGeneratedVoiceClips,
                    isRunning = state.isRunning,
                    safeCount = state.sounds.count { it.isSafeForRandomMode },
                    onSafeMode = viewModel::setSafeMode,
                    onIncludeGenerated = viewModel::setIncludeGeneratedVoiceClips
                )
            }

            item {
                RandomizerActions(
                    isRunning = state.isRunning,
                    canStart = filteredCount > 0 && viewModel.selectedCategories.isNotEmpty(),
                    onStart = viewModel::startRandomizer,
                    onStop = viewModel::stopRandomizer,
                    onStopAll = {
                        viewModel.stopRandomizer()
                        audioPlayerController.stopAll()
                    }
                )
            }
        }
    }
}

@Composable
private fun RandomizerHeader(
    isRunning: Boolean,
    loadedCount: Int,
    filteredCount: Int
) {
    val pulse by rememberInfiniteTransition(label = "randomizer-pulse").animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "pulse"
    )

    HUDCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        accentColor = if (isRunning) FuchsiaAccent else LimeAccent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Shuffle, contentDescription = null, tint = if (isRunning) FuchsiaAccent else CyanAccent)
                    LabelCaps("Chaos Algorithm", color = OutlineLight)
                }
                Text(
                    text = "RANDOMIZER",
                    color = LimeAccent,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$loadedCount loaded | $filteredCount armed",
                    color = OutlineLight,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background((if (isRunning) FuchsiaAccent else CyanAccent).copy(alpha = 0.12f + pulse * 0.1f))
                    .border(1.dp, (if (isRunning) FuchsiaAccent else CyanAccent).copy(alpha = pulse), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = if (isRunning) FuchsiaAccent else CyanAccent,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
private fun CategorySelectorPanel(
    categories: List<String>,
    selectedCategories: List<String>,
    filteredCount: Int,
    isRunning: Boolean,
    onToggle: (String) -> Unit
) {
    NeonSection(title = "Selected Category Chips", accent = CyanAccent) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("MULTI-SELECT FILTER", color = OnBackground, style = MaterialTheme.typography.labelLarge)
                Text("$filteredCount SOUNDS", color = LimeAccent, style = MaterialTheme.typography.labelSmall)
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    NeonCategoryChip(
                        label = category,
                        selected = category in selectedCategories,
                        enabled = !isRunning,
                        onClick = { onToggle(category) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NeonCategoryChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val accent = categoryAccent(label)
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) accent.copy(alpha = 0.18f) else SurfaceDark.copy(alpha = 0.9f))
            .border(1.dp, if (selected) accent else OutlineDark.copy(alpha = 0.65f), CircleShape)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = if (selected) Color.White else accent.copy(alpha = if (enabled) 0.82f else 0.35f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1
        )
    }
}

@Composable
private fun DelayRangePanel(
    minDelay: Float,
    maxDelay: Float,
    loopCount: Int,
    continuousMode: Boolean,
    isRunning: Boolean,
    onMinDelay: (Float) -> Unit,
    onMaxDelay: (Float) -> Unit,
    onContinuous: (Boolean) -> Unit,
    onLoopCount: (Int) -> Unit
) {
    NeonSection(title = "Delay Range Panel", accent = FuchsiaAccent) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            RandomizerSlider(
                label = "Min Delay",
                value = minDelay,
                valueText = "${minDelay.toInt()}s",
                enabled = !isRunning,
                accent = CyanAccent,
                onValueChange = onMinDelay
            )
            RandomizerSlider(
                label = "Max Delay",
                value = maxDelay,
                valueText = "${maxDelay.toInt()}s",
                enabled = !isRunning,
                accent = FuchsiaAccent,
                onValueChange = onMaxDelay
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = OrangeAccent)
                Column(modifier = Modifier.weight(1f)) {
                    Text("CONTINUOUS MODE", color = Color.White, style = MaterialTheme.typography.labelMedium)
                    Text(
                        if (continuousMode) "Runs until stopped" else "$loopCount plays then stops",
                        color = OutlineLight,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = continuousMode,
                    enabled = !isRunning,
                    onCheckedChange = onContinuous,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LimeAccent,
                        checkedTrackColor = LimeAccent.copy(alpha = 0.28f),
                        uncheckedThumbColor = OutlineLight,
                        uncheckedTrackColor = SurfaceBright
                    )
                )
            }
            if (!continuousMode) {
                RandomizerSlider(
                    label = "Loop Count",
                    value = loopCount.toFloat(),
                    valueText = "$loopCount",
                    enabled = !isRunning,
                    accent = LimeAccent,
                    range = 1f..20f,
                    steps = 18,
                    onValueChange = { onLoopCount(it.toInt()) }
                )
            }
        }
    }
}

@Composable
private fun CurrentSoundPanel(
    currentSoundName: String?,
    currentCategory: String?,
    countdownMs: Long,
    completedPlays: Int,
    skippedInvalid: Int,
    isRunning: Boolean,
    status: String
) {
    NeonSection(title = "Current Sound Panel", accent = if (isRunning) LimeAccent else OutlineDark) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LiveWaveform(isRunning = isRunning, modifier = Modifier.size(width = 92.dp, height = 46.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentSoundName?.uppercase() ?: "NO SOUND ACTIVE",
                        color = if (currentSoundName == null) OutlineLight else Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(fontStyle = FontStyle.Italic),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentCategory ?: status,
                        color = if (currentCategory == null) OutlineLight else categoryAccent(currentCategory),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                ReadoutCell("COUNTDOWN", if (countdownMs > 0) "${(countdownMs + 999) / 1000}s" else "READY", CyanAccent, Modifier.weight(1f))
                ReadoutCell("PLAYED", "$completedPlays", LimeAccent, Modifier.weight(1f))
                ReadoutCell("SKIPPED", "$skippedInvalid", OrangeAccent, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SafeModeCard(
    safeMode: Boolean,
    includeGeneratedVoiceClips: Boolean,
    isRunning: Boolean,
    safeCount: Int,
    onSafeMode: (Boolean) -> Unit,
    onIncludeGenerated: (Boolean) -> Unit
) {
    NeonSection(title = "Safe Mode Card", accent = LimeAccent) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.Security, contentDescription = null, tint = LimeAccent, modifier = Modifier.size(30.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("PRANK-SAFE RANDOM MODE", color = Color.White, style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = "Excludes unsafe catalog entries, known invalid assets, corrupt files, and long known-duration sounds. $safeCount catalog sounds are marked safe.",
                        color = OutlineLight,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = safeMode,
                    enabled = !isRunning,
                    onCheckedChange = onSafeMode,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LimeAccent,
                        checkedTrackColor = LimeAccent.copy(alpha = 0.25f),
                        uncheckedThumbColor = OutlineLight,
                        uncheckedTrackColor = SurfaceBright
                    )
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Include Generated Voice Clips", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Switch(
                checked = includeGeneratedVoiceClips,
                enabled = !isRunning,
                onCheckedChange = onIncludeGenerated,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyanAccent,
                    checkedTrackColor = CyanAccent.copy(alpha = 0.25f),
                    uncheckedThumbColor = OutlineLight,
                    uncheckedTrackColor = SurfaceBright
                )
            )
            }
        }
    }
}

@Composable
private fun RandomizerActions(
    isRunning: Boolean,
    canStart: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onStopAll: () -> Unit
) {
    NeonSection(title = "Start / Stop Action", accent = if (isRunning) ErrorRed else LimeAccent) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onStart,
                    enabled = !isRunning && canStart,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color.White),
                    modifier = Modifier.weight(1f).height(54.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("START RANDOMIZER")
                }
                OutlinedButton(
                    onClick = onStop,
                    enabled = isRunning,
                    border = BorderStroke(1.dp, ErrorRed),
                    modifier = Modifier.weight(1f).height(54.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, tint = ErrorRed)
                    Spacer(Modifier.width(8.dp))
                    Text("STOP", color = ErrorRed)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ErrorRed.copy(alpha = 0.13f))
                    .border(1.dp, ErrorRed.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                    .clickable { onStopAll() }
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Dangerous, contentDescription = null, tint = ErrorRed)
                    Column {
                        Text("STOP ALL CONTROL", color = ErrorRed, style = MaterialTheme.typography.labelLarge)
                        Text("Cancels the randomizer loop and stops any active audio.", color = OutlineLight, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun NeonSection(
    title: String,
    accent: Color,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceDark.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.34f)),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.16f), Color.Transparent)))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accent))
                LabelCaps(title, color = accent)
            }
            content()
        }
    }
}

@Composable
private fun RandomizerSlider(
    label: String,
    value: Float,
    valueText: String,
    enabled: Boolean,
    accent: Color,
    range: ClosedFloatingPointRange<Float> = 1f..60f,
    steps: Int = 58,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label.uppercase(), color = Color.White, style = MaterialTheme.typography.labelMedium)
            Text(valueText, color = accent, style = MaterialTheme.typography.labelMedium)
        }
        Slider(
            value = value.coerceIn(range.start, range.endInclusive),
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = OutlineDark.copy(alpha = 0.5f),
                disabledThumbColor = OutlineDark,
                disabledActiveTrackColor = OutlineDark.copy(alpha = 0.4f),
                disabledInactiveTrackColor = OutlineDark.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun ReadoutCell(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceBright.copy(alpha = 0.72f))
            .border(1.dp, accent.copy(alpha = 0.34f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            LabelCaps(label, color = OutlineLight)
            Text(value, color = accent, style = MaterialTheme.typography.headlineSmall, maxLines = 1)
        }
    }
}

@Composable
private fun LiveWaveform(
    isRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val phase by rememberInfiniteTransition(label = "randomizer-waveform").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(if (isRunning) 720 else 1600), RepeatMode.Restart),
        label = "phase"
    )
    Canvas(modifier = modifier) {
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.34f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
        )
        drawRoundRect(
            color = CyanAccent.copy(alpha = 0.32f),
            style = Stroke(width = 1.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
        )
        val bars = 18
        val barWidth = size.width / bars
        repeat(bars) { index ->
            val energy = if (isRunning) 0.35f + 0.65f * kotlin.math.abs(sin((phase * 8f + index * 0.55f) * PI)).toFloat() else 0.24f
            val h = size.height * energy
            val x = index * barWidth + barWidth / 2f
            drawLine(
                color = if (isRunning) FuchsiaAccent else CyanAccent.copy(alpha = 0.55f),
                start = Offset(x, size.height / 2f - h / 2f),
                end = Offset(x, size.height / 2f + h / 2f),
                strokeWidth = (barWidth * 0.48f).coerceAtLeast(2f),
                cap = StrokeCap.Round
            )
        }
    }
}

private fun categoryAccent(category: String): Color = when (category.uppercase()) {
    "FUNNY", "CARTOON", "REACTION", "MEME_REACTIONS" -> FuchsiaAccent
    "CREEPY", "HORROR_LITE", "MONSTER" -> ErrorRed
    "VOICE", "ROBOT", "SCI_FI", "GLITCH" -> CyanAccent
    "AMBIENCE", "OFFICE", "PHONE" -> OrangeAccent
    else -> LimeAccent
}
