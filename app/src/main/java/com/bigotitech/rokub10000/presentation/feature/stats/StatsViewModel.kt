package com.bigotitech.rokub10000.presentation.feature.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigotitech.rokub10000.data.preferences.UserPreferencesManager
import com.bigotitech.rokub10000.domain.repository.PlayerRepository
import com.bigotitech.rokub10000.domain.repository.ScoreRepository
import com.bigotitech.rokub10000.domain.usecase.history.GetGameHistoryUseCase
import com.bigotitech.rokub10000.presentation.feature.stats.state.GameWithWinner
import com.bigotitech.rokub10000.presentation.feature.stats.state.PlayerStats
import com.bigotitech.rokub10000.presentation.feature.stats.state.ScoreWithPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de estadísticas.
 * Gestiona la carga y presentación de datos para las tres pestañas:
 * - Jugadores: Estadísticas de cada jugador
 * - Historial: Partidas completadas
 * - Récords: Mejores puntuaciones
 */
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val scoreRepository: ScoreRepository,
    private val getGameHistoryUseCase: GetGameHistoryUseCase,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _playerStats = MutableStateFlow<List<PlayerStats>>(emptyList())
    val playerStats: StateFlow<List<PlayerStats>> = _playerStats

    private val _gameHistory = MutableStateFlow<List<GameWithWinner>>(emptyList())
    val gameHistory: StateFlow<List<GameWithWinner>> = _gameHistory

    private val _botGameHistory = MutableStateFlow<List<GameWithWinner>>(emptyList())
    val botGameHistory: StateFlow<List<GameWithWinner>> = _botGameHistory

    private val _multiplayerGameHistory = MutableStateFlow<List<GameWithWinner>>(emptyList())
    val multiplayerGameHistory: StateFlow<List<GameWithWinner>> = _multiplayerGameHistory

    private val _topScores = MutableStateFlow<List<ScoreWithPlayer>>(emptyList())
    val topScores: StateFlow<List<ScoreWithPlayer>> = _topScores

    init {
        loadPlayerStats()
        loadGameHistory()
        loadBotGameHistory()
        loadMultiplayerGameHistory()
        loadTopScores()
    }

    /**
     * Carga las estadísticas de todos los jugadores activos.
     */
    private fun loadPlayerStats() {
        viewModelScope.launch {
            playerRepository.getAllActivePlayers().collect { players ->
                val stats = players.map { player ->
                    PlayerStats(
                        player = player,
                        bestScore = scoreRepository.getPlayerHighestScore(player.id),
                        averageScore = scoreRepository.getPlayerAverageScore(player.id)
                    )
                }
                _playerStats.value = stats
            }
        }
    }

    /**
     * Carga el historial de partidas del usuario seleccionado.
     * Utiliza GetGameHistoryUseCase para obtener el historial filtrado.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadGameHistory() {
        viewModelScope.launch {
            val playersFlow = playerRepository.getAllActivePlayers()
            val currentUserIdFlow = userPreferencesManager.selectedUserId

            combine(currentUserIdFlow, playersFlow) { currentUserId, players ->
                Pair(currentUserId, players)
            }.flatMapLatest { (currentUserId, players) ->
                if (currentUserId > 0) {
                    getGameHistoryUseCase.getPlayerGameHistory(currentUserId).map { games ->
                        games.map { game ->
                            val winner = game.winnerPlayerId?.let { winnerId ->
                                players.find { it.id == winnerId }
                            }
                            GameWithWinner(game = game, winner = winner)
                        }
                    }
                } else {
                    flowOf(emptyList())
                }
            }.collect { history ->
                _gameHistory.value = history
            }
        }
    }

    /**
     * Carga el historial de partidas contra el bot del usuario seleccionado.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadBotGameHistory() {
        viewModelScope.launch {
            val playersFlow = playerRepository.getAllActivePlayers()
            val currentUserIdFlow = userPreferencesManager.selectedUserId

            combine(currentUserIdFlow, playersFlow) { currentUserId, players ->
                Pair(currentUserId, players)
            }.flatMapLatest { (currentUserId, players) ->
                if (currentUserId > 0) {
                    getGameHistoryUseCase.getPlayerBotGameHistory(currentUserId).map { games ->
                        games.map { game ->
                            val winner = game.winnerPlayerId?.let { winnerId ->
                                players.find { it.id == winnerId }
                            }
                            GameWithWinner(game = game, winner = winner)
                        }
                    }
                } else {
                    flowOf(emptyList())
                }
            }.collect { history ->
                _botGameHistory.value = history
            }
        }
    }

    /**
     * Carga el historial de partidas multijugador del usuario seleccionado.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadMultiplayerGameHistory() {
        viewModelScope.launch {
            val playersFlow = playerRepository.getAllActivePlayers()
            val currentUserIdFlow = userPreferencesManager.selectedUserId

            combine(currentUserIdFlow, playersFlow) { currentUserId, players ->
                Pair(currentUserId, players)
            }.flatMapLatest { (currentUserId, players) ->
                if (currentUserId > 0) {
                    getGameHistoryUseCase.getPlayerMultiplayerGameHistory(currentUserId).map { games ->
                        games.map { game ->
                            val winner = game.winnerPlayerId?.let { winnerId ->
                                players.find { it.id == winnerId }
                            }
                            GameWithWinner(game = game, winner = winner)
                        }
                    }
                } else {
                    flowOf(emptyList())
                }
            }.collect { history ->
                _multiplayerGameHistory.value = history
            }
        }
    }

    /**
     * Carga las mejores puntuaciones de todos los jugadores.
     */
    private fun loadTopScores() {
        viewModelScope.launch {
            val players = playerRepository.getAllActivePlayers().first()
            val topScoresList = mutableListOf<ScoreWithPlayer>()

            players.forEach { player ->
                scoreRepository.getPlayerBestTurn(player.id)?.let { bestTurn ->
                    topScoresList.add(ScoreWithPlayer(bestTurn, player))
                }
            }

            _topScores.value = topScoresList.sortedByDescending { it.score.turnScore }
        }
    }
}
