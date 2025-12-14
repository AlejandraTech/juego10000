package com.bigotitech.rokub10000.ui.settings

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
 * ViewModel para la pantalla de configuraciones
 * Gestiona las preferencias del usuario como sonido, vibración y modo oscuro
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    // Configuración de tiempo de expiración común para todos los flujos
    private val flowStopTimeout = 5000L

    // Flujos de estado para las preferencias del usuario
    val soundEnabled: StateFlow<Boolean> = userPreferencesManager.soundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(flowStopTimeout), true)

    val vibrationEnabled: StateFlow<Boolean> = userPreferencesManager.vibrationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(flowStopTimeout), true)

    val darkMode: StateFlow<Boolean> = userPreferencesManager.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(flowStopTimeout), false)

    val language: StateFlow<String> = userPreferencesManager.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(flowStopTimeout), UserPreferencesManager.LANGUAGE_SYSTEM)

    /**
     * Actualiza la preferencia de sonido
     */
    fun setSoundEnabled(enabled: Boolean) {
        updatePreference { userPreferencesManager.setSoundEnabled(enabled) }
    }

    /**
     * Actualiza la preferencia de vibración
     */
    fun setVibrationEnabled(enabled: Boolean) {
        updatePreference { userPreferencesManager.setVibrationEnabled(enabled) }
    }

    /**
     * Actualiza la preferencia de modo oscuro
     */
    fun setDarkMode(enabled: Boolean) {
        updatePreference { userPreferencesManager.setDarkMode(enabled) }
    }

    /**
     * Actualiza la preferencia de idioma
     */
    fun setLanguage(language: String) {
        updatePreference { userPreferencesManager.setLanguage(language) }
    }

    /**
     * Método auxiliar para actualizar preferencias en una corrutina
     */
    private fun updatePreference(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }
}
