package com.bigotitech.rokub10000.domain.model

/**
 * Modelo de datos que representa un dado en el juego.
 *
 * @property id Identificador único del dado
 * @property value Valor actual del dado (1-6)
 * @property isSelected Indica si el dado está seleccionado por el jugador
 * @property isLocked Indica si el dado está bloqueado (no se puede volver a lanzar)
 */
data class Dice(
    val id: Int,
    val value: Int = 1,
    val isSelected: Boolean = false,
    val isLocked: Boolean = false
)