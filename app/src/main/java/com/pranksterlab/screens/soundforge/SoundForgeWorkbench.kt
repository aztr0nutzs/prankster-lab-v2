package com.pranksterlab.screens.soundforge

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pranksterlab.R
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.components.PrankstarHeader
import com.pranksterlab.core.model.GeneratedSoundResult
import com.pranksterlab.core.model.SoundForgeGeneratorType
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.ErrorRed
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrangeAccent
import com.pranksterlab.theme.OutlineDark
import com.pranksterlab.theme.OutlineLight
import com.pranksterlab.theme.PrimaryContainer
import com.pranksterlab.theme.SurfaceBright
import com.pranksterlab.theme.SurfaceDark
import kotlin.math.PI
import kotlin.math.sin

private data class MacroParameter(
    val name: String,
    val minimum: Float = 0f,
    val maximum: Float = 1f,
    val format: (Float) -> String = { String.format("%.2f", it) }
)

private val fxControls = listOf("Echo", "Reverb", "Wobble", "Bitcrush", "Distortion", "Low-pass", "Reverse", "Stutter")

@Composable
fun SoundForgeWorkbench(
    viewModel: SoundForgeViewModel,
    onBack: () -> Unit,
    onPreview: (String) -> Unit,
    onStopPreview: () -> Unit,
    isPlaying: Boolean
) {
    val selectedType by viewModel.selectedType
    val params by viewModel.parameters
    val isGenerating by viewModel.isGenerating
    val result by viewModel.generatedResult
    val isSeedLocked by viewModel.isSeedLocked
    val fxChain by viewModel.fxChain
    val savedConfirmation by viewModel.savedConfirmation
    val saveError by viewModel.saveError

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF102C35), BackgroundDark, Color.Black),
                    radius = 1200f
                )
            ),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            PrankstarHeader(
                title = "Sound Forge",
                subtitle = "Procedural Synthesis Lab",
                imageRes = R.drawable.header_sound_gen,
                statusLabel = when {
                    isGenerating -> "RENDER"
                    isPlaying -> "PREVIEW"
                    else -> "ARMED"
                },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
            )
        }
        item {
            WorkbenchHeader(
                selectedType = selectedType,
                isGenerating = isGenerating,
                isPlaying = isPlaying
            )
        }

        item {
            GeneratorModeCarousel(
                selectedType = selectedType,
                onSelect = viewModel::setGeneratorType
            )
        }

        item {
            AnimatedWaveformPreview(
                result = result,
                isGenerating = isGenerating,
                isPlaying = isPlaying
            )
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MacroParameterConsole(
                    type = selectedType,
                    durationMs = params.durationMs,
                    volume = params.volume,
                    values = params.customParams,
                    onDurationChange = { value ->
                        viewModel.updateParams { it.copy(durationMs = value.toLong()) }
                    },
                    onVolumeChange = { value ->
                        viewModel.updateParams { it.copy(volume = value) }
                    },
                    onValueChange = viewModel::updateCustomParam
                )

                FxChainPanel(
                    fxChain = fxChain,
                    values = params.customParams,
                    onToggle = viewModel::toggleFx,
                    onAmountChange = viewModel::updateFxAmount
                )

                SeedAndPresetPanel(
                    seed = params.seed,
                    isSeedLocked = isSeedLocked,
                    presets = viewModel.presets,
                    selectedType = selectedType,
                    onShuffleSeed = viewModel::randomizeSeed,
                    onToggleSeedLock = { viewModel.isSeedLocked.value = !isSeedLocked },
                    onRandomizeParameters = viewModel::randomizeAllParameters,
                    onReset = viewModel::reset,
                    onPreset = viewModel::applyPreset
                )

                PreviewSaveControls(
                    hasResult = result?.errorMessage == null && result != null,
                    isGenerating = isGenerating,
                    isPlaying = isPlaying,
                    onGenerate = viewModel::generate,
                    onPreview = {
                        val uri = result?.fileUri?.toString().orEmpty()
                        if (uri.isNotBlank()) onPreview(uri)
                    },
                    onStopPreview = onStopPreview,
                    onSave = { viewModel.saveGeneratedSound(result?.name.orEmpty(), "CUSTOM") }
                )

                GeneratedSoundStatusPanel(
                    result = result,
                    savedConfirmation = savedConfirmation,
                    saveError = saveError
                )

                ResponsibleUseWarningCard()
            }
        }
    }
}

@Composable
private fun WorkbenchHeader(
    selectedType: SoundForgeGeneratorType,
    isGenerating: Boolean,
    isPlaying: Boolean
) {
    val pulse by rememberInfiniteTransition(label = "forge-header").animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Color(0xFF071114), Color(0xFF151226), Color(0xFF071114))))
            .border(1.dp, CyanAccent.copy(alpha = 0.18f))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            LabelCaps("Sound Forge Workbench", color = CyanAccent)
            Text(
                text = selectedType.displayName,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = when {
                    isGenerating -> "render core active"
                    isPlaying -> "preview monitor live"
                    else -> "procedural prank audio lab"
                }.uppercase(),
                color = if (isPlaying) FuchsiaAccent else LimeAccent.copy(alpha = pulse),
                style = MaterialTheme.typography.labelSmall
            )
        }

        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatusLamp(CyanAccent, pulse)
            StatusLamp(FuchsiaAccent, if (isPlaying) pulse else 0.18f)
            StatusLamp(LimeAccent, if (isGenerating) pulse else 0.28f)
        }
    }
}

@Composable
private fun StatusLamp(color: Color, alpha: Float) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha.coerceIn(0.15f, 1f)))
    )
}

@Composable
private fun GeneratorModeCarousel(
    selectedType: SoundForgeGeneratorType,
    onSelect: (SoundForgeGeneratorType) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 14.dp)) {
        LabelCaps(
            text = "Generator Mode Carousel",
            color = OutlineLight,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(SoundForgeGeneratorType.values()) { type ->
                val selected = type == selectedType
                val borderColor by animateColorAsState(
                    targetValue = if (selected) CyanAccent else OutlineDark.copy(alpha = 0.55f),
                    label = "mode-border"
                )
                Surface(
                    modifier = Modifier
                        .width(150.dp)
                        .height(78.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelect(type) },
                    color = if (selected) Color(0xFF08262C) else SurfaceDark.copy(alpha = 0.88f),
                    border = BorderStroke(1.dp, borderColor),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = if (selected) 8.dp else 0.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeColor = if (selected) CyanAccent.copy(alpha = 0.55f) else OutlineDark.copy(alpha = 0.25f)
                            repeat(4) { index ->
                                val y = size.height * (0.25f + index * 0.16f)
                                drawLine(strokeColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                            }
                        }
                        Column(
                            modifier = Modifier.align(Alignment.BottomStart),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = if (selected) CyanAccent else OutlineLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = type.displayName.uppercase(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedWaveformPreview(
    result: GeneratedSoundResult?,
    isGenerating: Boolean,
    isPlaying: Boolean
) {
    val sweep by rememberInfiniteTransition(label = "waveform-sweep").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(if (isPlaying) 900 else 1800)),
        label = "sweep"
    )
    val energy by animateFloatAsState(
        targetValue = when {
            isPlaying -> 1f
            isGenerating -> 0.72f
            result?.waveformPeaks?.isNotEmpty() == true -> 0.52f
            else -> 0.22f
        },
        label = "energy"
    )
    val peaks = result?.waveformPeaks.orEmpty()

    NeonPanel(
        title = "Animated Waveform Preview",
        accent = if (isPlaying) FuchsiaAccent else CyanAccent,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.55f))
                .border(1.dp, CyanAccent.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridColor = Color(0xFF244A52).copy(alpha = 0.34f)
                val centerY = size.height / 2f
                repeat(7) { index ->
                    val y = size.height * index / 6f
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                }
                repeat(12) { index ->
                    val x = size.width * index / 11f
                    drawLine(gridColor.copy(alpha = 0.22f), Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                }
                drawLine(CyanAccent.copy(alpha = 0.25f), Offset(0f, centerY), Offset(size.width, centerY), strokeWidth = 2f)

                if (peaks.isNotEmpty()) {
                    val barWidth = size.width / peaks.size
                    peaks.forEachIndexed { index, peak ->
                        val x = index * barWidth + barWidth / 2f
                        val animatedPeak = (peak * (0.86f + 0.14f * sin((index / 5f + sweep * 8f) * PI).toFloat())) * energy
                        val height = (animatedPeak * size.height * 0.9f).coerceAtLeast(5f)
                        drawLine(
                            brush = Brush.verticalGradient(listOf(FuchsiaAccent.copy(alpha = 0.35f), CyanAccent, LimeAccent.copy(alpha = 0.5f))),
                            start = Offset(x, centerY - height / 2f),
                            end = Offset(x, centerY + height / 2f),
                            strokeWidth = (barWidth * 0.68f).coerceIn(2f, 8f),
                            cap = StrokeCap.Round
                        )
                    }
                } else {
                    val points = 96
                    val path = Path()
                    for (i in 0 until points) {
                        val x = size.width * i / (points - 1)
                        val y = centerY + sin((i * 0.34f + sweep * 12f) * PI).toFloat() * size.height * 0.12f * energy
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(path, CyanAccent.copy(alpha = 0.55f), style = Stroke(width = 3f, cap = StrokeCap.Round))
                }

                val sweepX = size.width * sweep
                drawLine(
                    brush = Brush.verticalGradient(listOf(Color.Transparent, FuchsiaAccent, Color.Transparent)),
                    start = Offset(sweepX, 0f),
                    end = Offset(sweepX, size.height),
                    strokeWidth = 4f
                )
            }

            Text(
                text = when {
                    isGenerating -> "SYNTHESIZING"
                    result?.errorMessage != null -> "BLOCKED"
                    peaks.isNotEmpty() -> "${peaks.size} PEAK BUCKETS | ${result?.durationMs ?: 0} MS"
                    else -> "AWAITING GENERATION"
                },
                color = if (result?.errorMessage != null) ErrorRed else OutlineLight,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)
            )
        }
    }
}

@Composable
private fun MacroParameterConsole(
    type: SoundForgeGeneratorType,
    durationMs: Long,
    volume: Float,
    values: Map<String, Float>,
    onDurationChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onValueChange: (String, Float) -> Unit
) {
    NeonPanel(title = "Macro Parameter Console", accent = LimeAccent) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ParameterStrip(
                label = "Duration",
                value = durationMs.toFloat(),
                range = 120f..5000f,
                valueText = "${durationMs}ms",
                accent = LimeAccent,
                onChange = onDurationChange
            )
            generatorParameters(type).forEachIndexed { index, spec ->
                val accent = when (index % 3) {
                    0 -> CyanAccent
                    1 -> FuchsiaAccent
                    else -> LimeAccent
                }
                val value = values[spec.name] ?: 0.5f
                ParameterStrip(
                    label = spec.name,
                    value = value,
                    range = spec.minimum..spec.maximum,
                    valueText = spec.format(value),
                    accent = accent,
                    onChange = { onValueChange(spec.name, it) }
                )
            }
            ParameterStrip(
                label = "Output Level",
                value = volume,
                range = 0.1f..1f,
                valueText = "${(volume * 100).toInt()}%",
                accent = OrangeAccent,
                onChange = onVolumeChange
            )
        }
    }
}

@Composable
private fun FxChainPanel(
    fxChain: Map<String, Boolean>,
    values: Map<String, Float>,
    onToggle: (String) -> Unit,
    onAmountChange: (String, Float) -> Unit
) {
    NeonPanel(title = "FX Chain Panel", accent = FuchsiaAccent) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            fxControls.forEach { fx ->
                val enabled = fxChain[fx] == true
                val amount = values[fx] ?: defaultFxAmount(fx)
                FxModule(
                    name = fx,
                    enabled = enabled,
                    amount = amount,
                    onToggle = { onToggle(fx) },
                    onAmountChange = { onAmountChange(fx, it) }
                )
            }
        }
    }
}

@Composable
private fun FxModule(
    name: String,
    enabled: Boolean,
    amount: Float,
    onToggle: () -> Unit,
    onAmountChange: (Float) -> Unit
) {
    val glow by animateFloatAsState(if (enabled) 1f else 0.28f, label = "fx-glow")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (enabled) Color(0xFF190F22) else Color(0xFF091113),
        border = BorderStroke(1.dp, (if (enabled) FuchsiaAccent else OutlineDark).copy(alpha = 0.35f + glow * 0.35f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (enabled) FuchsiaAccent.copy(alpha = glow) else OutlineDark)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(name.uppercase(), color = Color.White, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                Switch(
                    checked = enabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = FuchsiaAccent,
                        checkedTrackColor = FuchsiaAccent.copy(alpha = 0.28f),
                        uncheckedThumbColor = OutlineLight,
                        uncheckedTrackColor = SurfaceBright
                    )
                )
            }
            if (name != "Reverse") {
                Slider(
                    value = amount,
                    onValueChange = onAmountChange,
                    valueRange = 0f..1f,
                    enabled = enabled,
                    colors = forgeSliderColors(if (enabled) FuchsiaAccent else OutlineLight)
                )
            } else {
                Text(
                    text = if (enabled) "BUFFER ORDER INVERTED" else "OFFLINE BUFFER FLIP",
                    color = OutlineLight,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun SeedAndPresetPanel(
    seed: Long,
    isSeedLocked: Boolean,
    presets: List<SoundForgePreset>,
    selectedType: SoundForgeGeneratorType,
    onShuffleSeed: () -> Unit,
    onToggleSeedLock: () -> Unit,
    onRandomizeParameters: () -> Unit,
    onReset: () -> Unit,
    onPreset: (SoundForgePreset) -> Unit
) {
    NeonPanel(title = "Seed / Randomization Controls", accent = CyanAccent) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                SeedButton(Icons.Default.Shuffle, "Shuffle Seed", enabled = !isSeedLocked, onClick = onShuffleSeed)
                SeedButton(if (isSeedLocked) Icons.Default.Lock else Icons.Default.LockOpen, "Lock Seed", enabled = true, onClick = onToggleSeedLock)
                SeedButton(Icons.Default.Tune, "Randomize Parameters", enabled = true, onClick = onRandomizeParameters)
                SeedButton(Icons.Default.Refresh, "Reset", enabled = true, onClick = onReset)
            }
            Text(
                text = "SEED ${seed.toString().takeLast(8).padStart(8, '0')} ${if (isSeedLocked) "LOCKED" else "LIVE"}",
                color = if (isSeedLocked) CyanAccent else OutlineLight,
                style = MaterialTheme.typography.labelSmall
            )
            LabelCaps("Presets", color = OutlineLight)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    val selected = preset.type == selectedType
                    Surface(
                        modifier = Modifier
                            .height(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onPreset(preset) },
                        color = if (selected) Color(0xFF0C2832) else SurfaceBright.copy(alpha = 0.74f),
                        border = BorderStroke(1.dp, if (selected) CyanAccent.copy(alpha = 0.72f) else OutlineDark.copy(alpha = 0.42f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                            Text(
                                text = preset.name.uppercase(),
                                color = if (selected) Color.White else OutlineLight,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeedButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (enabled) SurfaceBright else SurfaceDark)
            .border(1.dp, if (enabled) CyanAccent.copy(alpha = 0.34f) else OutlineDark.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
    ) {
        Icon(icon, contentDescription = label, tint = if (enabled) CyanAccent else OutlineDark)
    }
}

@Composable
private fun PreviewSaveControls(
    hasResult: Boolean,
    isGenerating: Boolean,
    isPlaying: Boolean,
    onGenerate: () -> Unit,
    onPreview: () -> Unit,
    onStopPreview: () -> Unit,
    onSave: () -> Unit
) {
    NeonPanel(title = "Preview / Save Controls", accent = OrangeAccent) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onGenerate,
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color.White),
                modifier = Modifier.weight(1.2f).height(52.dp)
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (isGenerating) "FORGING" else "GENERATE")
            }
            OutlinedButton(
                onClick = if (isPlaying) onStopPreview else onPreview,
                enabled = hasResult,
                border = BorderStroke(1.dp, if (isPlaying) ErrorRed else FuchsiaAccent),
                modifier = Modifier.weight(1f).height(52.dp)
            ) {
                Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null, tint = if (isPlaying) ErrorRed else FuchsiaAccent)
                Spacer(Modifier.width(6.dp))
                Text(if (isPlaying) "STOP" else "PREVIEW", color = if (isPlaying) ErrorRed else FuchsiaAccent)
            }
            OutlinedButton(
                onClick = onSave,
                enabled = hasResult,
                border = BorderStroke(1.dp, LimeAccent),
                modifier = Modifier.weight(1f).height(52.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = LimeAccent)
                Spacer(Modifier.width(6.dp))
                Text("SAVE", color = LimeAccent)
            }
        }
    }
}

@Composable
private fun GeneratedSoundStatusPanel(
    result: GeneratedSoundResult?,
    savedConfirmation: String?,
    saveError: String?
) {
    NeonPanel(title = "Generated Sound Status Panel", accent = if (result?.errorMessage != null || saveError != null) ErrorRed else LimeAccent) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (result?.errorMessage != null || saveError != null) Icons.Default.Warning else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (result?.errorMessage != null || saveError != null) ErrorRed else LimeAccent
            )
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = when {
                        result?.errorMessage != null -> "Generation blocked"
                        saveError != null -> "Save failed"
                        savedConfirmation != null -> savedConfirmation
                        result != null -> result.name
                        else -> "No rendered sound yet"
                    },
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = when {
                        result?.errorMessage != null -> result.errorMessage
                        saveError != null -> saveError
                        result != null -> "${result.generatorType.displayName} | ${result.durationMs}ms | WAV ready"
                        else -> "Generate a sound to arm preview and library save."
                    }.orEmpty(),
                    color = OutlineLight,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ResponsibleUseWarningCard() {
    NeonPanel(title = "Responsible-use Warning Card", accent = ErrorRed) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed)
            Text(
                text = "Restricted alert patterns, weapon-like impacts, real-person voices, and copyrighted character voices are not generated or saved.",
                color = OutlineLight,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun NeonPanel(
    title: String,
    accent: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xE6070B0D),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.34f)),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 3.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.16f), Color.Transparent)))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accent))
                Spacer(Modifier.width(8.dp))
                LabelCaps(title, color = accent)
            }
            content()
        }
    }
}

@Composable
private fun ParameterStrip(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    valueText: String,
    accent: Color,
    onChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium)
            Text(valueText, color = accent, style = MaterialTheme.typography.labelSmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Canvas(
                modifier = Modifier
                    .width(48.dp)
                    .aspectRatio(1f)
            ) {
                drawCircle(color = accent.copy(alpha = 0.12f), radius = size.minDimension / 2)
                drawCircle(color = accent.copy(alpha = 0.55f), radius = size.minDimension / 2, style = Stroke(width = 2.dp.toPx()))
                val normalized = ((value - range.start) / (range.endInclusive - range.start)).coerceIn(0f, 1f)
                val angle = (-135f + normalized * 270f) * PI.toFloat() / 180f
                val end = Offset(
                    x = size.width / 2f + kotlin.math.cos(angle) * size.width * 0.33f,
                    y = size.height / 2f + kotlin.math.sin(angle) * size.height * 0.33f
                )
                drawLine(accent, Offset(size.width / 2f, size.height / 2f), end, strokeWidth = 4f, cap = StrokeCap.Round)
            }
            Slider(
                value = value.coerceIn(range.start, range.endInclusive),
                onValueChange = onChange,
                valueRange = range,
                colors = forgeSliderColors(accent),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun forgeSliderColors(accent: Color) = SliderDefaults.colors(
    thumbColor = accent,
    activeTrackColor = accent,
    inactiveTrackColor = OutlineDark.copy(alpha = 0.45f),
    disabledThumbColor = OutlineDark,
    disabledActiveTrackColor = OutlineDark.copy(alpha = 0.35f),
    disabledInactiveTrackColor = OutlineDark.copy(alpha = 0.18f)
)

private fun defaultFxAmount(fx: String): Float = when (fx) {
    "Echo" -> 0.42f
    "Reverb" -> 0.45f
    "Wobble" -> 0.5f
    "Bitcrush" -> 0.5f
    "Distortion" -> 0.35f
    "Low-pass" -> 0.55f
    "Stutter" -> 0.55f
    else -> 1f
}

private fun generatorParameters(type: SoundForgeGeneratorType): List<MacroParameter> = when (type) {
    SoundForgeGeneratorType.SCI_FI_BLIP -> listOf(
        MacroParameter("Pitch"),
        MacroParameter("Sweep"),
        MacroParameter("Echo"),
        MacroParameter("Brightness")
    )
    SoundForgeGeneratorType.GLITCH_BURST -> listOf(
        MacroParameter("Glitch"),
        MacroParameter("Stutter"),
        MacroParameter("Bitcrush"),
        MacroParameter("Chaos"),
        MacroParameter("Fragment Count", format = { "${(2 + it * 22).toInt()}" })
    )
    SoundForgeGeneratorType.ROBOT_BEEP -> listOf(
        MacroParameter("Pitch"),
        MacroParameter("Beep Count", format = { "${(1 + it * 7).toInt()}" }),
        MacroParameter("Spacing"),
        MacroParameter("Robotization")
    )
    SoundForgeGeneratorType.CARTOON_POP -> listOf(
        MacroParameter("Pitch"),
        MacroParameter("Snap"),
        MacroParameter("Pop Count", format = { "${(1 + it * 5).toInt()}" }),
        MacroParameter("Brightness")
    )
    SoundForgeGeneratorType.TOY_SQUEAK -> listOf(
        MacroParameter("Pitch"),
        MacroParameter("Wobble"),
        MacroParameter("Brightness")
    )
    SoundForgeGeneratorType.CREEPY_DRONE -> listOf(
        MacroParameter("Depth"),
        MacroParameter("Wobble"),
        MacroParameter("Noise"),
        MacroParameter("Darkness")
    )
    SoundForgeGeneratorType.MONSTER_GROWL -> listOf(
        MacroParameter("Depth"),
        MacroParameter("Grit"),
        MacroParameter("Wobble"),
        MacroParameter("Throat Size")
    )
    SoundForgeGeneratorType.KNOCK_PATTERN -> listOf(
        MacroParameter("Knock Count", format = { "${(1 + it * 8).toInt()}" }),
        MacroParameter("Spacing"),
        MacroParameter("Room Size"),
        MacroParameter("Wood Tone")
    )
    SoundForgeGeneratorType.FOOTSTEP_PATTERN -> listOf(
        MacroParameter("Step Count", format = { "${(2 + it * 8).toInt()}" }),
        MacroParameter("Spacing"),
        MacroParameter("Surface"),
        MacroParameter("Heaviness")
    )
    SoundForgeGeneratorType.CHAOS_RANDOM -> listOf(
        MacroParameter("Density"),
        MacroParameter("Bitcrush"),
        MacroParameter("Chaos")
    )
}
