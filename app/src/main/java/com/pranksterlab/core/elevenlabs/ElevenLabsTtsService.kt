package com.pranksterlab.core.elevenlabs

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

class ElevenLabsTtsService(
    private val client: OkHttpClient,
    private val apiKeyProvider: () -> String,
    private val endpointBaseUrl: String = "https://api.elevenlabs.io/v1/text-to-speech"
) {
    suspend fun generateTweakerGeographicNarration(
        text: String,
        outputFile: File
    ): ElevenLabsTtsResult = withContext(Dispatchers.IO) {
        val normalizedText = text.trim()
        if (normalizedText.isBlank()) {
            return@withContext ElevenLabsTtsResult.Failure(ElevenLabsTtsError.BlankText)
        }
        if (normalizedText.length > TWEAKER_GEOGRAPHIC_MAX_CHARS) {
            return@withContext ElevenLabsTtsResult.Failure(
                ElevenLabsTtsError.TextTooLong(TWEAKER_GEOGRAPHIC_MAX_CHARS)
            )
        }
        val apiKey = apiKeyProvider().trim()
        if (apiKey.isBlank()) {
            return@withContext ElevenLabsTtsResult.Failure(ElevenLabsTtsError.MissingApiKey)
        }

        val requestBody = JSONObject()
            .put("text", normalizedText)
            .put("model_id", DEFAULT_MODEL_ID)
            .put(
                "voice_settings",
                JSONObject()
                    .put("stability", 0.55)
                    .put("similarity_boost", 0.75)
                    .put("style", 0.25)
                    .put("use_speaker_boost", true)
            )
            .toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$endpointBaseUrl/$TWEAKER_GEOGRAPHIC_VOICE_ID?output_format=$TWEAKER_GEOGRAPHIC_OUTPUT_FORMAT")
            .addHeader("xi-api-key", apiKey)
            .addHeader("Accept", "audio/mpeg")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        try {
            outputFile.parentFile?.mkdirs()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    outputFile.delete()
                    return@withContext ElevenLabsTtsResult.Failure(mapHttpError(response.code, response.message))
                }
                val bytes = response.body?.bytes().orEmpty()
                if (bytes.isEmpty()) {
                    outputFile.delete()
                    return@withContext ElevenLabsTtsResult.Failure(ElevenLabsTtsError.EmptyAudio)
                }
                outputFile.writeBytes(bytes)
                if (!outputFile.exists() || outputFile.length() <= 0L) {
                    outputFile.delete()
                    return@withContext ElevenLabsTtsResult.Failure(ElevenLabsTtsError.EmptyAudio)
                }
                ElevenLabsTtsResult.Success(
                    outputFile = outputFile,
                    format = "mp3",
                    durationMs = null
                )
            }
        } catch (e: IOException) {
            outputFile.delete()
            ElevenLabsTtsResult.Failure(ElevenLabsTtsError.NetworkError(e.message ?: e.javaClass.simpleName))
        } catch (e: Exception) {
            outputFile.delete()
            ElevenLabsTtsResult.Failure(ElevenLabsTtsError.Unknown(e.message ?: e.javaClass.simpleName))
        }
    }

    private fun mapHttpError(code: Int, message: String): ElevenLabsTtsError {
        return when (code) {
            400, 422 -> ElevenLabsTtsError.BadRequest(message)
            401, 403 -> ElevenLabsTtsError.Unauthorized(message)
            429 -> ElevenLabsTtsError.RateLimited(message)
            in 500..599 -> ElevenLabsTtsError.NetworkError("HTTP $code $message")
            else -> ElevenLabsTtsError.Unknown("HTTP $code $message")
        }
    }
}
