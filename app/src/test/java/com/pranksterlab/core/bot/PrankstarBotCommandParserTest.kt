package com.pranksterlab.core.bot

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrankstarBotCommandParserTest {
    private val parser = PrankstarBotCommandParser()

    @Test
    fun recognizesTwakAttackCommand() {
        val intent = parser.parse("make a twak attack about fixing a bike")

        assertTrue(intent is PrankstarBotIntent.GenerateTweakerGeographic)
        intent as PrankstarBotIntent.GenerateTweakerGeographic
        assertEquals("fixing a bike", intent.action)
    }

    @Test
    fun recognizesTweakerGeographicCommand() {
        val intent = parser.parse("make a tweaker geographic about looking for a lighter")

        assertTrue(intent is PrankstarBotIntent.GenerateTweakerGeographic)
        intent as PrankstarBotIntent.GenerateTweakerGeographic
        assertEquals("looking for a lighter", intent.action)
    }

    @Test
    fun recognizesUrbanWildlifeReportCommand() {
        val intent = parser.parse("urban wildlife report about digging through a backpack")

        assertTrue(intent is PrankstarBotIntent.GenerateTweakerGeographic)
        intent as PrankstarBotIntent.GenerateTweakerGeographic
        assertEquals("digging through a backpack", intent.action)
    }
}
