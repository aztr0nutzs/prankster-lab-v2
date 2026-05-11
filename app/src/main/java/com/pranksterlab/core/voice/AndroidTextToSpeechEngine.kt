package com.pranksterlab.core.voice

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.util.Locale
import kotlin.coroutines.resume

class AndroidTextToSpeechEngine(context: Context) : VoiceSynthesisEngine {
    private val initResult = CompletableDeferred<VoiceEngineReadiness>()
    private val _readiness = MutableStateFlow<VoiceEngineReadiness>(VoiceEngineReadiness.Initializing)
    override val readiness: StateFlow<VoiceEngineReadiness> = _readiness
    private val mainHandler = Handler(Looper.getMainLooper())
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            mainHandler.post {
                if (status != TextToSpeech.SUCCESS) {
                    publishReadiness(VoiceEngineReadiness.Unavailable)
                    return@post
                }

                val engine = tts
                if (engine == null) {
                    publishReadiness(VoiceEngineReadiness.Error("TextToSpeech engine did not initialize."))
                    return@post
                }

                when (engine.setLanguage(Locale.getDefault())) {
                    TextToSpeech.LANG_MISSING_DATA -> publishReadiness(VoiceEngineReadiness.Error("TTS language data is missing."))
                    TextToSpeech.LANG_NOT_SUPPORTED -> publishReadiness(VoiceEngineReadiness.Error("TTS language is unsupported on this device."))
                    else -> publishReadiness(VoiceEngineReadiness.Ready)
                }
            }
        }
    }

    override suspend fun synthesizeToFile(settings: VoiceGeneratorSettings, outputFile: File): VoiceSynthesisResult {
        val readyState = awaitReady()
        if (readyState !is VoiceEngineReadiness.Ready) {
            return failure(outputFile, readinessMessage(readyState))
        }

        val engine = tts ?: return failure(outputFile, "TextToSpeech engine is unavailable.")
        outputFile.parentFile?.mkdirs()
        if (outputFile.exists()) outputFile.delete()

        if (engine.setPitch(settings.pitch) == TextToSpeech.ERROR) {
            return failure(outputFile, "TextToSpeech rejected the pitch setting.")
        }
        if (engine.setSpeechRate(settings.speechRate) == TextToSpeech.ERROR) {
            return failure(outputFile, "TextToSpeech rejected the speed setting.")
        }

        val utteranceId = "voice_${System.currentTimeMillis()}"
        val params = Bundle().apply { putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, settings.volume) }

        val synthesisResult = withTimeoutOrNull(SYNTHESIS_TIMEOUT_MS) {
            suspendCancellableCoroutine<VoiceSynthesisResult> { cont ->
                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) = Unit

                    override fun onDone(doneUtteranceId: String?) {
                        if (doneUtteranceId == utteranceId && cont.isActive) {
                            cont.resume(validateOutput(outputFile))
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(errorUtteranceId: String?) {
                        if (errorUtteranceId == utteranceId && cont.isActive) {
                            cont.resume(failure(outputFile, "TextToSpeech synthesis failed."))
                        }
                    }

                    override fun onError(errorUtteranceId: String?, errorCode: Int) {
                        if (errorUtteranceId == utteranceId && cont.isActive) {
                            cont.resume(failure(outputFile, "TextToSpeech synthesis failed with error code $errorCode."))
                        }
                    }
                })

                val startResult = engine.synthesizeToFile(settings.text, params, outputFile, utteranceId)
                if (startResult == TextToSpeech.ERROR && cont.isActive) {
                    cont.resume(failure(outputFile, "TextToSpeech could not start file synthesis."))
                }
            }
        }

        return synthesisResult ?: failure(outputFile, "TextToSpeech synthesis timed out.")
    }

    override fun preview(settings: VoiceGeneratorSettings) {
        val engine = tts ?: return
        if (readiness.value !is VoiceEngineReadiness.Ready) return
        engine.setPitch(settings.pitch)
        engine.setSpeechRate(settings.speechRate)
        val params = Bundle().apply { putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, settings.volume) }
        engine.speak(settings.text, TextToSpeech.QUEUE_FLUSH, params, "preview_${System.currentTimeMillis()}")
    }

    override fun stopPreview() { tts?.stop() }
    override fun release() { tts?.shutdown(); tts = null }

    private suspend fun awaitReady(): VoiceEngineReadiness {
        return when (val current = readiness.value) {
            VoiceEngineReadiness.Initializing -> withTimeoutOrNull(INIT_TIMEOUT_MS) { initResult.await() }
                ?: VoiceEngineReadiness.Error("TextToSpeech initialization timed out.")
            else -> current
        }
    }

    private fun publishReadiness(state: VoiceEngineReadiness) {
        _readiness.value = state
        if (!initResult.isCompleted) initResult.complete(state)
    }

    private fun validateOutput(outputFile: File): VoiceSynthesisResult {
        if (!outputFile.exists()) return failure(outputFile, "Generated audio file is missing.")
        if (outputFile.length() <= 0L) return failure(outputFile, "Generated audio file is empty.")
        return VoiceSynthesisResult(
            outputFile = outputFile,
            formatLabel = FORMAT_LABEL,
            durationMs = readDurationMs(outputFile),
            success = true
        )
    }

    private fun readDurationMs(outputFile: File): Long? {
        return runCatching {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(outputFile.absolutePath)
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
            } finally {
                retriever.release()
            }
        }.getOrNull()
    }

    private fun failure(outputFile: File, message: String): VoiceSynthesisResult {
        return VoiceSynthesisResult(outputFile, FORMAT_LABEL, null, success = false, errorMessage = message)
    }

    private fun readinessMessage(state: VoiceEngineReadiness): String {
        return when (state) {
            VoiceEngineReadiness.Initializing -> "TextToSpeech is still initializing."
            VoiceEngineReadiness.Ready -> ""
            VoiceEngineReadiness.Unavailable -> "TextToSpeech engine is unavailable on this device."
            is VoiceEngineReadiness.Error -> state.message
        }
    }

    companion object {
        private const val FORMAT_LABEL = "WAV/PCM"
        private const val INIT_TIMEOUT_MS = 10_000L
        private const val SYNTHESIS_TIMEOUT_MS = 60_000L
    }
}
