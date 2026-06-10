package com.pranksterlab.core.voice

import com.pranksterlab.core.elevenlabs.ElevenLabsTtsError
import com.pranksterlab.core.elevenlabs.ElevenLabsTtsResult
import com.pranksterlab.core.elevenlabs.ElevenLabsTtsService
import java.io.File
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

interface NarrationVoiceProvider {
    suspend fun generateNarration(
        text: String,
        feature: String,
        voiceId: String,
        outputHint: String
    ): NarrationVoiceResult
}

sealed interface NarrationVoiceResult {
    data class Success(
        val outputFile: File,
        val format: String,
        val durationMs: Long? = null,
        val remainingCredits: Int? = null
    ) : NarrationVoiceResult

    data class Failure(
        val errorCode: NarrationVoiceErrorCode,
        val userMessage: String,
        val remainingCredits: Int? = null
    ) : NarrationVoiceResult
}

enum class NarrationVoiceErrorCode {
    UNAUTHENTICATED,
    NOT_ENTITLED,
    OUT_OF_CREDITS,
    RATE_LIMITED,
    TEXT_REJECTED,
    PROVIDER_FAILED,
    NETWORK_ERROR,
    NO_BACKEND_CONFIGURED,
    MISSING_API_KEY,
    UNSUPPORTED_LOCAL_PROVIDER
}

class LocalAndroidTtsVoiceProvider : NarrationVoiceProvider {
    override suspend fun generateNarration(
        text: String,
        feature: String,
        voiceId: String,
        outputHint: String
    ): NarrationVoiceResult {
        return NarrationVoiceResult.Failure(
            errorCode = NarrationVoiceErrorCode.UNSUPPORTED_LOCAL_PROVIDER,
            userMessage = "Local Android TTS uses the existing Voice Lab preset controls instead of this remote narration provider."
        )
    }
}

class DebugElevenLabsDirectProvider(
    private val outputDirectory: File,
    private val service: ElevenLabsTtsService
) : NarrationVoiceProvider {
    override suspend fun generateNarration(
        text: String,
        feature: String,
        voiceId: String,
        outputHint: String
    ): NarrationVoiceResult {
        val outputFile = File(outputDirectory, "${safeOutputName(outputHint)}_${System.currentTimeMillis()}.mp3")
        return when (val result = service.generateTweakerGeographicNarration(text, outputFile)) {
            is ElevenLabsTtsResult.Success -> NarrationVoiceResult.Success(
                outputFile = result.outputFile,
                format = result.format,
                durationMs = result.durationMs
            )
            is ElevenLabsTtsResult.Failure -> result.error.toNarrationFailure()
        }
    }
}

class ProductionBackendVoiceProvider(
    private val client: OkHttpClient,
    private val backendBaseUrlProvider: () -> String,
    private val authTokenProvider: () -> String?,
    private val outputDirectory: File,
    private val toneProvider: () -> String = { "BALANCED" }
) : NarrationVoiceProvider {
    override suspend fun generateNarration(
        text: String,
        feature: String,
        voiceId: String,
        outputHint: String
    ): NarrationVoiceResult = withContext(Dispatchers.IO) {
        val baseUrl = backendBaseUrlProvider().trim().trimEnd('/')
        if (baseUrl.isBlank()) {
            return@withContext NarrationVoiceResult.Failure(
                errorCode = NarrationVoiceErrorCode.NO_BACKEND_CONFIGURED,
                userMessage = "Narration backend is not configured yet."
            )
        }

        val token = authTokenProvider()?.trim().orEmpty()
        if (token.isBlank()) {
            return@withContext NarrationVoiceResult.Failure(
                errorCode = NarrationVoiceErrorCode.UNAUTHENTICATED,
                userMessage = "Sign in is required before using premium narration."
            )
        }

        val body = JSONObject()
            .put("feature", feature)
            .put("text", text.trim())
            .put("voiceId", voiceId)
            .put("tone", toneProvider())
            .put("clientRequestId", UUID.randomUUID().toString())
            .toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/api/generate-narration")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "audio/mpeg, application/json")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        try {
            outputDirectory.mkdirs()
            client.newCall(request).execute().use { response ->
                val contentType = response.header("Content-Type").orEmpty()
                if (response.isSuccessful && contentType.contains("audio/", ignoreCase = true)) {
                    return@withContext writeAudioBytes(
                        bytes = response.body?.bytes() ?: ByteArray(0),
                        outputHint = outputHint,
                        remainingCredits = response.header("X-Remaining-Credits")?.toIntOrNull()
                    )
                }

                val payload = response.body?.string().orEmpty()
                if (response.isSuccessful && payload.isNotBlank()) {
                    val json = JSONObject(payload)
                    val remainingCredits = json.optIntOrNull("remainingCredits")
                    val audioUrl = json.optString("audioUrl").takeIf { it.isNotBlank() }
                    if (audioUrl != null) {
                        return@withContext downloadSignedAudio(audioUrl, outputHint, remainingCredits)
                    }
                    return@withContext NarrationVoiceResult.Failure(
                        errorCode = NarrationVoiceErrorCode.PROVIDER_FAILED,
                        userMessage = "Narration backend did not return audio."
                    )
                }

                parseBackendFailure(response.code, payload)
            }
        } catch (_: IOException) {
            NarrationVoiceResult.Failure(
                errorCode = NarrationVoiceErrorCode.NETWORK_ERROR,
                userMessage = "Network error while contacting narration backend."
            )
        } catch (_: Exception) {
            NarrationVoiceResult.Failure(
                errorCode = NarrationVoiceErrorCode.PROVIDER_FAILED,
                userMessage = "Narration backend failed unexpectedly."
            )
        }
    }

    private fun downloadSignedAudio(audioUrl: String, outputHint: String, remainingCredits: Int?): NarrationVoiceResult {
        val request = Request.Builder().url(audioUrl).get().build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return NarrationVoiceResult.Failure(
                    errorCode = NarrationVoiceErrorCode.PROVIDER_FAILED,
                    userMessage = "Narration audio was generated but could not be downloaded.",
                    remainingCredits = remainingCredits
                )
            }
            return writeAudioBytes(response.body?.bytes() ?: ByteArray(0), outputHint, remainingCredits)
        }
    }

    private fun writeAudioBytes(bytes: ByteArray, outputHint: String, remainingCredits: Int?): NarrationVoiceResult {
        if (bytes.isEmpty()) {
            return NarrationVoiceResult.Failure(
                errorCode = NarrationVoiceErrorCode.PROVIDER_FAILED,
                userMessage = "Narration backend returned an empty audio file.",
                remainingCredits = remainingCredits
            )
        }
        val outputFile = File(outputDirectory, "${safeOutputName(outputHint)}_${System.currentTimeMillis()}.mp3")
        outputFile.parentFile?.mkdirs()
        outputFile.writeBytes(bytes)
        return NarrationVoiceResult.Success(outputFile, "mp3", remainingCredits = remainingCredits)
    }

    private fun parseBackendFailure(statusCode: Int, payload: String): NarrationVoiceResult.Failure {
        val json = runCatching { JSONObject(payload) }.getOrNull()
        val code = json?.optString("errorCode")?.takeIf { it.isNotBlank() }
        val remainingCredits = json?.optIntOrNull("remainingCredits")
        val errorCode = code?.let { runCatching { NarrationVoiceErrorCode.valueOf(it) }.getOrNull() }
            ?: when (statusCode) {
                401 -> NarrationVoiceErrorCode.UNAUTHENTICATED
                402 -> NarrationVoiceErrorCode.OUT_OF_CREDITS
                403 -> NarrationVoiceErrorCode.NOT_ENTITLED
                409, 422 -> NarrationVoiceErrorCode.TEXT_REJECTED
                429 -> NarrationVoiceErrorCode.RATE_LIMITED
                in 500..599 -> NarrationVoiceErrorCode.PROVIDER_FAILED
                else -> NarrationVoiceErrorCode.NETWORK_ERROR
            }
        return NarrationVoiceResult.Failure(errorCode, errorCode.defaultUserMessage(), remainingCredits)
    }
}

private fun ElevenLabsTtsError.toNarrationFailure(): NarrationVoiceResult.Failure {
    val code = when (this) {
        ElevenLabsTtsError.MissingApiKey -> NarrationVoiceErrorCode.MISSING_API_KEY
        ElevenLabsTtsError.BlankText,
        is ElevenLabsTtsError.TextTooLong,
        is ElevenLabsTtsError.BadRequest -> NarrationVoiceErrorCode.TEXT_REJECTED
        is ElevenLabsTtsError.NetworkError -> NarrationVoiceErrorCode.NETWORK_ERROR
        is ElevenLabsTtsError.Unauthorized -> NarrationVoiceErrorCode.UNAUTHENTICATED
        is ElevenLabsTtsError.RateLimited -> NarrationVoiceErrorCode.RATE_LIMITED
        ElevenLabsTtsError.EmptyAudio,
        is ElevenLabsTtsError.Unknown -> NarrationVoiceErrorCode.PROVIDER_FAILED
    }
    return NarrationVoiceResult.Failure(code, userMessage)
}

private fun NarrationVoiceErrorCode.defaultUserMessage(): String {
    return when (this) {
        NarrationVoiceErrorCode.UNAUTHENTICATED -> "Sign in is required before using premium narration."
        NarrationVoiceErrorCode.NOT_ENTITLED -> "Premium narration is not enabled for this account."
        NarrationVoiceErrorCode.OUT_OF_CREDITS -> "No narration credits remain."
        NarrationVoiceErrorCode.RATE_LIMITED -> "Narration requests are rate limited. Wait a moment and retry."
        NarrationVoiceErrorCode.TEXT_REJECTED -> "Narration text was rejected by the safety check."
        NarrationVoiceErrorCode.PROVIDER_FAILED -> "Narration provider failed. Retry later."
        NarrationVoiceErrorCode.NETWORK_ERROR -> "Network error while generating narration."
        NarrationVoiceErrorCode.NO_BACKEND_CONFIGURED -> "Narration backend is not configured yet."
        NarrationVoiceErrorCode.MISSING_API_KEY -> "Debug ElevenLabs key is missing."
        NarrationVoiceErrorCode.UNSUPPORTED_LOCAL_PROVIDER -> "Use local Voice Lab generation for Android TTS."
    }
}

private fun JSONObject.optIntOrNull(name: String): Int? {
    return if (has(name) && !isNull(name)) optInt(name) else null
}

private fun safeOutputName(value: String): String {
    return value
        .lowercase()
        .replace(Regex("[^a-z0-9_-]+"), "_")
        .trim('_')
        .take(48)
        .ifBlank { "narration" }
}
