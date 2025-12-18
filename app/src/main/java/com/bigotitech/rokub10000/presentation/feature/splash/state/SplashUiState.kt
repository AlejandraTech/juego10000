package com.bigotitech.rokub10000.presentation.feature.splash.state

/**
 * Estado UI para la pantalla de splash.
 *
 * @property isLoading Indica si se está cargando la configuración inicial
 * @property isReady Indica si la aplicación está lista para continuar
 * @property errorMessage Mensaje de error si algo falla durante la inicialización
 */
data class SplashUiState(
    val isLoading: Boolean = true,
    val isReady: Boolean = false,
    val errorMessage: String? = null
)
