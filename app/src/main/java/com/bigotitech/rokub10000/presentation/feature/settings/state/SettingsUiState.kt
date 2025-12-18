package com.bigotitech.rokub10000.presentation.feature.settings.state

/**
 * Modelo de datos para un elemento de configuración con switch.
 *
 * @property title Título del ajuste
 * @property description Descripción del ajuste
 * @property checked Estado actual del switch
 * @property onCheckedChange Callback cuando cambia el estado
 */
data class SettingItem(
    val title: String,
    val description: String,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)

/**
 * Modelo para los enlaces de atribución de sonidos.
 *
 * @property textResId ID del recurso de texto
 * @property url URL del enlace
 */
data class SoundAttribution(
    val textResId: Int,
    val url: String
)
