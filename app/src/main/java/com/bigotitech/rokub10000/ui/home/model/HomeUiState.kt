package com.bigotitech.rokub10000.ui.home.model

import com.bigotitech.rokub10000.domain.model.BotDifficulty
import com.bigotitech.rokub10000.domain.model.Game
import com.bigotitech.rokub10000.domain.model.Player

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
    val currentGameId: Long = 0L,
    // Usuario actual seleccionado
    val currentUser: Player? = null,
    // Estadísticas para la home (del usuario actual)
    val userStats: UserStats = UserStats(),
    val recentScores: List<Int> = emptyList(),
    val lastGame: LastGameInfo? = null,
    val isDrawerOpen: Boolean = false
)

/**
 * Estadísticas del usuario para mostrar en la home
 */
data class UserStats(
    val totalGamesPlayed: Int = 0,
    val totalWins: Int = 0,
    val bestTurnScore: Int = 0,
    val winRate: Float = 0f
)

/**
 * Información de la última partida
 */
data class LastGameInfo(
    val game: Game,
    val winnerName: String?,
    val playerScore: Int,
    val isVictory: Boolean
)
