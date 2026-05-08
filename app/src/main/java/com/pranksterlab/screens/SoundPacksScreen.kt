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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.R
import com.pranksterlab.components.HUDCard
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.components.PrankstarHeader
import com.pranksterlab.components.ScanlineOverlay
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.repository.PackSummary
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.GlassBackground
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrangeAccent

@Composable
fun SoundPacksScreen(soundRepository: SoundRepository, audioPlayerController: AudioPlayerController, onOpenLibrary: () -> Unit) {
    var bundledSounds by remember { mutableStateOf(emptyList<PrankSound>()) }
    var customSounds by remember { mutableStateOf(emptyList<PrankSound>()) }

    LaunchedEffect(Unit) {
        bundledSounds = soundRepository.getBundledSounds()
        soundRepository.getCustomSoundsFlow().collect { customSounds = it }
    }

    val validSounds = (bundledSounds + customSounds).filter { soundRepository.isSoundPlayable(it) }
    val packSummaries = soundRepository.buildPackSummaries(validSounds)

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        ScanlineOverlay()

        Column(modifier = Modifier.fillMaxSize()) {
            PrankstarHeader(
                title = "Sound Packs",
                subtitle = "Featured Data Pack Catalogue",
                imageRes = R.drawable.prankstar_sn2,
                statusLabel = "${packSummaries.size} PACKS",
                modifier = Modifier.padding(top = 8.dp)
            )
            Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp)) {
            HeadlineText("FEATURED DATA PACKS", color = CyanAccent)
            Text(
                "REAL CATALOG PACKS  /  ${validSounds.size} VALID SIGNALS",
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (packSummaries.isEmpty()) {
                HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = CyanAccent) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("NO VALID PACKS", color = CyanAccent)
                        Text("Catalog packs appear once valid playable assets are detected.", color = Color.Gray)
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(168.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(packSummaries, key = { it.packId }) { pack ->
                    val packSounds = validSounds.filter { it.packId == pack.packId }
                    PackCard(
                        pack = pack,
                        sampleSound = packSounds.firstOrNull(),
                        onPreview = {
                            val sample = packSounds.randomOrNull() ?: return@PackCard
                            audioPlayerController.playPrankSound(sample, false)
                        },
                        onOpen = {
                            soundRepository.setActivePackFilter(pack.packId)
                            onOpenLibrary()
                        }
                    )
                }
            }
            }
        }
    }
}

@Composable
fun PackCard(pack: PackSummary, sampleSound: PrankSound?, onPreview: () -> Unit, onOpen: () -> Unit) {
    val color = when (pack.categoryFocus) {
        "VOICE" -> OrangeAccent
        "CREEPY" -> FuchsiaAccent
        "AMBIENCE" -> CyanAccent
        else -> LimeAccent
    }

    HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = color) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .background(GlassBackground, RoundedCornerShape(10.dp))
                    .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            ) {
                NeonWaveform(
                    seed = pack.packId,
                    color = color,
                    modifier = Modifier.fillMaxWidth().height(72.dp).align(Alignment.Center)
                )
                LabelCaps(
                    pack.categoryFocus,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(color, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            HeadlineText(pack.packId.uppercase(), color = color)
            Text("${pack.soundCount} VALID SOUNDS", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Text(
                sampleSound?.name ?: "No preview sample",
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = onPreview, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))) {
                    Icon(Icons.Default.PlayArrow, "Preview random sound from pack", tint = color)
                }
                Button(onClick = onOpen, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))) {
                    Icon(Icons.Default.FilterAlt, "Open pack in library", tint = CyanAccent)
                }
            }
        }
    }
}
