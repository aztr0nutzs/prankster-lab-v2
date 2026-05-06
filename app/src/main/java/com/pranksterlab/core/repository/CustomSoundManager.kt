package com.pranksterlab.core.repository

import android.content.Context
import android.net.Uri
import com.pranksterlab.core.model.PrankCategory
import com.pranksterlab.core.model.PrankSound
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CustomSoundManager(
    private val context: Context,
    private val repository: SoundRepository
) {
    /**
     * Simulates importing a file from a URI (e.g., from the Android file picker),
     * copying it to internal storage, and registering it in DataStore.
     */
    suspend fun importAndRegisterSound(
        sourceUri: Uri,
        name: String,
        tags: List<String> = emptyList()
    ): PrankSound? {
        try {
            // Create a unique filename for internal storage
            val uniqueId = "custom_${UUID.randomUUID().toString().take(8)}"
            val fileName = "$uniqueId.ogg"
            val internalFile = File(context.filesDir, fileName)

            // Copy from source URI to internal storage
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(internalFile).use { output ->
                    input.copyTo(output)
                }
            }

            // Create the domain model for the new custom sound
            val newSound = PrankSound(
                id = uniqueId,
                name = name,
                category = PrankCategory.CUSTOM.name,
                packId = "user_custom",
                assetPath = internalFile.absolutePath, // Use absolute path or custom scheme for playback
                durationMs = 0L, // In a real app, use MediaMetadataRetriever to get duration
                tags = tags + listOf("custom", "user_uploaded"),
                loopable = false,
                isCustom = true,
                createdAt = System.currentTimeMillis()
            )

            // Persist the metadata
            repository.saveCustomSound(newSound)
            
            return newSound
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Simulates trimming a sound file. In a real app, this would use FFmpeg or MediaCodec.
     * Here we just create a new metadata entry pointing to the trimmed logic.
     */
    suspend fun trimCustomSound(
        originalSound: PrankSound,
        startTimeMs: Long,
        endTimeMs: Long
    ): PrankSound {
        // Create a new sound record representing the trimmed version
        val trimmedId = "${originalSound.id}_trimmed_${System.currentTimeMillis()}"
        
        val trimmedSound = originalSound.copy(
            id = trimmedId,
            name = "${originalSound.name} (Trimmed)",
            durationMs = endTimeMs - startTimeMs,
            createdAt = System.currentTimeMillis()
            // Physical file trimming logic would happen here, saving to a new file.
        )
        
        repository.saveCustomSound(trimmedSound)
        return trimmedSound
    }

    suspend fun addCustomSound(sound: PrankSound) {
        repository.saveCustomSound(sound)
    }
}
