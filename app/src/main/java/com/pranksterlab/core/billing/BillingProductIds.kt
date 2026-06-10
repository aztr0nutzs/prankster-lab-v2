package com.pranksterlab.core.billing

object BillingProductIds {
    const val PRO_MONTHLY = "prankstar_pro_monthly"
    const val PRO_YEARLY = "prankstar_pro_yearly"
    const val PRO_LIFETIME = "prankstar_pro_lifetime"
    const val VOICE_CREDITS_SMALL = "voice_credits_25"
    const val VOICE_CREDITS_MEDIUM = "voice_credits_100"
    const val VOICE_CREDITS_LARGE = "voice_credits_300"

    val subscriptionIds = setOf(PRO_MONTHLY, PRO_YEARLY)
    val nonConsumableIds = setOf(PRO_LIFETIME)
    val consumableCreditPackIds = setOf(
        VOICE_CREDITS_SMALL,
        VOICE_CREDITS_MEDIUM,
        VOICE_CREDITS_LARGE
    )
}

enum class BillingProductType {
    SUBSCRIPTION,
    NON_CONSUMABLE,
    CONSUMABLE
}

data class BillingProductDefinition(
    val productId: String,
    val type: BillingProductType,
    val displayName: String,
    val includedVoiceCredits: Int? = null
)

enum class VoiceCreditPack(
    val productId: String,
    val credits: Int,
    val displayName: String
) {
    SMALL(BillingProductIds.VOICE_CREDITS_SMALL, 25, "Voice Credits Small"),
    MEDIUM(BillingProductIds.VOICE_CREDITS_MEDIUM, 100, "Voice Credits Medium"),
    LARGE(BillingProductIds.VOICE_CREDITS_LARGE, 300, "Voice Credits Large")
}

object BillingProducts {
    val proMonthly = BillingProductDefinition(
        productId = BillingProductIds.PRO_MONTHLY,
        type = BillingProductType.SUBSCRIPTION,
        displayName = "Prankstar Pro Monthly",
        includedVoiceCredits = 150
    )
    val proYearly = BillingProductDefinition(
        productId = BillingProductIds.PRO_YEARLY,
        type = BillingProductType.SUBSCRIPTION,
        displayName = "Prankstar Pro Yearly",
        includedVoiceCredits = 150
    )
    val proLifetime = BillingProductDefinition(
        productId = BillingProductIds.PRO_LIFETIME,
        type = BillingProductType.NON_CONSUMABLE,
        displayName = "Lifetime Pro"
    )

    val all = listOf(
        proMonthly,
        proYearly,
        proLifetime
    ) + VoiceCreditPack.entries.map { pack ->
        BillingProductDefinition(
            productId = pack.productId,
            type = BillingProductType.CONSUMABLE,
            displayName = pack.displayName,
            includedVoiceCredits = pack.credits
        )
    }
}
