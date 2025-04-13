package com.alejandrapazrivas.juego10000.ui.home.model

import com.alejandrapazrivas.juego10000.domain.model.BotDifficulty
import com.alejandrapazrivas.juego10000.domain.model.Player

/**
 * Estado unificado para la pantalla de inicio
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showGameModeDialog: Boolean = false,
    val showPlayerSelectionDialog: Boolean = false,
    val isSinglePlayerMode: Boolean = false,
    val botDifficulty: BotDifficulty? = null,
    val availablePlayers: List<Player> = emptyList(),
    val selectedPlayers: List<Player> = emptyList(),
    val currentGameId: Long = 0L
)
