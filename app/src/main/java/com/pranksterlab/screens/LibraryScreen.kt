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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.pranksterlab.components.HUDCard
import com.pranksterlab.components.ScanlineOverlay
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.theme.BackgroundDark
import com.pranksterlab.theme.CyanAccent
import com.pranksterlab.theme.FuchsiaAccent
import com.pranksterlab.theme.LimeAccent
import com.pranksterlab.theme.OrangeAccent
import kotlinx.coroutines.launch

private const val FILTER_ALL = "ALL"
private const val FILTER_FAVORITES = "FAVORITES"
private const val FILTER_CUSTOM = "CUSTOM"
private const val FILTER_GENERATED = "GENERATED"

@Composable
fun LibraryScreen(
    soundRepository: SoundRepository,
    audioPlayerController: AudioPlayerController,
    onOpenSequence: () -> Unit = {},
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

    val favoriteIds by soundRepository.getFavoritesFlow().collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        bundledSounds = soundRepository.getBundledSounds()
        soundRepository.getCustomSoundsFlow().collect { customSounds = it }
    }

    LaunchedEffect(activePackFilter) {
        selectedPack = activePackFilter
    }

    val allSounds = bundledSounds + customSounds
    val validSounds = allSounds.filter { sound ->
        soundRepository.isSoundPlayable(sound)
    }
    val invalidSounds = allSounds - validSounds.toSet()

    val packCounts = validSounds.mapNotNull { it.packId }.groupingBy { it }.eachCount()
    val categoryCounts = validSounds.groupingBy { it.category }.eachCount()

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
                (sound.packId?.contains(searchQuery, true) == true)
        }
        matchesCategory && matchesPack && matchesSearch
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        ScanlineOverlay()

        Column(modifier = Modifier.fillMaxSize()) {
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
                items(filteredSounds, key = { it.id }) { sound ->
                    HUDSoundCard(
                        sound = sound,
                        audioPlayerController = audioPlayerController,
                        isFavorite = favoriteIds.contains(sound.id),
                        onToggleFavorite = { scope.launch { soundRepository.toggleFavorite(sound.id) } },
                        onAddToSequence = {
                            soundRepository.queueSoundForSequence(sound.id)
                            onOpenSequence()
                        },
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
    Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(32.dp))
            Text("PRANKSTER", style = MaterialTheme.typography.headlineSmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontWeight = FontWeight.Black, letterSpacing = (-1).sp), color = Color.White)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = if (showDiagnostics) "DIAG $invalidCount" else "DIAG",
                    color = if (showDiagnostics) OrangeAccent else Color.Gray,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { onDiagnosticsClick() }
                )
                IconButton(onClick = onSearchClick, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Search, null, tint = LimeAccent.copy(alpha = 0.8f)) }
            }
        }
        Spacer(modifier = Modifier.size(18.dp))
        Text("AUDIO ARSENAL", style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp, letterSpacing = 4.sp, lineHeight = 32.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), color = LimeAccent)
    }
}

@Composable
fun HUDSoundCard(
    sound: PrankSound,
    audioPlayerController: AudioPlayerController,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onAddToSequence: () -> Unit,
    onTimerShortcut: () -> Unit
) {
    val playbackState by audioPlayerController.playbackState.collectAsState()
    val isPlaying = playbackState.isPlaying && playbackState.currentSoundId == sound.id
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
                    Text("${sound.category} • ${sound.packId ?: "UNPACKED"}", color = Color.Gray)
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
                Button(onClick = onAddToSequence, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f))) {
                    Icon(Icons.Default.Add, "Add to sequence", tint = CyanAccent)
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
