package com.alejandrapazrivas.juego10000.ui.game

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.data.preferences.UserPreferencesManager
import com.alejandrapazrivas.juego10000.domain.model.Bot
import com.alejandrapazrivas.juego10000.domain.model.Dice
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.domain.usecase.BotTurnHandler
import com.alejandrapazrivas.juego10000.domain.usecase.CalculateScoreUseCase
import com.alejandrapazrivas.juego10000.domain.usecase.GameStateUseCase
import com.alejandrapazrivas.juego10000.domain.usecase.RollDiceUseCase
import com.alejandrapazrivas.juego10000.domain.usecase.SaveGameUseCase
import com.alejandrapazrivas.juego10000.domain.usecase.ValidateTurnUseCase
import com.alejandrapazrivas.juego10000.domain.usecase.GetPlayersUseCase
import com.alejandrapazrivas.juego10000.domain.model.BotDifficulty
import com.alejandrapazrivas.juego10000.ui.game.model.GameUiState
import com.alejandrapazrivas.juego10000.ads.AdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameStateUseCase: GameStateUseCase,
    private val calculateScoreUseCase: CalculateScoreUseCase,
    private val rollDiceUseCase: RollDiceUseCase,
    private val validateTurnUseCase: ValidateTurnUseCase,
    private val saveGameUseCase: SaveGameUseCase,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val getPlayersUseCase: GetPlayersUseCase,
    private val userPreferencesManager: UserPreferencesManager,
    private val botTurnHandler: BotTurnHandler,
    val adManager: AdManager
) : ViewModel() {

    private val gameId: Long = savedStateHandle.get<Long>("gameId") ?: 0L

    // Estados del juego
    private val _gameState = MutableStateFlow(GameUiState())
    val gameState: StateFlow<GameUiState> = _gameState.asStateFlow()
    
    // Estados del modo de juego y bot
    private val _isSinglePlayerMode = MutableStateFlow(false)
    private val _bot = MutableStateFlow<Bot?>(null)
    private var botPlayerId: Long = 0L
    private val _isBotTurn = MutableStateFlow(false)
    val isBotTurn: StateFlow<Boolean> = _isBotTurn.asStateFlow()
    private val _botActionInProgress = MutableStateFlow(false)
    val botActionInProgress: StateFlow<Boolean> = _botActionInProgress.asStateFlow()

    // Estados de los dados y puntuación
    private val _dice = MutableStateFlow<List<Dice>>(emptyList())
    private val _selectedDice = MutableStateFlow<List<Dice>>(emptyList())
    private val _lockedDice = MutableStateFlow<List<Dice>>(emptyList())
    private val _currentTurnScore = MutableStateFlow(0)
    private val _canRoll = MutableStateFlow(true)
    private val _canBank = MutableStateFlow(false)
    private val _isRolling = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)
    private val _gameStarted = MutableStateFlow(false)
    private val _playerScores = MutableStateFlow<Map<Long, Int>>(emptyMap())
    private val _projectedScores = MutableStateFlow<Map<Long, Int>>(emptyMap())
    private val _allDiceScored = MutableStateFlow(false)
    private val _playersInGame = MutableStateFlow<Map<Long, Boolean>>(emptyMap())

    // Efectos de sonido
    private var diceRollSound: MediaPlayer? = null
    private var winSound: MediaPlayer? = null

    init {
        viewModelScope.launch {
            loadGame()
            gameStateUseCase.getGameScores(gameId).collect { scores ->
                _playerScores.value = scores
                _gameState.value = _gameState.value.copy(playerScores = scores)
            }
        }

        viewModelScope.launch {
            val game = gameStateUseCase.getGameState(gameId).first()
            if (game != null && game.game.gameMode == "SINGLE_PLAYER") {
                _isSinglePlayerMode.value = true

                val botDifficultyStr = userPreferencesManager.botDifficulty.first()
                val botDifficulty = when (botDifficultyStr) {
                    "BEGINNER" -> BotDifficulty.BEGINNER
                    "INTERMEDIATE" -> BotDifficulty.INTERMEDIATE
                    "EXPERT" -> BotDifficulty.EXPERT
                    else -> BotDifficulty.INTERMEDIATE
                }

                val botPlayer = game.players.find { it.name == "Bot" }
                if (botPlayer != null) {
                    botPlayerId = botPlayer.id
                    _bot.value = Bot(difficulty = botDifficulty, name = botPlayer.name)
                    Log.d("GameViewModel", "Bot configurado con ID: $botPlayerId")
                }

                _gameState.update { currentState ->
                    currentState.copy(isSinglePlayerMode = true, botDifficulty = botDifficulty)
                }
            }
        }

        viewModelScope.launch {
            combine(
                _dice,
                _selectedDice,
                _lockedDice,
                _currentTurnScore,
                _canRoll,
                _canBank,
                _isRolling,
                _message,
                _gameStarted,
                _playerScores,
                _allDiceScored,
                _playersInGame
            ) { values ->
                @Suppress("UNCHECKED_CAST")
                val dice = (values[0] as? List<Dice>) ?: emptyList()
                @Suppress("UNCHECKED_CAST")
                val selected = (values[1] as? List<Dice>) ?: emptyList()
                @Suppress("UNCHECKED_CAST")
                val locked = (values[2] as? List<Dice>) ?: emptyList()
                val turnScore = values[3] as? Int ?: 0
                val canRoll = values[4] as? Boolean ?: false
                val canBank = values[5] as? Boolean ?: false
                val isRolling = values[6] as? Boolean ?: false
                val message = values[7] as? String
                val gameStarted = values[8] as? Boolean ?: false
                @Suppress("UNCHECKED_CAST")
                val playerScores = (values[9] as? Map<Long, Int>) ?: emptyMap()
                val allDiceScored = values[10] as? Boolean ?: false
                @Suppress("UNCHECKED_CAST")
                val playersInGame = (values[11] as? Map<Long, Boolean>) ?: emptyMap()

        GameUiState(
            dice = dice,
            selectedDice = selected,
            lockedDice = locked,
            currentTurnScore = turnScore,
            canRoll = canRoll && !isRolling,
            canBank = canBank && !isRolling,
            isRolling = isRolling,
            message = message,
            currentRound = _gameState.value.currentRound,
            currentPlayer = _gameState.value.currentPlayer,
            players = _gameState.value.players,
            playerScores = playerScores,
            currentPlayerIndex = _gameState.value.currentPlayerIndex,
            isGameOver = _gameState.value.isGameOver,
            winner = _gameState.value.winner,
            gameStarted = gameStarted,
            allDiceScored = allDiceScored,
            playersInGame = playersInGame,
            isSinglePlayerMode = _gameState.value.isSinglePlayerMode,
            botDifficulty = _gameState.value.botDifficulty,
            selectedDiceChanged = _gameState.value.selectedDiceChanged,
            scoreExceeded = _gameState.value.scoreExceeded
                )
            }.collect { state ->
                _gameState.value = state
            }
        }

        initSounds()
    }

    private fun initSounds() {
        try {
            diceRollSound?.release()
            winSound?.release()
            diceRollSound = MediaPlayer.create(context, R.raw.dice_roll)
            winSound = MediaPlayer.create(context, R.raw.win_sound)
            diceRollSound?.setOnCompletionListener { it.seekTo(0) }
            winSound?.setOnCompletionListener { it.seekTo(0) }
        } catch (e: Exception) {
            Log.e("GameViewModel", "Error al inicializar sonidos: ${e.message}")
        }
    }

    fun loadGame() {
        viewModelScope.launch {
            if (gameId <= 0) {
                _message.value = context.getString(R.string.invalid_game_id)
                return@launch
            }

            try {
                _message.value = context.getString(R.string.loading_game)
                val initialGameState = gameStateUseCase.getGameState(gameId).first()

                if (initialGameState != null) {
                    _isSinglePlayerMode.value = initialGameState.game.gameMode == "SINGLE_PLAYER"
                    updateGameState(initialGameState)
                    initializePlayerScores()

                    val currentPlayer = initialGameState.currentPlayer
                    if (currentPlayer != null) {
                        val isInGame = _playersInGame.value[currentPlayer.id] ?: false
                        if (!isInGame) {
                            _message.value = context.getString(R.string.player_needs_minimum_points, currentPlayer.name)
                        } else {
                            _message.value = context.getString(R.string.player_turn, currentPlayer.name)
                        }
                        checkIfBotTurn(currentPlayer)
                    }

                    viewModelScope.launch {
                        gameStateUseCase.getGameScores(gameId).collect { scores ->
                            _playerScores.value = scores
                            _gameState.update { currentState -> currentState.copy(playerScores = scores) }
                        }
                    }
                } else {
                    _message.value = context.getString(R.string.game_not_found)
                }
            } catch (e: Exception) {
                _message.value = context.getString(R.string.error_loading_game, e.message ?: "")
                Log.e("GameViewModel", "Error en loadGame: ${e.message}", e)
            }
        }
    }
    
    private fun checkIfBotTurn(currentPlayer: Player) {
        Log.d("GameViewModel", "checkIfBotTurn: currentPlayer=${currentPlayer.name}, isSinglePlayerMode=${_isSinglePlayerMode.value}")

        if (_isSinglePlayerMode.value && currentPlayer.name == "Bot") {
            Log.d("GameViewModel", "Es turno del Bot, preparando ejecución")
            _isBotTurn.value = true
            _canRoll.value = false
            _canBank.value = false
            viewModelScope.launch {
                delay(1000)
                executeBotTurn()
            }
        } else {
            _isBotTurn.value = false
        }
    }

    private fun executeBotTurn() {
        val bot = _bot.value ?: return
        Log.d("GameViewModel", "Ejecutando turno del Bot: dificultad=${bot.difficulty}")

        viewModelScope.launch {
            try {
                _botActionInProgress.value = true

                val humanPlayerScore = _playerScores.value.entries
                    .filter { (playerId, _) -> _gameState.value.players.find { it.id == playerId }?.name != "Bot" }
                    .maxByOrNull { it.value }?.value ?: 0

                val botPlayer = _gameState.value.players.find { it.name == "Bot" }
                val botScore = botPlayer?.let { _playerScores.value[it.id] } ?: 0
                Log.d("GameViewModel", "Bot va a jugar: botPlayer=$botPlayer, botScore=$botScore, humanScore=$humanPlayerScore")

                viewModelScope.launch {
                    _message.value = null
                    _gameStarted.value = true

                    botTurnHandler.executeBotTurn(
                        bot = bot,
                        currentDice = _dice.value,
                        totalScore = botScore,
                        opponentMaxScore = humanPlayerScore,
                        onDiceRolled = { rolledDice ->
                            Log.d("GameViewModel", "Bot lanzando dados: ${rolledDice.size} dados")
                            _dice.value = rolledDice
                            _isRolling.value = true

                            viewModelScope.launch {
                                val soundEnabled = userPreferencesManager.soundEnabled.first()
                                val vibrationEnabled = userPreferencesManager.vibrationEnabled.first()
                                if (soundEnabled) playDiceRollSound()
                                if (vibrationEnabled) vibrate()
                            }

                            viewModelScope.launch {
                                delay(1000)
                                _isRolling.value = false
                            }
                        },
                        onDiceSelected = { selectedDice ->
                            Log.d("GameViewModel", "Bot seleccionando dados: ${selectedDice.count { it.isSelected }} seleccionados")
                            _dice.value = selectedDice
                            _selectedDice.value = selectedDice.filter { it.isSelected }
                            val (score, description) = calculateScoreUseCase(_selectedDice.value)
                            _currentTurnScore.value = _currentTurnScore.value + score
                            _message.value = description
                        },
                        onBankScore = { score ->
                            val botPlayer = _gameState.value.players.find { it.name == "Bot" } ?: return@executeBotTurn
                            val previousScore = _playerScores.value[botPlayer.id] ?: 0
                            val totalScore = previousScore + score

                            viewModelScope.launch {
                                val currentGame = gameStateUseCase.getGameState(gameId).first() ?: return@launch
                                val gameStateData = currentGame.additionalState.toMutableMap()

                                if (totalScore == currentGame.game.targetScore) {
                                    val updatedScores = _playerScores.value.toMutableMap()
                                    updatedScores[botPlayer.id] = totalScore
                                    _playerScores.value = updatedScores
                                    gameStateData["playerScore_${botPlayer.id}"] = totalScore
                                    gameStateData["playerInGame_${botPlayer.id}"] = true

                                    saveGameUseCase.saveScore(gameId, botPlayer.id, currentGame.currentRound, score, totalScore, 0)
                                    saveGameUseCase.completeGame(gameId, botPlayer.id)

                                    _gameState.update { it.copy(isGameOver = true, winner = botPlayer, canRoll = false, canBank = true) }
                                    _message.value = context.getString(R.string.bot_won_with_score, totalScore)
                                    _botActionInProgress.value = false
                                    _isBotTurn.value = false

                                    viewModelScope.launch {
                                        if (userPreferencesManager.soundEnabled.first()) playWinSound()
                                    }
                                    return@launch
                                } else if (totalScore > currentGame.game.targetScore) {
                                    _gameState.update { it.copy(scoreExceeded = true) }
                                    _message.value = context.getString(R.string.bot_exceeded_score)
                                    delay(2000)
                                    _botActionInProgress.value = false
                                    _isBotTurn.value = false
                                    nextPlayer()
                                    return@launch
                                }

                                val updatedScores = _playerScores.value.toMutableMap()
                                updatedScores[botPlayer.id] = totalScore
                                _playerScores.value = updatedScores
                                _message.value = context.getString(R.string.bot_saved_points, score)

                                gameStateData["playerScore_${botPlayer.id}"] = totalScore
                                gameStateData["playerInGame_${botPlayer.id}"] = true
                                saveGameUseCase.saveScore(gameId, botPlayer.id, currentGame.currentRound, score, totalScore, 0)

                                if (validateTurnUseCase.hasWon(totalScore, currentGame.game.targetScore)) {
                                    saveGameUseCase.completeGame(gameId, botPlayer.id)
                                    _gameState.update { it.copy(isGameOver = true, winner = botPlayer, canRoll = false, canBank = true) }
                                    _message.value = context.getString(R.string.bot_won_with_score, totalScore)
                                    _botActionInProgress.value = false
                                    _isBotTurn.value = false

                                    viewModelScope.launch {
                                        if (userPreferencesManager.soundEnabled.first()) playWinSound()
                                    }
                                    return@launch
                                }

                                saveGameUseCase(gameId, currentGame.currentPlayerIndex, currentGame.currentRound, gameStateData)
                                delay(1500)
                                _botActionInProgress.value = false
                                _isBotTurn.value = false
                                nextPlayer()
                            }
                        },
                        onTurnLost = {
                            _message.value = context.getString(R.string.bot_lost_turn)
                            viewModelScope.launch {
                                delay(1500)
                                _botActionInProgress.value = false
                                _isBotTurn.value = false
                                nextPlayer()
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error en el turno del Bot: ${e.message}", e)
                _message.value = context.getString(R.string.error_bot_turn)
                _botActionInProgress.value = false
                _isBotTurn.value = false
                nextPlayer()
            }
        }
    }

    private fun initializePlayerScores() {
        viewModelScope.launch {
            val currentGame = gameStateUseCase.getGameState(gameId).first()
            if (currentGame != null) {
                val initialPlayerScores = mutableMapOf<Long, Int>()
                val initialPlayersInGame = mutableMapOf<Long, Boolean>()

                currentGame.players.forEach { player ->
                    val scoreKey = "playerScore_${player.id}"
                    val score = when (val scoreValue = currentGame.additionalState[scoreKey]) {
                        is String -> scoreValue.toIntOrNull() ?: 0
                        is Number -> scoreValue.toInt()
                        else -> 0
                    }
                    initialPlayerScores[player.id] = score

                    val inGameKey = "playerInGame_${player.id}"
                    val isInGame = when (val inGameValue = currentGame.additionalState[inGameKey]) {
                        is String -> inGameValue.equals("true", ignoreCase = true)
                        is Boolean -> inGameValue
                        else -> (score >= 500)
                    }
                    initialPlayersInGame[player.id] = isInGame
                }

                _playerScores.value = initialPlayerScores
                _playersInGame.value = initialPlayersInGame

                val currentPlayer = currentGame.currentPlayer
                if (currentPlayer != null) {
                    val isInGame = initialPlayersInGame[currentPlayer.id] ?: false
                    if (!isInGame) {
                        _message.value = context.getString(R.string.player_needs_minimum_points, currentPlayer.name)
                    }
                }
            }
        }
    }

    private fun updateGameState(gameState: GameStateUseCase.GameState) {
        blockInteractionsDuringTransition(300)
        val savedDice = gameState.getDice()

        _gameState.update { currentState ->
            currentState.copy(
                currentRound = gameState.currentRound,
                currentPlayer = gameState.currentPlayer,
                players = gameState.players,
                currentPlayerIndex = gameState.currentPlayerIndex,
                isGameOver = gameState.isGameOver,
                winner = gameState.winner
            )
        }

        if (_playerScores.value.isEmpty()) {
            initializePlayerScores()
        }

        if (savedDice.isNotEmpty()) {
            _dice.value = savedDice
            _lockedDice.value = savedDice.filter { it.isLocked }
            _selectedDice.value = savedDice.filter { it.isSelected }
            _currentTurnScore.value = gameState.getCurrentTurnScore()
            _gameStarted.value = true

            val (canBankByDice, _) = validateTurnUseCase(_selectedDice.value)
            val currentPlayer = _gameState.value.currentPlayer
            val isPlayerInGame = currentPlayer?.let { _playersInGame.value[it.id] } ?: true

            _canBank.value = if (!isPlayerInGame) {
                canBankByDice && _currentTurnScore.value >= 500
            } else {
                canBankByDice && _currentTurnScore.value > 0
            }
        } else {
            _dice.value = emptyList()
            _selectedDice.value = emptyList()
            _lockedDice.value = emptyList()
            _currentTurnScore.value = 0
            _gameStarted.value = false
            _canBank.value = false
            _canRoll.value = !_isBotTurn.value
        }
    }

    fun onDiceClick(dice: Dice) {
        if (_isRolling.value || dice.isLocked) return

        val currentDice = _dice.value.toMutableList()
        val index = currentDice.indexOfFirst { it.id == dice.id }

        if (index >= 0) {
            if (!currentDice[index].isSelected) {
                currentDice[index] = currentDice[index].copy(isSelected = true)
                _dice.value = currentDice
                _selectedDice.value = currentDice.filter { it.isSelected }

                val (newSelectionScore, description) = calculateScoreUseCase(_selectedDice.value)
                val lockedDiceScore = calculateScoreUseCase(_lockedDice.value).first
                _currentTurnScore.value = lockedDiceScore + newSelectionScore
                updateProjectedScore(_currentTurnScore.value)

                if (newSelectionScore > 0) {
                    _message.value = description
                    val currentPlayer = _gameState.value.currentPlayer
                    val isPlayerInGame = currentPlayer?.let { _playersInGame.value[it.id] } ?: true
                    _canBank.value = if (!isPlayerInGame) _currentTurnScore.value >= 500 else true
                } else {
                    _message.value = context.getString(R.string.invalid_selection)
                    _canBank.value = false
                }
            } else {
                _message.value = context.getString(R.string.cannot_deselect_dice)
            }
        }

        _gameState.update { it.copy(selectedDiceChanged = true) }
    }

    private fun updateProjectedScore(currentTurnScore: Int) {
        val currentPlayer = _gameState.value.currentPlayer ?: return
        val currentScore = _playerScores.value[currentPlayer.id] ?: 0
        val projectedScore = currentScore + currentTurnScore
        val updatedProjections = _projectedScores.value.toMutableMap()
        updatedProjections[currentPlayer.id] = projectedScore
        _projectedScores.value = updatedProjections
    }

    private fun lockSelectedDice() {
        if (_selectedDice.value.isEmpty()) return

        val currentDice = _dice.value.toMutableList()
        _selectedDice.value.forEach { selectedDice ->
            val index = currentDice.indexOfFirst { it.id == selectedDice.id }
            if (index >= 0) {
                currentDice[index] = currentDice[index].copy(isSelected = false, isLocked = true)
            }
        }

        _dice.value = currentDice
        _lockedDice.value = currentDice.filter { it.isLocked }
        _selectedDice.value = emptyList()
    }

    fun onRollClick() {
        if (_gameState.value.scoreExceeded) return
        if (_isRolling.value || !_canRoll.value) return

        _message.value = null

        viewModelScope.launch {
            try {
                _isRolling.value = true
                _canRoll.value = false
                _canBank.value = false

                val soundEnabled = userPreferencesManager.soundEnabled.first()
                val vibrationEnabled = userPreferencesManager.vibrationEnabled.first()
                if (soundEnabled) playDiceRollSound()
                if (vibrationEnabled) vibrate()

                val currentGame = gameStateUseCase.getGameState(gameId).first()
                if (currentGame != null) {
                    val currentPlayer = currentGame.currentPlayer
                    if (currentPlayer != null) {
                        val isPlayerInGame = _playersInGame.value[currentPlayer.id] ?: false

                        if (_dice.value.isEmpty() || _allDiceScored.value || (_dice.value.isNotEmpty() && _dice.value.all { it.isLocked })) {
                            _dice.value = emptyList()
                            _selectedDice.value = emptyList()
                            _lockedDice.value = emptyList()
                            _dice.value = rollDiceUseCase.createNewDiceSet()
                            _allDiceScored.value = false

                            if (_dice.value.isEmpty()) {
                                setEssentialMessage(context.getString(R.string.error_creating_dice))
                                _isRolling.value = false
                                _canRoll.value = true
                                return@launch
                            }
                            _gameStarted.value = true
                        } else {
                            lockSelectedDice()
                        }

                        val unlockedDice = _dice.value.filter { !it.isLocked }
                        if (unlockedDice.isEmpty()) {
                            _canRoll.value = true
                            return@launch
                        }

                        val rolledDice = rollDiceUseCase(unlockedDice)
                        delay(300)

                        val updatedDiceState = _dice.value.map { dice ->
                            val rolledDie = rolledDice.find { it.id == dice.id }
                            if (rolledDie != null) dice.copy(value = rolledDie.value, isSelected = false) else dice
                        }
                        _dice.value = updatedDiceState

                        val hasScoringDice = validateTurnUseCase.hasScoringDice(updatedDiceState.filter { !it.isLocked })

                        if (!hasScoringDice) {
                            setEssentialMessage(context.getString(R.string.no_score))
                            _canRoll.value = false
                            _canBank.value = false
                            delay(1000)
                            _isRolling.value = false
                            nextPlayer()
                        } else {
                            autoSelectScoringDice()
                            _isRolling.value = false
                        }
                    }
                }
            } catch (e: Exception) {
                setEssentialMessage(context.getString(R.string.error_rolling_dice, e.message ?: ""))
                Log.e("GameViewModel", "Error en onRollClick: ${e.message}", e)
                _isRolling.value = false
                _canRoll.value = true
            }
        }
    }

    private fun autoSelectScoringDice() {
        val unlockedDice = _dice.value.filter { !it.isLocked }
        if (unlockedDice.isEmpty()) return

        val bestScoringDice = findBestScoringDice(unlockedDice)

        if (bestScoringDice.isNotEmpty()) {
            val updatedDice = _dice.value.map { dice ->
                if (!dice.isLocked) {
                    dice.copy(isSelected = bestScoringDice.any { it.id == dice.id })
                } else {
                    dice
                }
            }

            _dice.value = updatedDice
            _selectedDice.value = updatedDice.filter { it.isSelected }

            val (newSelectionScore, description) = calculateScoreUseCase(_selectedDice.value)
            val lockedDiceScore = calculateScoreUseCase(_lockedDice.value).first
            _currentTurnScore.value = _currentTurnScore.value - lockedDiceScore + lockedDiceScore + newSelectionScore
            updateProjectedScore(_currentTurnScore.value)

            val allUnlockedSelected = unlockedDice.size == bestScoringDice.size
            _allDiceScored.value = allUnlockedSelected

            _message.value = when {
                allUnlockedSelected && unlockedDice.size == 6 -> context.getString(R.string.all_dice_score_full, _currentTurnScore.value)
                allUnlockedSelected -> context.getString(R.string.all_available_dice_score, _currentTurnScore.value)
                else -> description
            }

            val currentPlayer = _gameState.value.currentPlayer
            val isPlayerInGame = currentPlayer?.let { _playersInGame.value[it.id] } ?: true
            _canBank.value = if (!isPlayerInGame) newSelectionScore > 0 && _currentTurnScore.value >= 500 else newSelectionScore > 0
            _canRoll.value = true
        }
    }

    private fun findBestScoringDice(availableDice: List<Dice>): List<Dice> {
        if (availableDice.isEmpty()) return emptyList()

        val scoringDice = mutableListOf<Dice>()

        if (availableDice.size == 6) {
            val values = availableDice.map { it.value }.toSet()
            if (values.size == 6 && values.containsAll(listOf(1, 2, 3, 4, 5, 6))) {
                return availableDice
            }
        }

        if (availableDice.size == 6) {
            val valueCounts = availableDice.groupBy { it.value }.mapValues { it.value.size }
            if (valueCounts.size == 3 && valueCounts.values.all { it == 2 }) {
                return availableDice
            }
        }

        val diceByValue = availableDice.groupBy { it.value }

        for (count in 6 downTo 3) {
            diceByValue.forEach { (_, diceList) ->
                if (diceList.size >= count) {
                    scoringDice.addAll(diceList.take(count))
                }
            }
        }

        val remainingDice = availableDice.filter { it !in scoringDice }

        remainingDice.filter { it.value == 1 }.forEach { die ->
            if (scoringDice.count { it.value == 1 } < 3) {
                scoringDice.add(die)
            }
        }

        remainingDice.filter { it.value == 5 }.forEach { die ->
            if (scoringDice.count { it.value == 5 } < 3) {
                scoringDice.add(die)
            }
        }

        if (scoringDice.isEmpty()) {
            scoringDice.addAll(availableDice.filter { it.value == 1 })
            scoringDice.addAll(availableDice.filter { it.value == 5 })

            if (scoringDice.isEmpty()) {
                for (value in 2..6) {
                    val sameValueDice = availableDice.filter { it.value == value }
                    if (sameValueDice.size >= 3) {
                        scoringDice.addAll(sameValueDice.take(3))
                        break
                    }
                }
            }
        }

        return scoringDice
    }

    fun onBankClick() {
        if (_gameState.value.scoreExceeded) {
            onNextPlayerClick()
            return
        }

        viewModelScope.launch {
            if (!_canBank.value) return@launch

            _canRoll.value = false
            _canBank.value = false

            val currentGame = gameStateUseCase.getGameState(gameId).first()
            if (currentGame != null) {
                val currentPlayer = currentGame.currentPlayer
                if (currentPlayer != null) {
                    val isPlayerInGame = _playersInGame.value[currentPlayer.id] ?: false
                    val currentTurnScore = _currentTurnScore.value

                    if (!isPlayerInGame && currentTurnScore < 500) {
                        setEssentialMessage(context.getString(R.string.minimum_points_required))
                        delay(1000)
                        nextPlayer()
                        return@launch
                    }

                    if (!isPlayerInGame && currentTurnScore >= 500) {
                        val updatedPlayersInGame = _playersInGame.value.toMutableMap()
                        updatedPlayersInGame[currentPlayer.id] = true
                        _playersInGame.value = updatedPlayersInGame
                        delay(500)
                    }

                    val previousScore = _playerScores.value[currentPlayer.id] ?: 0
                    val totalScore = previousScore + _currentTurnScore.value

                    if (totalScore == currentGame.game.targetScore) {
                        try {
                            val updatedScores = _playerScores.value.toMutableMap()
                            updatedScores[currentPlayer.id] = totalScore
                            _playerScores.value = updatedScores

                            val gameStateData = currentGame.additionalState.toMutableMap()
                            gameStateData["playerScore_${currentPlayer.id}"] = totalScore
                            gameStateData["playerInGame_${currentPlayer.id}"] = true

                            saveGameUseCase.saveScore(gameId, currentPlayer.id, currentGame.currentRound, _currentTurnScore.value, totalScore, 0)
                            saveGameUseCase.completeGame(gameId, currentPlayer.id)

                            _gameState.update { it.copy(isGameOver = true, winner = currentPlayer, canRoll = false, canBank = true) }
                            setEssentialMessage(context.getString(R.string.player_won_with_score, currentPlayer.name, totalScore))

                            if (userPreferencesManager.soundEnabled.first()) playWinSound()
                            return@launch
                        } catch (e: Exception) {
                            setEssentialMessage(context.getString(R.string.error_completing_game, e.message ?: ""))
                            Log.e("GameViewModel", "Error al completar el juego: ${e.message}", e)
                        }
                    } else if (totalScore > currentGame.game.targetScore) {
                        setEssentialMessage(context.getString(R.string.score_exceeded))
                        delay(1000)
                        nextPlayer()
                        return@launch
                    }

                    try {
                        val updatedScores = _playerScores.value.toMutableMap()
                        updatedScores[currentPlayer.id] = totalScore
                        _playerScores.value = updatedScores

                        setEssentialMessage(context.getString(R.string.player_saved_points, currentPlayer.name, _currentTurnScore.value))

                        val gameStateData = currentGame.additionalState.toMutableMap()
                        gameStateData["playerScore_${currentPlayer.id}"] = totalScore
                        gameStateData["playerInGame_${currentPlayer.id}"] = true

                        saveGameUseCase.saveScore(gameId, currentPlayer.id, currentGame.currentRound, _currentTurnScore.value, totalScore, 0)

                        if (validateTurnUseCase.hasWon(totalScore, currentGame.game.targetScore)) {
                            saveGameUseCase.completeGame(gameId, currentPlayer.id)
                            _gameState.update { it.copy(isGameOver = true, winner = currentPlayer, canRoll = false, canBank = true) }
                            setEssentialMessage(context.getString(R.string.player_won_with_score, currentPlayer.name, totalScore))
                            if (userPreferencesManager.soundEnabled.first()) playWinSound()
                            return@launch
                        } else {
                            saveGameUseCase(gameId, currentGame.currentPlayerIndex, currentGame.currentRound, gameStateData)
                            delay(1000)
                            nextPlayer()
                        }
                    } catch (e: Exception) {
                        setEssentialMessage(context.getString(R.string.error_saving_score, e.message ?: ""))
                        Log.e("GameViewModel", "Error en onBankClick: ${e.message}", e)
                        _canRoll.value = true
                        _canBank.value = true
                    }
                }
            }
        }
    }

    private fun nextPlayer() {
        viewModelScope.launch {
            _canRoll.value = false
            _canBank.value = false

            val currentGame = gameStateUseCase.getGameState(gameId).first()
            if (currentGame != null) {
                val nextPlayerIndex = (currentGame.currentPlayerIndex + 1) % currentGame.players.size
                val nextRound = if (nextPlayerIndex == 0) currentGame.currentRound + 1 else currentGame.currentRound
                val nextPlayer = currentGame.players.getOrNull(nextPlayerIndex)

                _dice.value = emptyList()
                _selectedDice.value = emptyList()
                _lockedDice.value = emptyList()
                _currentTurnScore.value = 0
                _allDiceScored.value = false
                _gameStarted.value = false

                _gameState.update { it.copy(currentPlayerIndex = nextPlayerIndex, currentRound = nextRound, currentPlayer = nextPlayer, scoreExceeded = false) }

                try {
                    saveGameUseCase(gameId, nextPlayerIndex, nextRound, currentGame.additionalState)
                    nextPlayer?.let { checkIfBotTurn(it) }
                    if (!_isBotTurn.value) _canRoll.value = true
                } catch (e: Exception) {
                    setEssentialMessage(context.getString(R.string.error_changing_player, e.message ?: ""))
                    Log.e("GameViewModel", "Error en nextPlayer: ${e.message}", e)
                    if (!_isBotTurn.value) _canRoll.value = true
                }
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    private fun playDiceRollSound() {
        try {
            diceRollSound?.let {
                if (it.isPlaying) { it.stop(); it.prepare() }
                it.start()
            } ?: run { initSounds(); diceRollSound?.start() }
        } catch (e: Exception) {
            Log.e("GameViewModel", "Error al reproducir sonido de dados: ${e.message}")
        }
    }

    private fun playWinSound() {
        try {
            winSound?.let {
                if (it.isPlaying) { it.stop(); it.prepare() }
                it.start()
            } ?: run { initSounds(); winSound?.start() }
        } catch (e: Exception) {
            Log.e("GameViewModel", "Error al reproducir sonido de victoria: ${e.message}")
        }
    }

    private fun vibrate() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }
        } catch (e: Exception) {
            Log.e("GameViewModel", "Error al activar vibración: ${e.message}")
        }
    }

    fun exitGame() {
        viewModelScope.launch {
            try {
                if (_isSinglePlayerMode.value && botPlayerId > 0) {
                    Log.d("GameViewModel", "Eliminando bot al salir de la partida, ID: $botPlayerId")
                    saveGameUseCase.deleteBotPlayer(botPlayerId)
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error al eliminar bot: ${e.message}", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (_isSinglePlayerMode.value && botPlayerId > 0) {
            viewModelScope.launch {
                try {
                    Log.d("GameViewModel", "Eliminando bot en onCleared, ID: $botPlayerId")
                    saveGameUseCase.deleteBotPlayer(botPlayerId)
                } catch (e: Exception) {
                    Log.e("GameViewModel", "Error al eliminar bot en onCleared: ${e.message}", e)
                }
            }
        }
        diceRollSound?.release()
        diceRollSound = null
        winSound?.release()
        winSound = null
    }

    fun onScoreExceeded() {
        _message.value = context.getString(R.string.score_exceeded_lose_turn)
        viewModelScope.launch {
            delay(500)
            _gameState.update { currentState ->
                val nextPlayerIndex = (currentState.currentPlayerIndex + 1) % currentState.players.size
                currentState.copy(currentTurnScore = 0, currentPlayerIndex = nextPlayerIndex, dice = resetDice(), gameStarted = false, canRoll = true, canBank = false, selectedDiceChanged = false)
            }
        }
    }

    private fun resetDice(): List<Dice> {
        return List(6) { index -> Dice(id = index, value = 1, isSelected = false, isLocked = false) }
    }

    fun resetSelectedDiceChangedFlag() {
        _gameState.update { it.copy(selectedDiceChanged = false) }
    }

    fun setScoreExceeded(exceeded: Boolean) {
        _gameState.update { it.copy(scoreExceeded = exceeded, canRoll = if (exceeded) false else it.canRoll) }
    }

    fun showScoreExceededMessage() {
        _message.value = context.getString(R.string.score_exceeded_click_pass)
    }

    fun onNextPlayerClick() {
        blockInteractionsDuringTransition()
        _message.value = null
        nextPlayer()
        _gameState.update { it.copy(scoreExceeded = false, currentTurnScore = 0) }
    }

    private fun blockInteractionsDuringTransition(durationMs: Long = 500) {
        viewModelScope.launch {
            _canRoll.value = false
            _canBank.value = false
            delay(durationMs)

            if (!_isRolling.value && !_gameState.value.scoreExceeded && !_isBotTurn.value) {
                _canRoll.value = true
                val (canBankByDice, _) = validateTurnUseCase(_selectedDice.value)
                _canBank.value = canBankByDice && _currentTurnScore.value > 0
            }
        }
    }

    private fun setEssentialMessage(message: String?) {
        if (message == null) {
            _message.value = null
            return
        }
        val essentialKeywords = listOf("guardado", "puntos", "perdido el turno", "sin puntuación", "superado los 10,000", "ganado", "error", "Error")
        if (essentialKeywords.any { message.contains(it, ignoreCase = true) }) {
            _message.value = message
        }
    }
}
