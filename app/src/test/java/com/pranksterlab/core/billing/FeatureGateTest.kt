package com.pranksterlab.core.billing

import com.pranksterlab.core.narration.TweakerGeographicTone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureGateTest {
    @Test
    fun unconfiguredFreeKeepsPremiumFeaturesLocked() {
        val gate = FeatureGate.unconfiguredFree()

        assertFalse(gate.canUseElevenLabsNarrator)
        assertFalse(gate.canUseAdvancedTwakTones)
        assertFalse(gate.canUsePremiumBotActions)
        assertEquals(null, gate.remainingVoiceCredits)
        assertEquals("Credits not configured", gate.voiceCredits.statusLabel)
    }

    @Test
    fun freePlanAllowsCoreLocalLimitsOnly() {
        val gate = FeatureGate.unconfiguredFree()

        assertTrue(gate.canSaveGeneratedClip(0))
        assertTrue(gate.canSaveGeneratedClip(4))
        assertFalse(gate.canSaveGeneratedClip(5))
        assertTrue(gate.canGenerateTwakText(0))
        assertTrue(gate.canGenerateTwakText(4))
        assertFalse(gate.canGenerateTwakText(5))
        assertTrue(gate.isToneAllowed(TweakerGeographicTone.MILD))
        assertTrue(gate.isToneAllowed(TweakerGeographicTone.BALANCED))
        assertFalse(gate.isToneAllowed(TweakerGeographicTone.CHAOTIC))
    }

    @Test
    fun proRequiresCreditsForElevenLabsNarrator() {
        val noCredits = FeatureGate(
            entitlement = UserEntitlement(plan = PrankstarPlan.PRO_MONTHLY, billingConfigured = true),
            voiceCredits = VoiceCreditBalance(configured = true, monthlyIncludedRemaining = 0, purchasedRemaining = 0)
        )
        val withCredits = noCredits.copy(
            voiceCredits = VoiceCreditBalance(configured = true, monthlyIncludedRemaining = 12, purchasedRemaining = 3)
        )

        assertFalse(noCredits.canUseElevenLabsNarrator)
        assertTrue(withCredits.canUseElevenLabsNarrator)
        assertEquals(15, withCredits.remainingVoiceCredits)
    }

    @Test
    fun productDefinitionsMatchPlayProductIds() {
        val productIds = BillingProducts.all.map { it.productId }.toSet()

        assertTrue(productIds.contains(BillingProductIds.PRO_MONTHLY))
        assertTrue(productIds.contains(BillingProductIds.PRO_YEARLY))
        assertTrue(productIds.contains(BillingProductIds.PRO_LIFETIME))
        assertTrue(productIds.contains(BillingProductIds.VOICE_CREDITS_SMALL))
        assertTrue(productIds.contains(BillingProductIds.VOICE_CREDITS_MEDIUM))
        assertTrue(productIds.contains(BillingProductIds.VOICE_CREDITS_LARGE))
        assertEquals(25, VoiceCreditPack.SMALL.credits)
        assertEquals(100, VoiceCreditPack.MEDIUM.credits)
        assertEquals(300, VoiceCreditPack.LARGE.credits)
    }
}
