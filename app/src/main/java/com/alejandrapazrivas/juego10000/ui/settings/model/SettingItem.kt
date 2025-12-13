package com.alejandrapazrivas.juego10000.ui.settings.model

/**
 * Modelo de datos para un elemento de configuración con switch
 */
data class SettingItem(
    val title: String,
    val description: String,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)
