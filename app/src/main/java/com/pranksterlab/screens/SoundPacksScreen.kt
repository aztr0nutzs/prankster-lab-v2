package com.pranksterlab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.PlayArrow
import com.pranksterlab.components.GlassPanel
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.repository.PackSummary
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrangeAccent
import kotlinx.coroutines.launch

@Composable
fun SoundPacksScreen(soundRepository: SoundRepository, audioPlayerController: AudioPlayerController, onOpenLibrary: () -> Unit) {
    var bundledSounds by remember { mutableStateOf(emptyList<PrankSound>()) }
    var customSounds by remember { mutableStateOf(emptyList<PrankSound>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        bundledSounds = soundRepository.getBundledSounds()
        soundRepository.getCustomSoundsFlow().collect { customSounds = it }
    }

    val validSounds = (bundledSounds + customSounds).filter { it.isCustom || soundRepository.isCatalogSoundPlayable(it) }
    val packSummaries = soundRepository.buildPackSummaries(validSounds)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        HeadlineText("FEATURED DATA PACKS", color = CyanAccent)
        Spacer(modifier = Modifier.height(16.dp))

        if (packSummaries.isEmpty()) {
            GlassPanel {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("NO VALID PACKS", color = CyanAccent)
                    Text("Catalog packs appear once valid playable assets are detected.", color = Color.Gray)
                }
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(packSummaries, key = { it.packId }) { pack ->
                PackCard(
                    pack = pack,
                    onPreview = {
                        val sample = validSounds.filter { it.packId == pack.packId }.randomOrNull() ?: return@PackCard
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

@Composable
fun PackCard(pack: PackSummary, onPreview: () -> Unit, onOpen: () -> Unit) {
    val color = when (pack.categoryFocus) {
        "VOICE" -> OrangeAccent
        "CREEPY" -> FuchsiaAccent
        "AMBIENCE" -> CyanAccent
        else -> LimeAccent
    }

    GlassPanel {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth().height(90.dp).background(Color.Black)) {
                Box(modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(color, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    LabelCaps(pack.categoryFocus, color = Color.Black)
                }
            }
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HeadlineText(pack.packId.uppercase(), color = color)
                Text("${pack.soundCount} SFX", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = onPreview, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))) {
                        Icon(Icons.Default.PlayArrow, "Preview", tint = color)
                    }
                    Button(onClick = onOpen, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f))) {
                        Icon(Icons.Default.FilterAlt, "Open pack", tint = CyanAccent)
                    }
                }
            }
        }
    }
}
