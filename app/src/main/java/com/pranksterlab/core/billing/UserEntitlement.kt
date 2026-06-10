package com.pranksterlab.core.billing

enum class PrankstarPlan {
    FREE,
    PRO_MONTHLY,
    PRO_YEARLY,
    LIFETIME_PRO
}

data class UserEntitlement(
    val plan: PrankstarPlan = PrankstarPlan.FREE,
    val billingConfigured: Boolean = false,
    val activeProductIds: Set<String> = emptySet(),
    val expiresAtMillis: Long? = null,
    val lastVerifiedAtMillis: Long? = null
) {
    val isPro: Boolean
        get() = plan == PrankstarPlan.PRO_MONTHLY ||
            plan == PrankstarPlan.PRO_YEARLY ||
            plan == PrankstarPlan.LIFETIME_PRO

    val displayName: String
        get() = when (plan) {
            PrankstarPlan.FREE -> "Free"
            PrankstarPlan.PRO_MONTHLY -> "Prankstar Pro Monthly"
            PrankstarPlan.PRO_YEARLY -> "Prankstar Pro Yearly"
            PrankstarPlan.LIFETIME_PRO -> "Lifetime Pro"
        }

    companion object {
        fun unconfiguredFree(): UserEntitlement = UserEntitlement()
    }
}
