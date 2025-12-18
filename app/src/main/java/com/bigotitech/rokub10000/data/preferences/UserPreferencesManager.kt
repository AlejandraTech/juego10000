package com.bigotitech.rokub10000.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Gestor de preferencias del usuario que utiliza DataStore para almacenar y recuperar
 * configuraciones de la aplicación de manera persistente.
 */
@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    /**
     * Claves de preferencias utilizadas en la aplicación
     */
    private object PreferencesKeys {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val MUSIC_VOLUME = floatPreferencesKey("music_volume")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LAST_ACTIVE_GAME = longPreferencesKey("last_active_game")
        val BOT_DIFFICULTY = stringPreferencesKey("bot_difficulty")
        val SELECTED_USER_ID = longPreferencesKey("selected_user_id")
        val LANGUAGE = stringPreferencesKey("language")
    }

    companion object {
        const val LANGUAGE_SYSTEM = "system"
        const val LANGUAGE_SPANISH = "es"
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_CATALAN = "ca"
        const val LANGUAGE_FRENCH = "fr"
    }

    /**
     * Método genérico para obtener un valor booleano de las preferencias
     */
    private fun getBooleanPreference(key: Preferences.Key<Boolean>, defaultValue: Boolean): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    /**
     * Método genérico para obtener un valor Long de las preferencias
     */
    private fun getLongPreference(key: Preferences.Key<Long>, defaultValue: Long): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    /**
     * Método genérico para obtener un valor String de las preferencias
     */
    private fun getStringPreference(key: Preferences.Key<String>): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    /**
     * Método genérico para obtener un valor Float de las preferencias
     */
    private fun getFloatPreference(key: Preferences.Key<Float>, defaultValue: Float): Flow<Float> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    /**
     * Método genérico para establecer un valor en las preferencias
     */
    private suspend fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    // Propiedades públicas para acceder a las preferencias
    val soundEnabled: Flow<Boolean> = getBooleanPreference(PreferencesKeys.SOUND_ENABLED, true)
    val vibrationEnabled: Flow<Boolean> = getBooleanPreference(PreferencesKeys.VIBRATION_ENABLED, true)
    val musicEnabled: Flow<Boolean> = getBooleanPreference(PreferencesKeys.MUSIC_ENABLED, true)
    val musicVolume: Flow<Float> = getFloatPreference(PreferencesKeys.MUSIC_VOLUME, 0.5f)
    val darkMode: Flow<Boolean> = getBooleanPreference(PreferencesKeys.DARK_MODE, false)
    val lastActiveGame: Flow<Long> = getLongPreference(PreferencesKeys.LAST_ACTIVE_GAME, 0L)
    val botDifficulty: Flow<String?> = getStringPreference(PreferencesKeys.BOT_DIFFICULTY)
    val selectedUserId: Flow<Long> = getLongPreference(PreferencesKeys.SELECTED_USER_ID, 0L)
    val language: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LANGUAGE] ?: LANGUAGE_SYSTEM
    }

    // Métodos públicos para establecer preferencias
    suspend fun setSoundEnabled(enabled: Boolean) =
        setPreference(PreferencesKeys.SOUND_ENABLED, enabled)

    suspend fun setVibrationEnabled(enabled: Boolean) =
        setPreference(PreferencesKeys.VIBRATION_ENABLED, enabled)

    suspend fun setMusicEnabled(enabled: Boolean) =
        setPreference(PreferencesKeys.MUSIC_ENABLED, enabled)

    suspend fun setMusicVolume(volume: Float) =
        setPreference(PreferencesKeys.MUSIC_VOLUME, volume.coerceIn(0f, 1f))

    suspend fun setDarkMode(enabled: Boolean) =
        setPreference(PreferencesKeys.DARK_MODE, enabled)

    suspend fun setLastActiveGame(gameId: Long) =
        setPreference(PreferencesKeys.LAST_ACTIVE_GAME, gameId)

    suspend fun setBotDifficulty(difficulty: String) =
        setPreference(PreferencesKeys.BOT_DIFFICULTY, difficulty)

    suspend fun setSelectedUserId(userId: Long) =
        setPreference(PreferencesKeys.SELECTED_USER_ID, userId)

    suspend fun clearSelectedUser() =
        setPreference(PreferencesKeys.SELECTED_USER_ID, 0L)

    suspend fun setLanguage(language: String) =
        setPreference(PreferencesKeys.LANGUAGE, language)
}
