package com.pranksterlab

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pranksterlab.components.BottomNavBar
import com.pranksterlab.components.TopBar
import com.pranksterlab.screens.*
import com.pranksterlab.screens.soundforge.SoundForgeScreen
import com.pranksterlab.screens.soundforge.SoundForgeViewModel
import com.pranksterlab.core.audio.generator.SoundGeneratorEngine
import com.pranksterlab.core.repository.CustomSoundManager

import androidx.compose.runtime.DisposableEffect
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.repository.SoundRepository
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

@Composable
fun PranksterApp() {
    val context = LocalContext.current
    val audioPlayerController = remember { AudioPlayerController(context) }
    
    DisposableEffect(audioPlayerController) {
        onDispose {
            audioPlayerController.release()
        }
    }
    val soundRepository = remember { SoundRepository(context) }
    val customSoundManager = remember { CustomSoundManager(context, soundRepository) }
    val soundGeneratorEngine = remember { SoundGeneratorEngine(context) }
    val soundForgeViewModel = remember { SoundForgeViewModel(soundGeneratorEngine, customSoundManager) }
    
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        topBar = { 
            if (currentRoute != "library") {
                TopBar() 
            }
        },
        containerColor = Color.Black,
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen(audioPlayerController, soundRepository, onNavigate = { navController.navigate(it) }) }
            composable("library") { LibraryScreen(soundRepository, audioPlayerController) }
            composable("timer") { TimerPrankScreen(soundRepository, audioPlayerController) }
            composable("forge") { SoundForgeScreen(soundForgeViewModel, audioPlayerController) }
            composable("lab") { SoundPacksScreen() }
            composable("system") { SettingsScreen() }
            composable("sequence") { SequenceBuilderScreen(soundRepository, audioPlayerController) }
            composable("randomizer") { RandomizerScreen(soundRepository, audioPlayerController) }
            composable("messages") { PrankMessagesScreen() }
        }
    }
}
