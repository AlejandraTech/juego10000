package com.bigotitech.rokub10000.presentation.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigotitech.rokub10000.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de configuraciones.
 * Gestiona las preferencias del usuario como sonido, vibración, música y modo oscuro.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val flowStopTimeout = 5000L

    val soundEnabled: StateFlow<Boolean> = userPreferencesManager.soundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(flowStopTimeout), true)

    val vibrationEnabled: StateFlow<Boolean> = userPreferencesManager.vibrationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(flowStopTimeout), true)

    val musicEnabled: StateFlow<Boolean> = userPreferencesManager.musicEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(flowStopTimeout), true)

    val musicVolume: StateFlow<Float> = userPreferencesManager.musicVolume
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(flowStopTimeout), 0.5f)

    val darkMode: StateFlow<Boolean> = userPreferencesManager.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(flowStopTimeout), false)

    val language: StateFlow<String> = userPreferencesManager.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(flowStopTimeout), UserPreferencesManager.LANGUAGE_SYSTEM)

    fun setSoundEnabled(enabled: Boolean) {
        updatePreference { userPreferencesManager.setSoundEnabled(enabled) }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        updatePreference { userPreferencesManager.setVibrationEnabled(enabled) }
    }

    fun setMusicEnabled(enabled: Boolean) {
        updatePreference { userPreferencesManager.setMusicEnabled(enabled) }
    }

    fun setMusicVolume(volume: Float) {
        updatePreference { userPreferencesManager.setMusicVolume(volume) }
    }

    fun setDarkMode(enabled: Boolean) {
        updatePreference { userPreferencesManager.setDarkMode(enabled) }
    }

    fun setLanguage(language: String) {
        updatePreference { userPreferencesManager.setLanguage(language) }
    }

    private fun updatePreference(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }
}
