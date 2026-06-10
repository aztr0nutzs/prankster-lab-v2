package com.pranksterlab.core.elevenlabs

import java.io.File

sealed interface ElevenLabsTtsError {
    val userMessage: String

    data object MissingApiKey : ElevenLabsTtsError {
        override val userMessage = "ElevenLabs API key is not configured. Add ELEVENLABS_API_KEY to local.properties or environment variables."
    }
    data object BlankText : ElevenLabsTtsError {
        override val userMessage = "Enter narration text before generating audio."
    }
    data class TextTooLong(val maxChars: Int) : ElevenLabsTtsError {
        override val userMessage = "Narration is too long for this voice pass. Keep it under $maxChars characters."
    }
    data class NetworkError(val detail: String) : ElevenLabsTtsError {
        override val userMessage = "Network error while contacting ElevenLabs. Check connection and retry."
    }
    data class Unauthorized(val detail: String? = null) : ElevenLabsTtsError {
        override val userMessage = "ElevenLabs rejected the API key. Check ELEVENLABS_API_KEY and rebuild."
    }
    data class RateLimited(val detail: String? = null) : ElevenLabsTtsError {
        override val userMessage = "ElevenLabs rate limit reached. Wait a moment and retry."
    }
    data class BadRequest(val detail: String? = null) : ElevenLabsTtsError {
        override val userMessage = "ElevenLabs could not generate this narration. Review the text and retry."
    }
    data object EmptyAudio : ElevenLabsTtsError {
        override val userMessage = "ElevenLabs returned an empty audio file. Nothing was saved."
    }
    data class Unknown(val detail: String? = null) : ElevenLabsTtsError {
        override val userMessage = "ElevenLabs generation failed unexpectedly. Retry later."
    }
}

sealed interface ElevenLabsTtsResult {
    data class Success(
        val outputFile: File,
        val format: String,
        val durationMs: Long? = null,
        val voiceId: String = TWEAKER_GEOGRAPHIC_VOICE_ID,
        val source: String = ELEVENLABS_SOURCE,
        val feature: String = TWEAKER_GEOGRAPHIC_FEATURE
    ) : ElevenLabsTtsResult

    data class Failure(val error: ElevenLabsTtsError) : ElevenLabsTtsResult
}
