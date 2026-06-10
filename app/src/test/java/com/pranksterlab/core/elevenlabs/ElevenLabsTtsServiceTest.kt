package com.pranksterlab.core.elevenlabs

import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ElevenLabsTtsServiceTest {
    @Test
    fun missingApiKeyReturnsMissingApiKey() = runTest {
        val file = tempFile("missing")
        val result = service(apiKey = "").generateTweakerGeographicNarration("hello", file)

        assertTrue(result is ElevenLabsTtsResult.Failure)
        assertTrue((result as ElevenLabsTtsResult.Failure).error is ElevenLabsTtsError.MissingApiKey)
        assertFalse(file.exists() && file.length() > 0L)
    }

    @Test
    fun blankTextReturnsValidationError() = runTest {
        val result = service(apiKey = "key").generateTweakerGeographicNarration("   ", tempFile("blank"))

        assertTrue(result is ElevenLabsTtsResult.Failure)
        assertTrue((result as ElevenLabsTtsResult.Failure).error is ElevenLabsTtsError.BlankText)
    }

    @Test
    fun successfulFakeResponseWritesMp3Bytes() = runTest {
        val expected = byteArrayOf(0x49, 0x44, 0x33, 0x03)
        val file = tempFile("success")
        val result = service(apiKey = "key", code = 200, body = expected)
            .generateTweakerGeographicNarration("Here we observe the field test.", file)

        assertTrue(result is ElevenLabsTtsResult.Success)
        val success = result as ElevenLabsTtsResult.Success
        assertEquals(TWEAKER_GEOGRAPHIC_VOICE_ID, success.voiceId)
        assertEquals("mp3", success.format)
        assertTrue(file.exists())
        assertArrayEquals(expected, file.readBytes())
    }

    @Test
    fun emptyResponseReturnsEmptyAudio() = runTest {
        val file = tempFile("empty")
        val result = service(apiKey = "key", code = 200, body = byteArrayOf())
            .generateTweakerGeographicNarration("hello", file)

        assertTrue(result is ElevenLabsTtsResult.Failure)
        assertTrue((result as ElevenLabsTtsResult.Failure).error is ElevenLabsTtsError.EmptyAudio)
        assertFalse(file.exists())
    }

    @Test
    fun unauthorizedStatusReturnsUnauthorized() = runTest {
        val result = service(apiKey = "bad", code = 401)
            .generateTweakerGeographicNarration("hello", tempFile("unauthorized"))

        assertTrue(result is ElevenLabsTtsResult.Failure)
        assertTrue((result as ElevenLabsTtsResult.Failure).error is ElevenLabsTtsError.Unauthorized)
    }

    @Test
    fun rateLimitedStatusReturnsRateLimited() = runTest {
        val result = service(apiKey = "key", code = 429)
            .generateTweakerGeographicNarration("hello", tempFile("rate"))

        assertTrue(result is ElevenLabsTtsResult.Failure)
        assertTrue((result as ElevenLabsTtsResult.Failure).error is ElevenLabsTtsError.RateLimited)
    }

    private fun service(
        apiKey: String,
        code: Int = 200,
        body: ByteArray = byteArrayOf(1, 2, 3)
    ): ElevenLabsTtsService {
        val client = OkHttpClient.Builder()
            .addInterceptor(fakeResponseInterceptor(code, body))
            .build()
        return ElevenLabsTtsService(client = client, apiKeyProvider = { apiKey })
    }

    private fun fakeResponseInterceptor(code: Int, body: ByteArray): Interceptor {
        return Interceptor { chain ->
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(if (code in 200..299) "OK" else "Error")
                .body(body.toResponseBody("audio/mpeg".toMediaType()))
                .build()
        }
    }

    private fun tempFile(name: String): File {
        val dir = createTempDir(prefix = "elevenlabs_test_$name")
        return File(dir, "tweaker_geo_$name.mp3")
    }
}
