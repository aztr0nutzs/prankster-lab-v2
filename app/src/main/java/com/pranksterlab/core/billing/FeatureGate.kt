package com.pranksterlab.core.billing

import com.pranksterlab.core.narration.TweakerGeographicTone

private const val FREE_GENERATED_CLIP_SAVE_LIMIT = 5
private const val FREE_TWAK_TEXT_GENERATION_LIMIT = 5

data class FeatureGate(
    val entitlement: UserEntitlement,
    val voiceCredits: VoiceCreditBalance = VoiceCreditBalance.unconfigured()
) {
    val canUseElevenLabsNarrator: Boolean
        get() = entitlement.isPro && (voiceCredits.remainingVoiceCredits ?: 0) > 0

    val canUseAdvancedTwakTones: Boolean
        get() = entitlement.isPro

    val canUsePremiumBotActions: Boolean
        get() = entitlement.isPro

    val remainingVoiceCredits: Int?
        get() = voiceCredits.remainingVoiceCredits

    fun canSaveGeneratedClip(currentGeneratedClipCount: Int): Boolean {
        return entitlement.isPro || currentGeneratedClipCount < FREE_GENERATED_CLIP_SAVE_LIMIT
    }

    fun canGenerateTwakText(currentSessionGenerationCount: Int): Boolean {
        return entitlement.isPro || currentSessionGenerationCount < FREE_TWAK_TEXT_GENERATION_LIMIT
    }

    fun isToneAllowed(tone: TweakerGeographicTone): Boolean {
        return canUseAdvancedTwakTones || tone in freeTwakTones
    }

    fun generatedClipSaveLimitLabel(): String {
        return if (entitlement.isPro) "Unlimited generated clip saves" else "$FREE_GENERATED_CLIP_SAVE_LIMIT free generated clip saves"
    }

    fun twakTextLimitLabel(): String {
        return if (entitlement.isPro) "Unlimited Twak-Attacks text generation" else "$FREE_TWAK_TEXT_GENERATION_LIMIT free Twak-Attacks text generations"
    }

    companion object {
        val freeTwakTones = setOf(
            TweakerGeographicTone.MILD,
            TweakerGeographicTone.BALANCED
        )

        fun unconfiguredFree(): FeatureGate {
            return FeatureGate(
                entitlement = UserEntitlement.unconfiguredFree(),
                voiceCredits = VoiceCreditBalance.unconfigured()
            )
        }
    }
}
