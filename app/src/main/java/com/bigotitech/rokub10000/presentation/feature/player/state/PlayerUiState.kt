package com.bigotitech.rokub10000.presentation.feature.player.state

import com.bigotitech.rokub10000.domain.model.Player

/**
 * Estado de la UI para la pantalla de gestión de jugadores.
 */
data class PlayerUiState(
    val dialogState: PlayerDialogState = PlayerDialogState.None,
    val playerName: String = ""
)

/**
 * Estados posibles del diálogo de jugadores.
 */
sealed class PlayerDialogState {
    data object None : PlayerDialogState()
    data object Add : PlayerDialogState()
    data class Edit(val player: Player) : PlayerDialogState()
    data class Delete(val player: Player) : PlayerDialogState()
}

/**
 * Modelo UI que combina Player con la mejor puntuación en un turno.
 * Se usa para mostrar estadísticas adicionales en la tarjeta del jugador.
 */
data class PlayerWithBestTurn(
    val player: Player,
    val bestTurnScore: Int = 0
)
