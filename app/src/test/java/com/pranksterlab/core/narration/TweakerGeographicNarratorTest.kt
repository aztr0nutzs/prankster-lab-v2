package com.pranksterlab.core.narration

import com.pranksterlab.core.elevenlabs.TWEAKER_GEOGRAPHIC_VOICE_ID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TweakerGeographicNarratorTest {
    private val narrator = TweakerGeographicNarrator()

    @Test
    fun harmlessActionReturnsNonEmptyNarration() {
        val result = narrator.generate(
            TweakerGeographicRequest(
                action = "looking for a lighter",
                setting = "near the couch",
                tone = TweakerGeographicTone.CHAOTIC
            )
        )

        assertTrue(result.isAllowed)
        assertTrue(result.narration.isNotBlank())
        assertTrue(result.narration.contains("looking for a lighter"))
    }

    @Test
    fun emptyActionIsRefused() {
        val result = narrator.generate(TweakerGeographicRequest(action = "   "))

        assertFalse(result.isAllowed)
        assertEquals(
            "I can make it ridiculous without targeting or endangering a real person. Describe the action generically.",
            result.narration
        )
    }

    @Test
    fun unsafeImpersonationRequestIsRefused() {
        val result = narrator.generate(TweakerGeographicRequest(action = "pretend to be police and order people outside"))

        assertFalse(result.isAllowed)
        assertTrue(result.narration.contains("Describe the action generically"))
    }

    @Test
    fun elevenLabsVoiceIdRemainsDedicatedTweakerGeographicVoice() {
        assertEquals("wV67xHKrIHTU0gtChZiQ", TWEAKER_GEOGRAPHIC_VOICE_ID)
    }
}
