package com.pranksterlab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.R
import com.pranksterlab.components.HUDCard
import com.pranksterlab.components.PrankstarHeader
import com.pranksterlab.components.ScanlineOverlay
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrangeAccent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val FILTER_ALL = "ALL"
private const val FILTER_FAVORITES = "FAVORITES"
private const val FILTER_CUSTOM = "CUSTOM"
private const val FILTER_GENERATED = "GENERATED"

@Composable
fun LibraryScreen(
    soundRepository: SoundRepository,
    audioPlayerController: AudioPlayerController,
    onOpenVoiceLab: () -> Unit = {},
    onOpenTimer: () -> Unit = {}
) {
    var bundledSounds by remember { mutableStateOf(emptyList<PrankSound>()) }
    var customSounds by remember { mutableStateOf(emptyList<PrankSound>()) }
    var selectedCategory by remember { mutableStateOf(FILTER_ALL) }
    var selectedPack by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showDiagnostics by remember { mutableStateOf(false) }
    val activePackFilter by soundRepository.activePackFilter.collectAsState()
    val playbackState by audioPlayerController.playbackState.collectAsState()

    val favoriteIds by soundRepository.getFavoritesFlow().collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        bundledSounds = withContext(Dispatchers.IO) { soundRepository.getBundledSounds() }
        soundRepository.getCustomSoundsFlow().collect { customSounds = it }
    }

    LaunchedEffect(activePackFilter) {
        selectedPack = activePackFilter
    }

    val validSounds by produceState(initialValue = emptyList<PrankSound>(), bundledSounds, customSounds) {
        value = withContext(Dispatchers.IO) {
            (bundledSounds + customSounds).filter { soundRepository.isSoundPlayable(it) }
        }
    }
    val allSounds = bundledSounds + customSounds
    val invalidSounds = allSounds - validSounds.toSet()

    val packCounts = remember(validSounds) { validSounds.mapNotNull { it.packId }.groupingBy { it }.eachCount() }
    val categoryCounts = remember(validSounds) { validSounds.groupingBy { it.category }.eachCount() }

    val categoryChips = buildList {
        add("$FILTER_ALL (${validSounds.size})")
        add("$FILTER_FAVORITES (${validSounds.count { favoriteIds.contains(it.id) }})")
        categoryCounts.toList().sortedBy { it.first }.forEach { (category, count) ->
            add("$category ($count)")
        }
        if (validSounds.any { it.isCustom }) add("$FILTER_CUSTOM (${validSounds.count { it.isCustom }})")
        if (validSounds.any { it.isGeneratedSound() }) add("$FILTER_GENERATED (${validSounds.count { it.isGeneratedSound() }})")
    }

    val filteredSounds = validSounds.filter { sound ->
        val chipKey = selectedCategory.substringBefore(" (")
        val matchesCategory = when (chipKey) {
            FILTER_ALL -> true
            FILTER_FAVORITES -> favoriteIds.contains(sound.id)
            FILTER_CUSTOM -> sound.isCustom
            FILTER_GENERATED -> sound.isGeneratedSound()
            else -> sound.category == chipKey
        }
        val matchesPack = selectedPack == null || sound.packId == selectedPack
        val matchesSearch = if (searchQuery.isBlank()) true else {
                sound.name.contains(searchQuery, true) ||
                sound.category.contains(searchQuery, true) ||
                sound.tags.any { it.contains(searchQuery, true) } ||
                (sound.packId?.contains(searchQuery, true) == true) ||
                (sound.generatedMetadata?.voicePresetName?.contains(searchQuery, true) == true) ||
                (sound.generatedMetadata?.sourceText?.contains(searchQuery, true) == true)
        }
        matchesCategory && matchesPack && matchesSearch
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        ScanlineOverlay()

        Column(modifier = Modifier.fillMaxSize()) {
            PrankstarHeader(
                title = "Audio Arsenal",
                subtitle = "Library / Catalog Browser",
                imageRes = R.drawable.header_sound_stash,
                statusLabel = "${validSounds.size} ASSETS",
                showTextOverlay = false,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
            Header(
                onSearchClick = { isSearchActive = !isSearchActive },
                showDiagnostics = showDiagnostics,
                invalidCount = invalidSounds.size,
                onDiagnosticsClick = { showDiagnostics = !showDiagnostics }
            )

            if (selectedPack != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(Color(0xFF082F49), RoundedCornerShape(10.dp)).padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PACK LOCK: $selectedPack", color = CyanAccent)
                    Text("CLEAR", color = FuchsiaAccent, modifier = Modifier.clickable { selectedPack = null; soundRepository.setActivePackFilter(null) })
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(categoryChips) { chip ->
                    val isSelected = chip == selectedCategory
                    Box(
                        modifier = Modifier.padding(4.dp).clip(CircleShape).border(1.dp, if (isSelected) Color.White else Color.Gray.copy(alpha = 0.6f), CircleShape)
                            .background(if (isSelected) Color.White.copy(alpha = 0.1f) else Color.Transparent).clickable { selectedCategory = chip }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(chip, color = if (isSelected) Color.White else LimeAccent.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            LazyRow(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(packCounts.entries.toList().sortedBy { it.key }) { (packId, count) ->
                    val active = selectedPack == packId
                    Box(modifier = Modifier.padding(end = 8.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, if (active) CyanAccent else Color.Gray, RoundedCornerShape(12.dp))
                        .background(if (active) CyanAccent.copy(alpha = 0.12f) else Color.Transparent).clickable { selectedPack = if (active) null else packId }
                        .padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text("$packId ($count)", color = if (active) CyanAccent else Color.Gray)
                    }
                }
            }

            if (isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    placeholder = { Text("SEARCH NAME / CATEGORY / TAG / PACK", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LimeAccent,
                        unfocusedBorderColor = Color.Gray,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            if (filteredSounds.isEmpty()) {
                HUDCard(modifier = Modifier.fillMaxWidth().padding(16.dp), accentColor = CyanAccent) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("NO VALID SOUNDS MATCH FILTER", color = CyanAccent)
                        Text("Adjust category/pack/search to reveal playable catalog assets.", color = Color.Gray)
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 100.dp)) {
                itemsIndexed(filteredSounds, key = { index, sound -> "${sound.id}:${sound.assetPath}:${sound.localUri ?: "bundled"}:$index" }) { _, sound ->
                    HUDSoundCard(
                        sound = sound,
                        categoryLabel = soundRepository.readableCategory(sound),
                        audioPlayerController = audioPlayerController,
                        isPlaying = playbackState.isPlaying && playbackState.currentSoundId == sound.id,
                        isFavorite = favoriteIds.contains(sound.id),
                        onToggleFavorite = { scope.launch { soundRepository.toggleFavorite(sound.id) } },
                        onOpenVoiceLab = onOpenVoiceLab,
                        onTimerShortcut = {
                            soundRepository.queueSoundForTimer(sound.id)
                            onOpenTimer()
                        }
                    )
                }
                if (showDiagnostics && invalidSounds.isNotEmpty()) {
                    item {
                        HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = OrangeAccent) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("INVALID / BLOCKED ASSETS: ${invalidSounds.size}", color = OrangeAccent)
                                Text(
                                    invalidSounds.take(5).joinToString("\n") { "${it.name}  /  ${it.assetPath}" },
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Header(
    onSearchClick: () -> Unit,
    showDiagnostics: Boolean,
    invalidCount: Int,
    onDiagnosticsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (showDiagnostics) "DIAG $invalidCount" else "DIAG",
            color = if (showDiagnostics) OrangeAccent else Color.Gray,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.clickable { onDiagnosticsClick() }
        )
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = onSearchClick, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Search, null, tint = LimeAccent.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun HUDSoundCard(
    sound: PrankSound,
    categoryLabel: String,
    audioPlayerController: AudioPlayerController,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onOpenVoiceLab: () -> Unit,
    onTimerShortcut: () -> Unit
) {
    var loopEnabled by remember(sound.id) { mutableStateOf(sound.loopable) }
    val accentColor = when (sound.category) {
        "FUNNY", "CARTOON" -> FuchsiaAccent
        "VOICE" -> CyanAccent
        "FIGHTER" -> OrangeAccent
        else -> LimeAccent
    }

    HUDCard(modifier = Modifier.fillMaxWidth(), accentColor = accentColor) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(sound.name.uppercase(), color = LimeAccent, style = MaterialTheme.typography.headlineSmall.copy(letterSpacing = 1.sp))
                    Text("$categoryLabel • ${sound.packId ?: "UNPACKED"}", color = Color.Gray)
                    if (sound.isGeneratedSound()) {
                        sound.generatedMetadata?.voicePresetName?.let { preset ->
                            Text("VOICE: $preset", color = CyanAccent, style = MaterialTheme.typography.bodySmall)
                        }
                        sound.generatedMetadata?.sourceText?.takeIf { it.isNotBlank() }?.let { source ->
                            Text("TEXT: ${source.take(64)}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text(sound.tags.joinToString("  •  ").ifBlank { "NO TAGS" }, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onToggleFavorite) { Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "Favorite", tint = if (isFavorite) FuchsiaAccent else Color.Gray) }
            }
            NeonWaveform(
                seed = sound.id,
                color = accentColor,
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).height(34.dp)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Text(if (sound.durationMs > 0) "${sound.durationMs}ms" else "DURATION N/A", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                if (sound.loopable) {
                    Icon(
                        Icons.Default.Loop,
                        "Loop toggle",
                        tint = if (loopEnabled) CyanAccent else Color.Gray,
                        modifier = Modifier.size(18.dp).clickable { loopEnabled = !loopEnabled }
                    )
                    Text(if (loopEnabled) "LOOP ON" else "LOOP OFF", color = if (loopEnabled) CyanAccent else Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (isPlaying) audioPlayerController.stop() else audioPlayerController.playPrankSound(sound, sound.loopable && loopEnabled)
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, accentColor)) {
                    Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow, "Play stop", tint = accentColor)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(if (isPlaying) "STOP" else "PLAY", color = accentColor)
                }
                Button(onClick = onOpenVoiceLab, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f))) {
                    Icon(Icons.Default.Add, "Open Voice Lab", tint = CyanAccent)
                }
                Button(onClick = onTimerShortcut, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.4f))) {
                    Icon(Icons.Default.Timer, "Timer shortcut", tint = OrangeAccent)
                }
            }
        }
    }
}

@Composable
fun NeonWaveform(seed: String, color: Color, modifier: Modifier = Modifier) {
    val bars = remember(seed) {
        val base = seed.fold(0) { acc, char -> acc + char.code }.coerceAtLeast(1)
        List(28) { index ->
            val value = ((base + index * 37) % 100) / 100f
            0.25f + value * 0.75f
        }
    }
    Canvas(
        modifier = modifier
            .background(Brush.horizontalGradient(listOf(Color.Transparent, color.copy(alpha = 0.08f), Color.Transparent)))
    ) {
        val gap = size.width / (bars.size * 1.6f)
        val barWidth = gap.coerceAtLeast(2f)
        bars.forEachIndexed { index, heightFactor ->
            val x = index * (barWidth + gap)
            val barHeight = size.height * heightFactor
            drawLine(
                color = color.copy(alpha = 0.35f + heightFactor * 0.45f),
                start = Offset(x, (size.height - barHeight) / 2f),
                end = Offset(x, (size.height + barHeight) / 2f),
                strokeWidth = barWidth
            )
        }
    }
}

private fun PrankSound.isGeneratedSound(): Boolean {
    return generatedMetadata != null || createdByUser && tags.any { it.equals("generated", true) }
}
