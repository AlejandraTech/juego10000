package com.bigotitech.juego10000.ui.player.model

import com.bigotitech.juego10000.domain.model.Player

/**
 * Modelo UI que combina Player con la mejor puntuación en un turno.
 * Se usa para mostrar estadísticas adicionales en la tarjeta del jugador.
 */
data class PlayerWithBestTurn(
    val player: Player,
    val bestTurnScore: Int = 0
)
