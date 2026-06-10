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
