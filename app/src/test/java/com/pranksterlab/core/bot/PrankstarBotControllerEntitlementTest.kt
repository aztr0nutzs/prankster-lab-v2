package com.pranksterlab.core.bot

import com.pranksterlab.components.bot.PrankstarBotMood
import com.pranksterlab.core.billing.FeatureGate
import com.pranksterlab.core.billing.PrankstarPlan
import com.pranksterlab.core.billing.UserEntitlement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrankstarBotControllerEntitlementTest {
    @Test
    fun freePlanLocksPremiumPrankPlanningAction() {
        val controller = PrankstarBotController(featureGate = FeatureGate.unconfiguredFree())

        val result = controller.handle("build a prank plan for office", emptyList())

        assertEquals(PrankstarBotMood.WARNING, result.mood)
        assertNull(result.prankPlan)
        assertTrue(result.message.contains("Prankstar Pro"))
    }

    @Test
    fun proPlanAllowsPremiumPrankPlanningAction() {
        val controller = PrankstarBotController(
            featureGate = FeatureGate(
                entitlement = UserEntitlement(
                    plan = PrankstarPlan.PRO_MONTHLY,
                    billingConfigured = true
                )
            )
        )

        val result = controller.handle("build a prank plan for office", emptyList())

        assertTrue(result.prankPlan != null)
        assertEquals(PrankstarBotMood.CELEBRATING, result.mood)
    }
}
