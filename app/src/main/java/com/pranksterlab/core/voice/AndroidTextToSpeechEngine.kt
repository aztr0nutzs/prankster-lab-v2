package com.pranksterlab.core.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.Locale
import kotlin.coroutines.resume

class AndroidTextToSpeechEngine(context: Context) : VoiceSynthesisEngine {
    private var tts: TextToSpeech? = null
    init { tts = TextToSpeech(context) { if (it == TextToSpeech.SUCCESS) tts?.language = Locale.getDefault() } }

    override suspend fun synthesizeToFile(settings: VoiceGeneratorSettings, outputFile: File): VoiceSynthesisResult = suspendCancellableCoroutine { cont ->
        val engine = tts ?: run { cont.resume(VoiceSynthesisResult(outputFile, "WAV/PCM", null)); return@suspendCancellableCoroutine }
        val utteranceId = "voice_${System.currentTimeMillis()}"
        engine.setPitch(settings.pitch)
        engine.setSpeechRate(settings.speechRate)
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onDone(utteranceId: String?) { if (cont.isActive) cont.resume(VoiceSynthesisResult(outputFile, "WAV/PCM", null)) }
            override fun onError(utteranceId: String?) { if (cont.isActive) cont.resume(VoiceSynthesisResult(outputFile, "WAV/PCM", null)) }
        })
        val params = Bundle().apply { putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, settings.volume) }
        engine.synthesizeToFile(settings.text, params, outputFile, utteranceId)
    }


    override fun preview(settings: VoiceGeneratorSettings) {
        val engine = tts ?: return
        engine.setPitch(settings.pitch)
        engine.setSpeechRate(settings.speechRate)
        val params = Bundle().apply { putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, settings.volume) }
        engine.speak(settings.text, TextToSpeech.QUEUE_FLUSH, params, "preview_${System.currentTimeMillis()}")
    }

    override fun stopPreview() { tts?.stop() }
    override fun release() { tts?.shutdown(); tts = null }
}
