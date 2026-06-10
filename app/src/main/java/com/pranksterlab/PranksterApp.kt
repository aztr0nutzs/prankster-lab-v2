package com.pranksterlab

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pranksterlab.components.PrankstarBottomDock
import com.pranksterlab.screens.*
import com.pranksterlab.screens.soundforge.SoundForgeScreen
import com.pranksterlab.screens.voice.VoiceJokeGeneratorScreen
import com.pranksterlab.screens.soundforge.SoundForgeViewModel
import com.pranksterlab.core.audio.generator.SoundGeneratorEngine
import com.pranksterlab.core.repository.CustomSoundManager

import androidx.compose.runtime.DisposableEffect
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.billing.FeatureGate
import com.pranksterlab.core.billing.UnconfiguredEntitlementRepository
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
    val entitlementRepository = remember { UnconfiguredEntitlementRepository() }
    val featureGate by entitlementRepository.featureGate.collectAsState(initial = FeatureGate.unconfiguredFree())
    
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    val dockRoute = when (currentRoute) {
        "home", "home_native", "home_ultimate", "randomizer", "timer" -> "home"
        "library", "lab" -> "library"
        "forge" -> "forge"
        "voice_lab", "messages" -> "voice_lab"
        "system" -> "system"
        else -> "home"
    }
    val showNativeDock = currentRoute != "home"

    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            if (showNativeDock) {
                PrankstarBottomDock(
                    currentRoute = dockRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = if (showNativeDock) Modifier.padding(paddingValues) else Modifier
        ) {
            composable("home") {
                PrankstarStableHomeWebViewScreen(
                    audioPlayerController = audioPlayerController,
                    soundRepository = soundRepository,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable("home_native") { HomeScreen(audioPlayerController, soundRepository, onNavigate = { navController.navigate(it) }) }
            composable("home_ultimate") { UltimateReactorScreen(audioPlayerController, soundRepository, onNavigate = { navController.navigate(it) }) }
            composable("library") {
                LibraryScreen(
                    soundRepository = soundRepository,
                    audioPlayerController = audioPlayerController,
                    onCreateJoke = { navController.navigate("voice_lab") },
                    onOpenTimer = { navController.navigate("timer") }
                )
            }
            composable("timer") { TimerPrankScreen(soundRepository, audioPlayerController) }
            composable("forge") { SoundForgeScreen(soundForgeViewModel, audioPlayerController) }
            composable("lab") { SoundPacksScreen(soundRepository, audioPlayerController, onOpenLibrary = { navController.navigate("library") }) }
            composable("system") {
                SettingsScreen(
                    soundRepository = soundRepository,
                    audioPlayerController = audioPlayerController,
                    featureGate = featureGate
                )
            }
            composable("voice_lab") {
                VoiceJokeGeneratorScreen(
                    soundRepository = soundRepository,
                    audioPlayerController = audioPlayerController,
                    onNavigate = { navController.navigate(it) },
                    featureGate = featureGate
                )
            }
            composable("randomizer") { RandomizerScreen(soundRepository, audioPlayerController) }
            composable("messages") { PrankMessagesScreen() }
        }
    }
}
