package com.pranksterlab.core.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.Locale
import kotlin.coroutines.resume

class AndroidTextToSpeechEngine(context: Context) : VoiceSynthesisEngine {
    private var tts: TextToSpeech? = null
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()
    private val _status = MutableStateFlow("INITIALIZING")
    val status: StateFlow<String> = _status.asStateFlow()

    init {
        tts = TextToSpeech(context) { result ->
            if (result == TextToSpeech.SUCCESS) {
                val languageResult = tts?.setLanguage(Locale.getDefault())
                val available = languageResult != TextToSpeech.LANG_MISSING_DATA && languageResult != TextToSpeech.LANG_NOT_SUPPORTED
                _isReady.value = available
                _status.value = if (available) "READY" else "TTS LANGUAGE UNAVAILABLE"
            } else {
                _isReady.value = false
                _status.value = "TTS UNAVAILABLE"
            }
        }
    }

    override suspend fun synthesizeToFile(settings: VoiceGeneratorSettings, outputFile: File): VoiceSynthesisResult = suspendCancellableCoroutine { cont ->
        val engine = tts ?: run {
            cont.resume(VoiceSynthesisResult(outputFile, "WAV/PCM", null, "TTS engine unavailable"))
            return@suspendCancellableCoroutine
        }
        if (!_isReady.value) {
            cont.resume(VoiceSynthesisResult(outputFile, "WAV/PCM", null, _status.value))
            return@suspendCancellableCoroutine
        }
        val utteranceId = "voice_${System.currentTimeMillis()}"
        engine.setPitch(settings.pitch)
        engine.setSpeechRate(settings.speechRate)
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onDone(utteranceId: String?) {
                if (cont.isActive) cont.resume(VoiceSynthesisResult(outputFile, "WAV/PCM", null))
            }
            override fun onError(utteranceId: String?) {
                if (cont.isActive) cont.resume(VoiceSynthesisResult(outputFile, "WAV/PCM", null, "TTS synthesis failed"))
            }
        })
        val params = Bundle().apply { putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, settings.volume) }
        val accepted = engine.synthesizeToFile(settings.text, params, outputFile, utteranceId)
        if (accepted == TextToSpeech.ERROR && cont.isActive) {
            cont.resume(VoiceSynthesisResult(outputFile, "WAV/PCM", null, "TTS rejected synthesis request"))
        }
    }


    override fun preview(settings: VoiceGeneratorSettings) {
        val engine = tts ?: return
        if (!_isReady.value) return
        engine.setPitch(settings.pitch)
        engine.setSpeechRate(settings.speechRate)
        val params = Bundle().apply { putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, settings.volume) }
        engine.speak(settings.text, TextToSpeech.QUEUE_FLUSH, params, "preview_${System.currentTimeMillis()}")
    }

    override fun stopPreview() { tts?.stop() }
    override fun release() {
        tts?.shutdown()
        tts = null
        _isReady.value = false
        _status.value = "RELEASED"
    }
}
