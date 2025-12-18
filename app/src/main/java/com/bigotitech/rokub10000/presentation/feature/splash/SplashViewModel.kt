package com.bigotitech.rokub10000.presentation.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigotitech.rokub10000.core.ads.AdManager
import com.bigotitech.rokub10000.presentation.feature.splash.state.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de splash.
 * Gestiona la inicialización de la aplicación y la transición a la siguiente pantalla.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val adManager: AdManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        initializeApp()
    }

    /**
     * Inicializa la aplicación cargando los recursos necesarios
     */
    private fun initializeApp() {
        viewModelScope.launch {
            try {
                // Inicializar AdMob
                adManager.initialize()

                // Marcar como listo
                _uiState.update { it.copy(isLoading = false, isReady = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
