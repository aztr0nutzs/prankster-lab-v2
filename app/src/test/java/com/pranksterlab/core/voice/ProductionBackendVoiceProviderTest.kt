package com.pranksterlab.core.voice

import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionBackendVoiceProviderTest {
    @Test
    fun missingBackendReturnsNoBackendConfigured() = runTest {
        val result = provider(backendBaseUrl = "", authToken = "token")
            .generateNarration("hello", "twak_attacks", "voice", "test")

        assertTrue(result is NarrationVoiceResult.Failure)
        assertEquals(
            NarrationVoiceErrorCode.NO_BACKEND_CONFIGURED,
            (result as NarrationVoiceResult.Failure).errorCode
        )
    }

    @Test
    fun missingAuthTokenReturnsUnauthenticated() = runTest {
        val result = provider(backendBaseUrl = "https://voice.example", authToken = null)
            .generateNarration("hello", "twak_attacks", "voice", "test")

        assertTrue(result is NarrationVoiceResult.Failure)
        assertEquals(
            NarrationVoiceErrorCode.UNAUTHENTICATED,
            (result as NarrationVoiceResult.Failure).errorCode
        )
    }

    @Test
    fun audioResponseWritesMp3BytesAndRemainingCredits() = runTest {
        val bytes = byteArrayOf(0x49, 0x44, 0x33, 0x03)
        val outputDir = tempDir("backend_voice_success")
        val result = provider(
            outputDirectory = outputDir,
            responseCode = 200,
            responseBody = bytes,
            contentType = "audio/mpeg",
            remainingCreditsHeader = "7"
        ).generateNarration("hello", "twak_attacks", "voice", "Field Report")

        assertTrue(result is NarrationVoiceResult.Success)
        val success = result as NarrationVoiceResult.Success
        assertEquals("mp3", success.format)
        assertEquals(7, success.remainingCredits)
        assertTrue(success.outputFile.exists())
        assertArrayEquals(bytes, success.outputFile.readBytes())
    }

    @Test
    fun outOfCreditsJsonMapsToCreditError() = runTest {
        val result = provider(
            responseCode = 402,
            responseBody = """{"success":false,"errorCode":"OUT_OF_CREDITS","remainingCredits":0}""".toByteArray(),
            contentType = "application/json"
        ).generateNarration("hello", "twak_attacks", "voice", "test")

        assertTrue(result is NarrationVoiceResult.Failure)
        val failure = result as NarrationVoiceResult.Failure
        assertEquals(NarrationVoiceErrorCode.OUT_OF_CREDITS, failure.errorCode)
        assertEquals(0, failure.remainingCredits)
    }

    private fun provider(
        backendBaseUrl: String = "https://voice.example",
        authToken: String? = "token",
        outputDirectory: File = tempDir("backend_voice"),
        responseCode: Int = 200,
        responseBody: ByteArray = byteArrayOf(1, 2, 3),
        contentType: String = "audio/mpeg",
        remainingCreditsHeader: String? = null
    ): ProductionBackendVoiceProvider {
        val client = OkHttpClient.Builder()
            .addInterceptor(fakeResponseInterceptor(responseCode, responseBody, contentType, remainingCreditsHeader))
            .build()
        return ProductionBackendVoiceProvider(
            client = client,
            backendBaseUrlProvider = { backendBaseUrl },
            authTokenProvider = { authToken },
            outputDirectory = outputDirectory,
            toneProvider = { "CHAOTIC" }
        )
    }

    private fun fakeResponseInterceptor(
        code: Int,
        body: ByteArray,
        contentType: String,
        remainingCreditsHeader: String?
    ): Interceptor {
        return Interceptor { chain ->
            val builder = Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(if (code in 200..299) "OK" else "Error")
                .header("Content-Type", contentType)
                .body(body.toResponseBody(contentType.toMediaType()))
            if (remainingCreditsHeader != null) {
                builder.header("X-Remaining-Credits", remainingCreditsHeader)
            }
            builder.build()
        }
    }

    private fun tempDir(prefix: String): File {
        return Files.createTempDirectory(prefix).toFile()
    }
}
