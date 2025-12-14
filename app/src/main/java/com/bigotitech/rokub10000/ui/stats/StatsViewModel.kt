package com.bigotitech.rokub10000.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigotitech.rokub10000.data.preferences.UserPreferencesManager
import com.bigotitech.rokub10000.domain.model.Game
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.domain.repository.ScoreRepository
import com.bigotitech.rokub10000.domain.usecase.GetGameHistoryUseCase
import com.bigotitech.rokub10000.domain.usecase.GetPlayersUseCase
import com.bigotitech.rokub10000.ui.stats.model.PlayerStats
import com.bigotitech.rokub10000.ui.stats.model.ScoreWithPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de estadísticas
 * Maneja la carga y gestión de datos para las tres pestañas: Jugadores, Historial y Récords
 */
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val getPlayersUseCase: GetPlayersUseCase,
    private val getGameHistoryUseCase: GetGameHistoryUseCase,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    // Estadísticas de jugadores
    private val _playerStats = MutableStateFlow<List<PlayerStats>>(emptyList())
    val playerStats: StateFlow<List<PlayerStats>> = _playerStats

    // Historial de partidas
    private val _gameHistory = MutableStateFlow<List<Pair<Game, Player?>>>(emptyList())
    val gameHistory: StateFlow<List<Pair<Game, Player?>>> = _gameHistory

    // Mejores puntuaciones
    private val _topScores = MutableStateFlow<List<ScoreWithPlayer>>(emptyList())
    val topScores: StateFlow<List<ScoreWithPlayer>> = _topScores

    init {
        loadPlayerStats()
        loadGameHistory()
        loadTopScores()
    }

    /**
     * Carga las estadísticas de todos los jugadores
     */
    private fun loadPlayerStats() {
        viewModelScope.launch {
            getPlayersUseCase().collect { players ->
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
     * Carga el historial de partidas del usuario seleccionado
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadGameHistory() {
        viewModelScope.launch {
            val playersFlow = getPlayersUseCase()
            val currentUserIdFlow = userPreferencesManager.selectedUserId

            combine(currentUserIdFlow, playersFlow) { currentUserId, players ->
                Pair(currentUserId, players)
            }.flatMapLatest { (currentUserId, players) ->
                if (currentUserId > 0) {
                    getGameHistoryUseCase.getPlayerGameHistory(currentUserId).combine(flowOf(players)) { games, playerList ->
                        games.map { game ->
                            val winner = game.winnerPlayerId?.let { winnerId ->
                                playerList.find { it.id == winnerId }
                            }
                            Pair(game, winner)
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
     * Carga las mejores puntuaciones de todos los jugadores
     */
    private fun loadTopScores() {
        viewModelScope.launch {
            val players = getPlayersUseCase().first()
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
