package com.pranksterlab.core.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pranksterlab.core.model.PrankSound
import com.pranksterlab.core.model.SoundSequencePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStreamReader

import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "custom_sounds")

class SoundRepository(private val context: Context) {
    private val gson = Gson()
    private val CUSTOM_SOUNDS_KEY = stringPreferencesKey("custom_sounds_json")
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_sound_ids")
    private val SEQUENCE_PRESETS_KEY = stringPreferencesKey("sequence_presets_json")
    private val _activePackFilter = MutableStateFlow<String?>(null)
    val activePackFilter: StateFlow<String?> = _activePackFilter

    /**
     * Loads the bundled sound catalog from assets.
     */
    fun getBundledSounds(): List<PrankSound> {
        return try {
            val inputStream = context.assets.open("sound_catalog.json")
            val reader = InputStreamReader(inputStream)
            val listType = object : TypeToken<List<PrankSound>>() {}.type
            gson.fromJson(reader, listType) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun isCatalogSoundPlayable(sound: PrankSound): Boolean {
        if (sound.assetPath.isBlank()) return false
        return try {
            context.assets.open(sound.assetPath).use { stream ->
                stream.available() >= 0
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun buildPackSummaries(sounds: List<PrankSound>): List<PackSummary> {
        return sounds
            .filter { !it.packId.isNullOrBlank() }
            .groupBy { it.packId!!.trim() }
            .map { (packId, packSounds) ->
                val categoryFocus = packSounds.groupingBy { it.category }.eachCount().maxByOrNull { it.value }?.key ?: "MISC"
                PackSummary(
                    packId = packId,
                    soundCount = packSounds.size,
                    categoryFocus = categoryFocus
                )
            }
            .sortedBy { it.packId }
    }

    fun setActivePackFilter(packId: String?) {
        _activePackFilter.value = packId
    }

    /**
     * Exposes a reactive stream of the user's custom created/trimmed sounds.
     */
    fun getCustomSoundsFlow(): Flow<List<PrankSound>> {
        return context.dataStore.data.map { preferences ->
            val jsonString = preferences[CUSTOM_SOUNDS_KEY] ?: "[]"
            val listType = object : TypeToken<List<PrankSound>>() {}.type
            try {
                gson.fromJson<List<PrankSound>>(jsonString, listType) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    /**
     * Saves a newly imported or trimmed user sound to DataStore.
     * Overwrites if the ID already exists.
     */
    suspend fun saveCustomSound(sound: PrankSound) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[CUSTOM_SOUNDS_KEY] ?: "[]"
            val listType = object : TypeToken<List<PrankSound>>() {}.type
            val currentList: MutableList<PrankSound> = try {
                gson.fromJson(currentJson, listType) ?: mutableListOf()
            } catch (e: Exception) {
                mutableListOf()
            }
            
            val existingIndex = currentList.indexOfFirst { it.id == sound.id }
            if (existingIndex >= 0) {
                currentList[existingIndex] = sound
            } else {
                currentList.add(sound)
            }
            
            preferences[CUSTOM_SOUNDS_KEY] = gson.toJson(currentList)
        }
    }

    /**
     * Exposes a reactive stream of favorite sound IDs.
     */
    fun getFavoritesFlow(): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }
    }

    /**
     * Toggles a sound's favorite status.
     */
    suspend fun toggleFavorite(soundId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITES_KEY] ?: emptySet()
            if (current.contains(soundId)) {
                preferences[FAVORITES_KEY] = current - soundId
            } else {
                preferences[FAVORITES_KEY] = current + soundId
            }
        }
    }

    /**
     * Removes a user's custom sound from persistent storage.
     */
    suspend fun removeCustomSound(soundId: String) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[CUSTOM_SOUNDS_KEY] ?: "[]"
            val listType = object : TypeToken<List<PrankSound>>() {}.type
            val currentList: MutableList<PrankSound> = try {
                gson.fromJson(currentJson, listType) ?: mutableListOf()
            } catch (e: Exception) {
                mutableListOf()
            }
            
            currentList.removeAll { it.id == soundId }
            
            preferences[CUSTOM_SOUNDS_KEY] = gson.toJson(currentList)
        }
    }

    fun getSequencePresetsFlow(): Flow<List<SoundSequencePreset>> {
        return context.dataStore.data.map { preferences ->
            val jsonString = preferences[SEQUENCE_PRESETS_KEY] ?: "[]"
            val listType = object : TypeToken<List<SoundSequencePreset>>() {}.type
            try {
                gson.fromJson<List<SoundSequencePreset>>(jsonString, listType) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    suspend fun saveSequencePreset(preset: SoundSequencePreset) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[SEQUENCE_PRESETS_KEY] ?: "[]"
            val listType = object : TypeToken<List<SoundSequencePreset>>() {}.type
            val currentList: MutableList<SoundSequencePreset> = try {
                gson.fromJson(currentJson, listType) ?: mutableListOf()
            } catch (_: Exception) {
                mutableListOf()
            }
            val existingIndex = currentList.indexOfFirst { it.id == preset.id }
            if (existingIndex >= 0) {
                currentList[existingIndex] = preset
            } else {
                currentList.add(preset)
            }
            preferences[SEQUENCE_PRESETS_KEY] = gson.toJson(currentList)
        }
    }

    suspend fun deleteSequencePreset(presetId: String) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[SEQUENCE_PRESETS_KEY] ?: "[]"
            val listType = object : TypeToken<List<SoundSequencePreset>>() {}.type
            val currentList: MutableList<SoundSequencePreset> = try {
                gson.fromJson(currentJson, listType) ?: mutableListOf()
            } catch (_: Exception) {
                mutableListOf()
            }
            currentList.removeAll { it.id == presetId }
            preferences[SEQUENCE_PRESETS_KEY] = gson.toJson(currentList)
        }
    }
}

data class PackSummary(
    val packId: String,
    val soundCount: Int,
    val categoryFocus: String
)
