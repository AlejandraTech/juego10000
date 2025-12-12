package com.alejandrapazrivas.juego10000.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alejandrapazrivas.juego10000.domain.model.Game
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.domain.model.Score
import com.alejandrapazrivas.juego10000.domain.repository.ScoreRepository
import com.alejandrapazrivas.juego10000.domain.usecase.GetGameHistoryUseCase
import com.alejandrapazrivas.juego10000.domain.usecase.GetPlayersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    private val getGameHistoryUseCase: GetGameHistoryUseCase
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
     * Carga el historial de todas las partidas
     */
    private fun loadGameHistory() {
        viewModelScope.launch {
            getGameHistoryUseCase().collect { history ->
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

            // Ordenar por puntuación más alta
            _topScores.value = topScoresList.sortedByDescending { it.score.turnScore }
        }
    }

    // Clases de datos para la UI
    data class PlayerStats(
        val player: Player,
        val bestScore: Int,
        val averageScore: Float
    )

    data class ScoreWithPlayer(
        val score: Score,
        val player: Player
    )
}
