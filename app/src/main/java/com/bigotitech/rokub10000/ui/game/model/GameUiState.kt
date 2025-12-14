package com.bigotitech.rokub10000.ui.game.model

import com.bigotitech.rokub10000.domain.model.BotDifficulty
import com.bigotitech.rokub10000.domain.model.Dice
import com.bigotitech.rokub10000.domain.model.Player

/**
 * Estado de la UI para la pantalla de juego
 * 
 * Contiene toda la información necesaria para representar el estado actual del juego
 * en la interfaz de usuario.
 */
data class GameUiState(
    // Estado de los dados
    val dice: List<Dice> = emptyList(),
    val selectedDice: List<Dice> = emptyList(),
    val lockedDice: List<Dice> = emptyList(),
    val allDiceScored: Boolean = false,
    val selectedDiceChanged: Boolean = false,
    
    // Estado de la puntuación
    val currentTurnScore: Int = 0,
    val playerScores: Map<Long, Int> = emptyMap(),
    val scoreExceeded: Boolean = false,
    
    // Estado de los controles
    val canRoll: Boolean = true,
    val canBank: Boolean = false,
    val isRolling: Boolean = false,
    
    // Estado del juego
    val message: String? = null,
    val currentRound: Int = 1,
    val currentPlayer: Player? = null,
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val isGameOver: Boolean = false,
    val winner: Player? = null,
    val gameStarted: Boolean = false,
    val playersInGame: Map<Long, Boolean> = emptyMap(),
    
    // Configuración del juego
    val isSinglePlayerMode: Boolean = false,
    val botDifficulty: BotDifficulty? = null
)
