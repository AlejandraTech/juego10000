package com.bigotitech.rokub10000.presentation.feature.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.data.preferences.UserPreferencesManager
import com.bigotitech.rokub10000.domain.model.BotDifficulty
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.domain.repository.GameRepository
import com.bigotitech.rokub10000.domain.repository.PlayerRepository
import com.bigotitech.rokub10000.domain.repository.ScoreRepository
import com.bigotitech.rokub10000.domain.usecase.game.CreateGameUseCase
import com.bigotitech.rokub10000.domain.usecase.player.GetPlayersUseCase
import com.bigotitech.rokub10000.presentation.feature.home.state.HomeUiState
import com.bigotitech.rokub10000.presentation.feature.home.state.LastGameInfo
import com.bigotitech.rokub10000.presentation.feature.home.state.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de inicio.
 * Gestiona la lógica de creación de partidas, estadísticas del usuario y navegación.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application,
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
        loadCurrentUser()
        loadPlayers()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                userPreferencesManager.selectedUserId
                    .distinctUntilChanged()
                    .flatMapLatest { userId ->
                        if (userId > 0) {
                            playerRepository.getPlayerByIdFlow(userId)
                        } else {
                            kotlinx.coroutines.flow.flowOf(null)
                        }
                    }
                    .collectLatest { player ->
                        if (player != null) {
                            _uiState.update { it.copy(currentUser = player) }
                            loadUserStats(player)
                            loadLastGame(player)
                        } else {
                            _uiState.update { it.copy(currentUser = null) }
                        }
                    }
            } catch (e: Exception) {
                // Silently fail
            }
        }
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

    private fun loadUserStats(player: Player) {
        viewModelScope.launch {
            try {
                val totalGamesPlayed = player.gamesPlayed
                val totalWins = player.gamesWon
                val bestTurnScore = playerRepository.getPlayerBestTurnScore(player.id)

                val recentScoresList = mutableListOf<Int>()
                scoreRepository.getScoresByPlayerId(player.id).first().take(10).forEach { score ->
                    recentScoresList.add(score.turnScore)
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
            } catch (e: Exception) {
                // Silently fail, stats are optional
            }
        }
    }

    private fun loadLastGame(currentPlayer: Player) {
        viewModelScope.launch {
            try {
                gameRepository.getRecentCompletedGames().collect { games ->
                    val lastGame = games.firstOrNull()
                    if (lastGame != null) {
                        val winnerName = lastGame.winnerPlayerId?.let { winnerId ->
                            playerRepository.getPlayerById(winnerId)?.name
                        }

                        val playerScore = scoreRepository.getPlayerTotalScore(lastGame.id, currentPlayer.id)
                        val isVictory = lastGame.winnerPlayerId == currentPlayer.id

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
        val currentUser = _uiState.value.currentUser
        if (currentUser == null) {
            _uiState.update { it.copy(errorMessage = application.getString(R.string.no_user_selected_error)) }
            return
        }

        _uiState.update { it.copy(showGameModeDialog = true) }
    }

    fun onGameModeDialogDismiss() {
        _uiState.update { it.copy(showGameModeDialog = false) }
    }

    fun onMultiplayerModeSelected() {
        val currentUser = _uiState.value.currentUser
        val otherPlayers = _uiState.value.availablePlayers.filter { it.id != currentUser?.id }

        if (otherPlayers.isEmpty()) {
            _uiState.update {
                it.copy(
                    showGameModeDialog = false,
                    errorMessage = application.getString(R.string.no_other_players_error)
                )
            }
            return
        }

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
                showGameModeDialog = false
            )
        }
        createNewGame()
    }

    fun onPlayerSelected(player: Player) {
        val currentSelected = _uiState.value.selectedPlayers.toMutableList()

        if (currentSelected.contains(player)) {
            currentSelected.remove(player)
        } else {
            if (currentSelected.size < 5) {
                currentSelected.add(player)
            }
        }

        _uiState.update { it.copy(selectedPlayers = currentSelected) }
    }

    fun onPlayerSelectionDialogDismiss() {
        _uiState.update { it.copy(showPlayerSelectionDialog = false) }
    }

    fun createNewGame() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val currentState = _uiState.value
                val currentUser = currentState.currentUser
                    ?: throw IllegalArgumentException(application.getString(R.string.no_user_selected_error))

                val playerIds: List<Long>
                val gameMode: String

                if (currentState.isSinglePlayerMode) {
                    playerIds = listOf(currentUser.id)
                    gameMode = CreateGameUseCase.MODE_SINGLE_PLAYER
                } else {
                    if (currentState.selectedPlayers.isEmpty()) {
                        throw IllegalArgumentException(application.getString(R.string.select_at_least_one_opponent))
                    }
                    if (currentState.selectedPlayers.size > 5) {
                        throw IllegalArgumentException(application.getString(R.string.max_opponents_allowed))
                    }
                    playerIds = listOf(currentUser.id) + currentState.selectedPlayers.map { it.id }
                    gameMode = CreateGameUseCase.MODE_MULTIPLAYER
                }

                val result = if (currentState.isSinglePlayerMode) {
                    createGameUseCase(
                        playerIds = playerIds,
                        targetScore = CreateGameUseCase.DEFAULT_TARGET_SCORE,
                        gameMode = gameMode,
                        includeBotPlayer = true,
                        botName = application.getString(R.string.bot_name)
                    )
                } else {
                    createGameUseCase(
                        playerIds = playerIds,
                        targetScore = CreateGameUseCase.DEFAULT_TARGET_SCORE,
                        gameMode = gameMode
                    )
                }

                result.fold(
                    onSuccess = { gameId ->
                        userPreferencesManager.setLastActiveGame(gameId)

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

    fun onNavigationHandled() {
        _uiState.update { it.copy(currentGameId = 0L) }
    }
}
