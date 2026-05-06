package com.pranksterlab.core.model

import com.google.gson.annotations.SerializedName

data class PrankSound(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("packId") val packId: String? = null,
    @SerializedName("assetPath") val assetPath: String,
    @SerializedName("durationMs") val durationMs: Long = 0L,
    @SerializedName("tags") val tags: List<String> = emptyList(),
    @SerializedName("loopable") val loopable: Boolean = false,
    @SerializedName("intensityLevel") val intensityLevel: Int = 1,
    @SerializedName("isSafeForRandomMode") val isSafeForRandomMode: Boolean = true,
    @SerializedName("description") val description: String = "",
    @SerializedName("recommendedUse") val recommendedUse: String = "",
    @SerializedName("prankStyle") val prankStyle: String = "",
    @SerializedName("previewLabel") val previewLabel: String = "",
    // Metadata for custom user sounds
    @SerializedName("isCustom") val isCustom: Boolean = false,
    @SerializedName("createdAt") val createdAt: Long = System.currentTimeMillis(),
    val localUri: String? = null,
    val sourceType: SoundSourceType = SoundSourceType.ASSET,
    val createdByUser: Boolean = false,
    val generatedMetadata: GeneratedSoundMetadata? = null
)
