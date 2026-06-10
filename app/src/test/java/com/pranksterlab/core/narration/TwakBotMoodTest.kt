package com.pranksterlab.core.narration

import org.junit.Assert.assertEquals
import org.junit.Test

class TwakBotMoodTest {
    @Test
    fun moodsMapToExpectedResourceNames() {
        val expected = mapOf(
            TwakBotMood.IDLE to "twakbot_idle",
            TwakBotMood.SEARCHING to "twakbot_searching",
            TwakBotMood.GENERATING to "twakbot_generating",
            TwakBotMood.EXCITED to "twakbot_excited",
            TwakBotMood.ERROR to "twakbot_error",
            TwakBotMood.REFUSAL to "twakbot_error",
            TwakBotMood.SAVED to "twakbot_excited",
            TwakBotMood.PREVIEWING to "twakbot_excited"
        )

        expected.forEach { (mood, resourceName) ->
            assertEquals(resourceName, mood.rawResourceName)
        }
    }

    @Test
    fun rawResourceIdUsesResourceNameAndRawType() {
        val resolved = TwakBotMood.GENERATING.rawResourceId("com.pranksterlab") { name, type, packageName ->
            assertEquals("twakbot_generating", name)
            assertEquals("raw", type)
            assertEquals("com.pranksterlab", packageName)
            42
        }

        assertEquals(42, resolved)
    }
}
