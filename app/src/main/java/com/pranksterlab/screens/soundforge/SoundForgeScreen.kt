package com.pranksterlab.screens.soundforge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranksterlab.components.GlassPanel
import com.pranksterlab.components.HeadlineText
import com.pranksterlab.components.LabelCaps
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.SoundForgeGeneratorType
import com.pranksterlab.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundForgeScreen(viewModel: SoundForgeViewModel, audioPlayer: AudioPlayerController) {
    val playbackState by audioPlayer.playbackState.collectAsState()
    val isPlayingPreview = playbackState.isPlaying && playbackState.currentSoundId == "preview"
    val result by viewModel.generatedResult

    var showSaveDialog by remember { mutableStateOf(false) }
    var saveName by remember { mutableStateOf("") }
    var saveCategory by remember { mutableStateOf("CUSTOM") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        SoundForgeWorkbench(
            viewModel = viewModel,
            onBack = { /* Handled by bottom nav */ },
            onPreview = { uri -> 
                audioPlayer.playSound(uri, isLocalUri = true, soundId = "preview", soundTitle = "FORGE_PREVIEW") 
            },
            onStopPreview = { audioPlayer.stop() },
            isPlaying = isPlayingPreview
        )

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.Center))

        // Floating Save Button
        if (result != null && result?.errorMessage == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 120.dp, end = 24.dp)
            ) {
                FloatingActionButton(
                    onClick = { showSaveDialog = true },
                    containerColor = FuchsiaAccent,
                    contentColor = Color.Black,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { HeadlineText("SAVE TO LIBRARY", color = FuchsiaAccent) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = saveName,
                        onValueChange = { saveName = it },
                        label = { Text("Sound Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text("Category", style = MaterialTheme.typography.labelMedium, color = OutlineDark)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("CUSTOM", "FUNNY", "CREEPY").forEach { cat ->
                            FilterChip(
                                selected = saveCategory == cat,
                                onClick = { saveCategory = cat },
                                label = { Text(cat) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveGeneratedSound(saveName, saveCategory)
                    showSaveDialog = false
                    saveName = ""
                    scope.launch {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = "Sound saved to library",
                            actionLabel = "VIEW",
                            duration = SnackbarDuration.Short
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                            // Ideally navigate to library, but we'd need navController here.
                            // For now, it's just feedback.
                        }
                    }
                }) {
                    Text("SAVE", color = FuchsiaAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("CANCEL", color = OutlineDark)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun ParameterSlider(name: String, value: Float, min: Float, max: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Text(String.format("%.2f", value), color = PrimaryContainer, style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            colors = SliderDefaults.colors(
                thumbColor = PrimaryContainer,
                activeTrackColor = PrimaryContainer,
                inactiveTrackColor = OutlineDark
            )
        )
    }
}
