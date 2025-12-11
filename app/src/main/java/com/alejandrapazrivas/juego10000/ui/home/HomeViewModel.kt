package com.alejandrapazrivas.juego10000.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alejandrapazrivas.juego10000.data.preferences.UserPreferencesManager
import com.alejandrapazrivas.juego10000.domain.model.BotDifficulty
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.domain.repository.GameRepository
import com.alejandrapazrivas.juego10000.domain.repository.PlayerRepository
import com.alejandrapazrivas.juego10000.domain.repository.ScoreRepository
import com.alejandrapazrivas.juego10000.domain.usecase.CreateGameUseCase
import com.alejandrapazrivas.juego10000.domain.usecase.GetPlayersUseCase
import com.alejandrapazrivas.juego10000.ui.home.model.HomeUiState
import com.alejandrapazrivas.juego10000.ui.home.model.LastGameInfo
import com.alejandrapazrivas.juego10000.ui.home.model.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPlayersUseCase: GetPlayersUseCase,
    private val createGameUseCase: CreateGameUseCase,
    private val userPreferencesManager: UserPreferencesManager,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPlayers()
        loadUserStats()
        loadLastGame()
    }

    private fun loadPlayers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                getPlayersUseCase().collect { players ->
                    _uiState.update { it.copy(availablePlayers = players) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadUserStats() {
        viewModelScope.launch {
            try {
                val players = getPlayersUseCase().first()
                if (players.isNotEmpty()) {
                    // Agregar estadísticas de todos los jugadores humanos
                    val totalGamesPlayed = players.sumOf { it.gamesPlayed }
                    val totalWins = players.sumOf { it.gamesWon }

                    // Obtener el mejor turno de todos los jugadores
                    var bestTurnScore = 0
                    val recentScoresList = mutableListOf<Int>()

                    players.forEach { player ->
                        val playerBestTurn = playerRepository.getPlayerBestTurnScore(player.id)
                        if (playerBestTurn > bestTurnScore) {
                            bestTurnScore = playerBestTurn
                        }

                        // Obtener últimas puntuaciones para la gráfica
                        scoreRepository.getScoresByPlayerId(player.id).first().take(10).forEach { score ->
                            recentScoresList.add(score.turnScore)
                        }
                    }

                    val winRate = if (totalGamesPlayed > 0) {
                        (totalWins.toFloat() / totalGamesPlayed) * 100
                    } else 0f

                    _uiState.update {
                        it.copy(
                            userStats = UserStats(
                                totalGamesPlayed = totalGamesPlayed,
                                totalWins = totalWins,
                                bestTurnScore = bestTurnScore,
                                winRate = winRate
                            ),
                            recentScores = recentScoresList.take(10)
                        )
                    }
                }
            } catch (e: Exception) {
                // Silently fail, stats are optional
            }
        }
    }

    private fun loadLastGame() {
        viewModelScope.launch {
            try {
                gameRepository.getRecentCompletedGames().collect { games ->
                    val lastGame = games.firstOrNull()
                    if (lastGame != null) {
                        val winnerName = lastGame.winnerPlayerId?.let { winnerId ->
                            playerRepository.getPlayerById(winnerId)?.name
                        }

                        // Obtener puntuación del ganador
                        val playerScore = lastGame.winnerPlayerId?.let { winnerId ->
                            scoreRepository.getPlayerTotalScore(lastGame.id, winnerId)
                        } ?: 0

                        val players = getPlayersUseCase().first()
                        val isVictory = lastGame.winnerPlayerId?.let { winnerId ->
                            players.any { it.id == winnerId }
                        } ?: false

                        _uiState.update {
                            it.copy(
                                lastGame = LastGameInfo(
                                    game = lastGame,
                                    winnerName = winnerName,
                                    playerScore = playerScore,
                                    isVictory = isVictory
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Silently fail, last game info is optional
            }
        }
    }

    fun toggleDrawer() {
        _uiState.update { it.copy(isDrawerOpen = !it.isDrawerOpen) }
    }

    fun closeDrawer() {
        _uiState.update { it.copy(isDrawerOpen = false) }
    }

    fun onNewGameClick() {
        if (_uiState.value.availablePlayers.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "No hay jugadores disponibles. Crea algunos primero.") }
            return
        }

        _uiState.update { it.copy(showGameModeDialog = true) }
    }
    
    fun onGameModeDialogDismiss() {
        _uiState.update { it.copy(showGameModeDialog = false) }
    }
    
    fun onMultiplayerModeSelected() {
        _uiState.update { 
            it.copy(
                isSinglePlayerMode = false,
                botDifficulty = null,
                selectedPlayers = emptyList(),
                showGameModeDialog = false,
                showPlayerSelectionDialog = true
            )
        }
    }
    
    fun onSinglePlayerModeSelected(difficulty: BotDifficulty) {
        _uiState.update { 
            it.copy(
                isSinglePlayerMode = true,
                botDifficulty = difficulty,
                selectedPlayers = emptyList(),
                showGameModeDialog = false,
                showPlayerSelectionDialog = true
            )
        }
    }

    fun onPlayerSelected(player: Player) {
        val currentState = _uiState.value
        
        if (currentState.isSinglePlayerMode) {
            // En modo individual, solo permitir un jugador seleccionado
            _uiState.update { it.copy(selectedPlayers = listOf(player)) }
        } else {
            // En modo multijugador, permitir selección múltiple
            val currentSelected = currentState.selectedPlayers.toMutableList()
            
            if (currentSelected.contains(player)) {
                currentSelected.remove(player)
            } else {
                currentSelected.add(player)
            }
            
            _uiState.update { it.copy(selectedPlayers = currentSelected) }
        }
    }

    fun onPlayerSelectionDialogDismiss() {
        _uiState.update { it.copy(showPlayerSelectionDialog = false) }
    }

    fun createNewGame() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val currentState = _uiState.value
                
                // Validar número de jugadores según el modo de juego
                if (currentState.isSinglePlayerMode) {
                    if (currentState.selectedPlayers.isEmpty()) {
                        throw IllegalArgumentException("Selecciona un jugador para comenzar")
                    }
                } else {
                    // Modo multijugador: mínimo 2, máximo 6 jugadores
                    when {
                        currentState.selectedPlayers.isEmpty() -> 
                            throw IllegalArgumentException("Selecciona jugadores para comenzar")
                        currentState.selectedPlayers.size < 2 -> 
                            throw IllegalArgumentException("Selecciona al menos 2 jugadores para modo multijugador")
                        currentState.selectedPlayers.size > 6 -> 
                            throw IllegalArgumentException("Máximo 6 jugadores permitidos")
                    }
                }

                val playerIds = currentState.selectedPlayers.map { it.id }
                val gameMode = if (currentState.isSinglePlayerMode) "SINGLE_PLAYER" else "MULTIPLAYER"
                
                // Crear el juego con el modo adecuado
                val result = if (currentState.isSinglePlayerMode) {
                    // En modo individual, añadir un jugador "Bot" a la lista
                    createGameUseCase(
                        playerIds = playerIds,
                        targetScore = 10000,
                        gameMode = gameMode,
                        includeBotPlayer = true,
                        botName = "Bot"
                    )
                } else {
                    // Modo multijugador normal
                    createGameUseCase(
                        playerIds = playerIds,
                        targetScore = 10000,
                        gameMode = gameMode
                    )
                }

                result.fold(
                    onSuccess = { gameId ->
                        // Guardar el ID del juego en preferencias
                        userPreferencesManager.setLastActiveGame(gameId)
                        
                        // Guardar información del bot si es modo individual
                        if (currentState.isSinglePlayerMode && currentState.botDifficulty != null) {
                            userPreferencesManager.setBotDifficulty(currentState.botDifficulty.toString())
                        }
                        
                        _uiState.update { it.copy(currentGameId = gameId) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(errorMessage = error.message) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
