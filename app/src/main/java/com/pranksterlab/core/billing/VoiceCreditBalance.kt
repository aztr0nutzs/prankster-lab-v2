package com.pranksterlab.core.billing

data class VoiceCreditBalance(
    val configured: Boolean = false,
    val monthlyIncludedRemaining: Int? = null,
    val purchasedRemaining: Int? = null,
    val refreshesAtMillis: Long? = null
) {
    val remainingVoiceCredits: Int?
        get() {
            val monthly = monthlyIncludedRemaining
            val purchased = purchasedRemaining
            return if (monthly == null && purchased == null) null else (monthly ?: 0) + (purchased ?: 0)
        }

    val statusLabel: String
        get() = remainingVoiceCredits?.let { "$it credits remaining" } ?: "Credits not configured"

    companion object {
        fun unconfigured(): VoiceCreditBalance = VoiceCreditBalance()
    }
}
