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
}
