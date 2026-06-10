package com.pranksterlab.screens

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.pranksterlab.core.audio.AudioPlayerController
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.repository.SoundRepository
import com.pranksterlab.core.voice.AndroidTextToSpeechEngine
import com.pranksterlab.core.voice.VoiceGeneratorSettings
import com.pranksterlab.core.voice.VoicePresetLibrary

const val PRANKSTAR_HOME_WEBVIEW_URL = "file:///android_asset/prankstar/prankstar_new_home_bot_screen.html"

private const val PRANKSTAR_BRIDGE_NAME = "PrankstarAndroid"
private const val PRANKSTAR_BRIDGE_TAG = "PrankstarBridge"

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun PrankstarHomeWebViewScreen(
    audioPlayerController: AudioPlayerController,
    soundRepository: SoundRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ttsEngine = remember(context) { AndroidTextToSpeechEngine(context) }
    val bridge = remember(audioPlayerController, soundRepository, ttsEngine) {
        PrankstarBridge(audioPlayerController, soundRepository, ttsEngine)
    }

    DisposableEffect(ttsEngine) {
        onDispose {
            ttsEngine.stopPreview()
            ttsEngine.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(AndroidColor.BLACK)
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    addJavascriptInterface(bridge, PRANKSTAR_BRIDGE_NAME)
                    loadUrl(PRANKSTAR_HOME_WEBVIEW_URL)
                }
            },
            update = { webView ->
                if (webView.url != PRANKSTAR_HOME_WEBVIEW_URL) {
                    webView.loadUrl(PRANKSTAR_HOME_WEBVIEW_URL)
                }
            }
        )
    }
}

private class PrankstarBridge(
    private val audioPlayerController: AudioPlayerController,
    private val soundRepository: SoundRepository,
    private val ttsEngine: AndroidTextToSpeechEngine
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val random = kotlin.random.Random(System.currentTimeMillis())

    @JavascriptInterface
    fun playRandomSound() {
        Log.d(PRANKSTAR_BRIDGE_TAG, "playRandomSound called")
        postSafely("playRandomSound") {
            playFirstPlayable(soundRepository.getBundledSounds().filter { it.isSafeForRandomMode }, "random")
        }
    }

    @JavascriptInterface
    fun playSoundByCategory(category: String) {
        Log.d(PRANKSTAR_BRIDGE_TAG, "playSoundByCategory called category=$category")
        postSafely("playSoundByCategory") {
            val mappedCategory = mapCategory(category)
            val candidates = soundRepository.getBundledSounds().filter { sound ->
                soundRepository.isSoundPlayable(sound) &&
                    (sound.category.equals(mappedCategory, ignoreCase = true) ||
                        sound.tags.any { it.equals(category, ignoreCase = true) } ||
                        sound.packId?.contains(category, ignoreCase = true) == true ||
                        sound.name.contains(category, ignoreCase = true))
            }
            if (!playFirstPlayable(candidates, "category=$category mapped=$mappedCategory")) {
                playFirstPlayable(soundRepository.getBundledSounds().filter { it.isSafeForRandomMode }, "fallback category=$category")
            }
        }
    }

    @JavascriptInterface
    fun playSoundById(id: String) {
        Log.d(PRANKSTAR_BRIDGE_TAG, "playSoundById called id=$id")
        postSafely("playSoundById") {
            val sound = soundRepository.getBundledSounds().firstOrNull { it.id == id }
            if (sound == null) {
                Log.w(PRANKSTAR_BRIDGE_TAG, "No sound found for id=$id")
                return@postSafely
            }
            audioPlayerController.playPrankSound(sound)
        }
    }

    @JavascriptInterface
    fun playRandomJoke() {
        Log.d(PRANKSTAR_BRIDGE_TAG, "playRandomJoke called")
        postSafely("playRandomJoke") {
            speakJoke(JokeLine.random(random))
        }
    }

    @JavascriptInterface
    fun playJokeByType(type: String) {
        Log.d(PRANKSTAR_BRIDGE_TAG, "playJokeByType called type=$type")
        postSafely("playJokeByType") {
            speakJoke(JokeLine.forType(type, random))
        }
    }

    @JavascriptInterface
    fun stopPlayback() {
        Log.d(PRANKSTAR_BRIDGE_TAG, "stopPlayback called")
        postSafely("stopPlayback") {
            audioPlayerController.stop()
            ttsEngine.stopPreview()
        }
    }

    private fun postSafely(action: String, block: () -> Unit) {
        mainHandler.post {
            try {
                block()
            } catch (throwable: Throwable) {
                Log.e(PRANKSTAR_BRIDGE_TAG, "$action playback failed", throwable)
            }
        }
    }

    private fun playFirstPlayable(candidates: List<PrankSound>, reason: String): Boolean {
        val playable = candidates.filter { soundRepository.isSoundPlayable(it) }
        val sound = playable.randomOrNull(random)
        if (sound == null) {
            Log.w(PRANKSTAR_BRIDGE_TAG, "No playable sound for $reason")
            return false
        }
        Log.d(PRANKSTAR_BRIDGE_TAG, "Playing ${sound.id} for $reason")
        return audioPlayerController.playPrankSound(sound)
    }

    private fun speakJoke(jokeLine: JokeLine) {
        audioPlayerController.stop()
        val preset = VoicePresetLibrary.presets.firstOrNull { it.id == jokeLine.presetId }
            ?: VoicePresetLibrary.presets.first()
        ttsEngine.preview(
            VoiceGeneratorSettings(
                preset = preset,
                text = jokeLine.text,
                pitch = preset.pitch,
                speechRate = preset.speechRate,
                volume = preset.volume,
                toneStyle = preset.toneStyle,
                effectAmount = 0.2f,
                enableEchoReverb = false,
                outputName = "webview_joke"
            )
        )
    }

    private fun mapCategory(category: String): String {
        return when (category.trim().lowercase()) {
            "effects", "scan", "power", "forge", "zap", "audio" -> "AMBIENCE"
            "stash", "deploy", "release", "funny", "dance" -> "FUNNY"
            "animal" -> "ANIMAL"
            "creepy", "sneak", "trap" -> "CREEPY"
            "cartoon", "classic", "mega" -> "CARTOON"
            "voice", "joke" -> "VOICE"
            else -> category.uppercase()
        }
    }
}

private data class JokeLine(
    val type: String,
    val text: String,
    val presetId: String
) {
    companion object {
        private val jokes = listOf(
            JokeLine("CLASSIC", "Diagnostic complete. Mischief engine online.", "glitch_bot"),
            JokeLine("CLASSIC", "I used to hate facial hair, but it grew on me.", "awkward_dad_joke"),
            JokeLine("SNEAK", "In silence, the toaster made its choice.", "dramatic_whisper"),
            JokeLine("SNEAK", "Attention team, nap o'clock has been approved.", "sleepy_announcer"),
            JokeLine("MEGA", "Docking sequence complete. Dance sequence optional.", "space_announcer"),
            JokeLine("MEGA", "Moon base memo: please label your anti-gravity lunch.", "moon_base_intern"),
            JokeLine("TRAP", "Step one: remain calm. Step two: pretend this is normal.", "mysterious_instructional_tape"),
            JokeLine("TRAP", "Troll says meeting postponed until snack arrives.", "cave_troll_lite")
        )

        fun random(random: kotlin.random.Random): JokeLine = jokes.random(random)

        fun forType(type: String, random: kotlin.random.Random): JokeLine {
            val normalized = type.trim().uppercase()
            return jokes.filter { it.type == normalized }.randomOrNull(random) ?: random(random)
        }
    }
}
