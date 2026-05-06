package com.pranksterlab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranksterlab.components.HUDCard
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.components.ScanlineOverlay
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.theme.*
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(soundRepository: SoundRepository, audioPlayerController: AudioPlayerController) {
    var bundledSounds by remember { mutableStateOf(emptyList<PrankSound>()) }
    var customSounds by remember { mutableStateOf(emptyList<PrankSound>()) }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    val favoriteIds by soundRepository.getFavoritesFlow().collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        bundledSounds = soundRepository.getBundledSounds()
        soundRepository.getCustomSoundsFlow().collect {
            customSounds = it
        }
    }

    val sounds = bundledSounds + customSounds
    val categories = sounds.map { it.category }.distinct().sorted()
    val chips = listOf("ALL", "FAVORITES") + categories + listOf("CUSTOM")
    
    val filteredSounds = sounds.filter { sound ->
        val matchesCategory = when (selectedCategory) {
            "ALL" -> true
            "FAVORITES" -> favoriteIds.contains(sound.id)
            "CUSTOM" -> sound.isCustom
            else -> sound.category == selectedCategory
        }
        val matchesSearch = if (searchQuery.isEmpty()) true else {
            sound.name.contains(searchQuery, ignoreCase = true) || 
            sound.tags.any { it.contains(searchQuery, ignoreCase = true) } ||
            sound.category.contains(searchQuery, ignoreCase = true)
        }
        matchesCategory && matchesSearch
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        ScanlineOverlay()
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Main Header from Design
            Header(onSearchClick = { isSearchActive = !isSearchActive })

            // Category Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(chips) { chip ->
                    val isSelected = chip == selectedCategory
                    val chipColor = when(chip) {
                        "FUNNY" -> Color(0xFFEC4899)
                        "MISC" -> Color(0xFF22D3EE)
                        "VOICE", "CUSTOM" -> Color(0xFFF97316)
                        else -> LimeAccent
                    }
                    
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .border(
                                1.dp, 
                                if (isSelected) Color.White else Color.Gray.copy(alpha=0.6f), 
                                CircleShape
                            )
                            .background(if (isSelected) Color.White.copy(alpha=0.1f) else Color.Transparent)
                            .clickable { selectedCategory = chip }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = chip,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = if (isSelected) Color.White else chipColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Search Trigger
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { isSearchActive = !isSearchActive },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF111111).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Search, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }

            if (isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    placeholder = { Text("SEARCH ARMAMENT...", color = Color.Gray) },
                    textStyle = MaterialTheme.typography.bodyMedium,
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

            // Sounds List
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredSounds) { sound ->
                    HUDSoundCard(
                        sound = sound,
                        audioPlayerController = audioPlayerController,
                        isFavorite = favoriteIds.contains(sound.id),
                        onToggleFavorite = { scope.launch { soundRepository.toggleFavorite(sound.id) } }
                    )
                }
            }
        }
    }
}

@Composable
fun Header(onSearchClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(32.dp)) // Spacer
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    imageVector = Icons.Default.RadioButtonChecked,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA), // light blue
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "PRANKSTER",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    ),
                    color = Color.White
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(onClick = onSearchClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Search, null, tint = LimeAccent.copy(alpha = 0.8f))
                }
                IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Settings, null, tint = Color(0xFFF97316).copy(alpha = 0.8f))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "AUDIO ARSENAL",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 32.sp,
                letterSpacing = 4.sp,
                lineHeight = 32.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            ),
            color = LimeAccent,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun HUDSoundCard(
    sound: PrankSound,
    audioPlayerController: AudioPlayerController,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val accentColor = when (sound.category) {
        "FUNNY", "CARTOON" -> FuchsiaAccent
        "CREEPY" -> Color.Red
        "VOICE" -> CyanAccent
        "FIGHTER" -> OrangeAccent
        "ANIMAL" -> LimeAccent
        else -> LimeAccent
    }

    val playbackState by audioPlayerController.playbackState.collectAsState()
    val invalidIds by audioPlayerController.invalidSoundIds.collectAsState()
    val isPlaying = playbackState.isPlaying && playbackState.currentSoundId == sound.id
    val isInvalid = sound.id in invalidIds
    val isErroredHere = playbackState.lastErrorSoundId == sound.id && playbackState.lastError != null

    HUDCard(
        modifier = Modifier.fillMaxWidth().clickable(enabled = !isInvalid) {
            if (isPlaying) {
                audioPlayerController.stop()
            } else {
                audioPlayerController.playPrankSound(sound, isLooping = sound.loopable)
            }
        },
        accentColor = if (isInvalid) Color.Red else accentColor
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sound.name.uppercase(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        letterSpacing = 1.sp
                    ),
                    color = LimeAccent,
                    maxLines = 2
                )
                
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1A1A1A), RoundedCornerShape(4.dp))
                            .border(1.dp, Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = sound.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            Icons.Default.AccessTime, 
                            null, 
                            tint = Color.Gray, 
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${(sound.durationMs / 100).toDouble() / 10}S",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            if (isInvalid || isErroredHere) {
                Box(
                    modifier = Modifier
                        .background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Red.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isInvalid) "UNAVAILABLE" else "ERROR",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Yellow else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        if (isPlaying) {
            // Waveform effect at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(accentColor)
            )
        }
    }
}
