package com.pranksterlab.screens.soundforge

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranksterlab.components.*
import com.pranksterlab.components.soundforge.GeneratedWaveformView
import com.pranksterlab.core.model.SoundForgeGeneratorType
import com.pranksterlab.theme.*

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top: Generator Carousel
        Box(modifier = Modifier.fillMaxWidth().background(SurfaceDark).padding(vertical = 12.dp)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(SoundForgeGeneratorType.values()) { type ->
                    val isSelected = type == selectedType
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setGeneratorType(type) },
                        color = if (isSelected) PrimaryContainer else Color.Transparent,
                        border = if (isSelected) null else BorderStroke(1.dp, OutlineDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = type.displayName.uppercase(),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }
        }

        // Center: Waveform Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Brush.verticalGradient(listOf(Color.Black, SurfaceDark.copy(alpha = 0.5f), Color.Black))),
            contentAlignment = Alignment.Center
        ) {
            if (isGenerating) {
                CircularProgressIndicator(color = CyanAccent)
            } else if (result?.waveformPeaks?.isNotEmpty() == true) {
                GeneratedWaveformView(
                    peaks = result!!.waveformPeaks,
                    modifier = Modifier.fillMaxWidth(0.9f).height(120.dp),
                    color = if (isPlaying) FuchsiaAccent else CyanAccent
                )
            } else {
                Text("READY TO FORGE", color = OutlineDark, style = MaterialTheme.typography.labelMedium)
            }
        }

        Row(modifier = Modifier.weight(1f)) {
            // Left: Macro Controls
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabelCaps(text = "CORE PARAMETERS", color = CyanAccent)
                
                ParameterItem("Duration", "${params.durationMs}ms") {
                    Slider(
                        value = params.durationMs.toFloat(),
                        onValueChange = { viewModel.updateParams { p -> p.copy(durationMs = it.toLong()) } },
                        valueRange = 100f..5000f,
                        colors = sliderColors()
                    )
                }

                // Generator specific params
                params.customParams.forEach { (key, value) ->
                    ParameterItem(key, String.format("%.2f", value)) {
                        Slider(
                            value = value,
                            onValueChange = { viewModel.updateCustomParam(key, it) },
                            valueRange = 0f..1f,
                            colors = sliderColors()
                        )
                    }
                }

                ParameterItem("Volume", String.format("%.2f", params.volume)) {
                    Slider(
                        value = params.volume,
                        onValueChange = { viewModel.updateParams { p -> p.copy(volume = it) } },
                        valueRange = 0.1f..1.0f,
                        colors = sliderColors()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                LabelCaps(text = "RECENT FORGES", color = CyanAccent.copy(alpha = 0.5f))
                viewModel.history.forEach { item ->
                    RecentForgeItem(item) {
                        viewModel.generatedResult.value = item
                    }
                }
            }

            // Right: FX Chain
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .background(SurfaceDark.copy(alpha = 0.3f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LabelCaps(text = "FX CHAIN", color = FuchsiaAccent)
                
                fxChain.forEach { (fx, active) ->
                    FXToggle(fx, active) { viewModel.toggleFx(fx) }
                }

                Spacer(modifier = Modifier.height(16.dp))
                LabelCaps(text = "PRESETS", color = Color.White)
                viewModel.presets.forEach { preset ->
                    PresetItem(preset.name, preset.type == selectedType) {
                        viewModel.applyPreset(preset)
                    }
                }
            }
        }

        // Bottom: Action Bar
        Surface(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = { viewModel.randomizeSeed() }, enabled = !isSeedLocked) {
                    Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = if (isSeedLocked) OutlineDark else Color.White)
                }
                
                IconButton(onClick = { viewModel.isSeedLocked.value = !isSeedLocked }) {
                    Icon(
                        if (isSeedLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Lock",
                        tint = if (isSeedLocked) CyanAccent else Color.White
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (result != null && !isGenerating) {
                    Button(
                        onClick = { 
                            if (isPlaying) onStopPreview() else onPreview(result!!.fileUri)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isPlaying) Color.Red else FuchsiaAccent)
                    ) {
                        Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isPlaying) "STOP" else "PREVIEW")
                    }
                }

                Button(
                    onClick = { viewModel.generate() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
                    modifier = Modifier.height(50.dp),
                    enabled = !isGenerating
                ) {
                    Text(if (isGenerating) "FORGING..." else "FORGE SOUND", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ParameterItem(label: String, value: String, content: @Composable () -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = OutlineLight, style = MaterialTheme.typography.labelMedium)
            Text(value, color = CyanAccent, style = MaterialTheme.typography.labelSmall)
        }
        content()
    }
}

@Composable
fun FXToggle(name: String, active: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).clickable { onClick() },
        color = if (active) FuchsiaAccent.copy(alpha = 0.2f) else Color.Transparent,
        border = BorderStroke(1.dp, if (active) FuchsiaAccent else OutlineDark.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (active) FuchsiaAccent else OutlineDark)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, color = if (active) Color.White else OutlineDark, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun PresetItem(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = name,
        color = if (isSelected) Color.White else OutlineDark,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 4.dp)
    )
}

@Composable
fun sliderColors() = SliderDefaults.colors(
    thumbColor = CyanAccent,
    activeTrackColor = CyanAccent,
    inactiveTrackColor = OutlineDark.copy(alpha = 0.1f)
)

@Composable
fun RecentForgeItem(item: com.pranksterlab.core.model.GeneratedSoundResult, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() },
        color = SurfaceBright.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, OutlineDark.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(CyanAccent.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.History, null, modifier = Modifier.size(12.dp), tint = CyanAccent)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(item.name, color = Color.White, style = MaterialTheme.typography.labelSmall)
                Text(item.generatorType.displayName, color = OutlineDark, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
