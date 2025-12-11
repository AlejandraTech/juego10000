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
    private val botTurnHandler: BotTurnHandler
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
            // Cargar el juego
            loadGame()

            // Observar las puntuaciones del juego de forma continua
            gameStateUseCase.getGameScores(gameId).collect { scores ->
                _playerScores.value = scores

                // Forzar actualización del estado de la UI cuando cambian las puntuaciones
                _gameState.value = _gameState.value.copy(
                    playerScores = scores
                )
            }
        }
        
        // Verificar si es modo individual y configurar el Bot
        viewModelScope.launch {
            val game = gameStateUseCase.getGameState(gameId).first()
            if (game != null && game.game.gameMode == "SINGLE_PLAYER") {
                _isSinglePlayerMode.value = true
                
                // Obtener la dificultad del Bot desde las preferencias
                val botDifficultyStr = userPreferencesManager.botDifficulty.first()
                val botDifficulty = when (botDifficultyStr) {
                    "BEGINNER" -> BotDifficulty.BEGINNER
                    "INTERMEDIATE" -> BotDifficulty.INTERMEDIATE
                    "EXPERT" -> BotDifficulty.EXPERT
                    else -> BotDifficulty.INTERMEDIATE // Valor por defecto
                }
                
                // Buscar el jugador Bot entre los jugadores del juego
                val botPlayer = game.players.find { it.name == "Bot" }
                if (botPlayer != null) {
                    botPlayerId = botPlayer.id
                    
                    // Crear el Bot con la dificultad correspondiente
                    _bot.value = Bot(
                        difficulty = botDifficulty,
                        name = botPlayer.name
                    )
                    
                    Log.d("GameViewModel", "Bot configurado con ID: $botPlayerId")
                }
                
                // Actualizar el estado de la UI con la información del modo de juego y la dificultad del bot
                _gameState.update { currentState ->
                    currentState.copy(
                        isSinglePlayerMode = true,
                        botDifficulty = botDifficulty
                    )
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

        // Inicializar los MediaPlayers
        initSounds()
    }

    /**
     * Inicializa los efectos de sonido
     */
    private fun initSounds() {
        try {
            // Liberar recursos previos si existen
            diceRollSound?.release()
            winSound?.release()

            // Crear nuevos MediaPlayers
            diceRollSound = MediaPlayer.create(context, R.raw.dice_roll)
            winSound = MediaPlayer.create(context, R.raw.win_sound)

            // Configurar para que se puedan reproducir múltiples veces
            diceRollSound?.setOnCompletionListener { it.seekTo(0) }
            winSound?.setOnCompletionListener { it.seekTo(0) }
        } catch (e: Exception) {
            Log.e("GameViewModel", "Error al inicializar sonidos: ${e.message}")
        }
    }

    fun loadGame() {
        viewModelScope.launch {
            if (gameId <= 0) {
                _message.value = "ID de juego inválido"
                return@launch
            }

            try {
                // Mostrar un indicador de carga o mensaje
                _message.value = "Cargando juego..."

                // Cargar el estado del juego de forma síncrona para la carga inicial
                val initialGameState = gameStateUseCase.getGameState(gameId).first()

                if (initialGameState != null) {
                    // Verificar si es modo individual
                    _isSinglePlayerMode.value = initialGameState.game.gameMode == "SINGLE_PLAYER"
                    
                    // Actualizar el estado de la UI con los datos del juego
                    updateGameState(initialGameState)

                    // Inicializar las puntuaciones inmediatamente
                    initializePlayerScores()

                    // Mostrar mensaje inicial para el primer jugador
                    val currentPlayer = initialGameState.currentPlayer
                    if (currentPlayer != null) {
                        val isInGame = _playersInGame.value[currentPlayer.id] ?: false
                        if (!isInGame) {
                            _message.value = context.getString(
                                R.string.player_needs_minimum_points,
                                currentPlayer.name
                            )
                        } else {
                            _message.value = "Turno de ${currentPlayer.name}"
                        }
                        
                        // Verificar si es el turno del Bot
                        checkIfBotTurn(currentPlayer)
                    }

                    // Ahora que tenemos la carga inicial, configurar la observación continua
                    // para actualizaciones futuras
                    viewModelScope.launch {
                        gameStateUseCase.getGameScores(gameId).collect { scores ->
                            _playerScores.value = scores

                            // Forzar actualización del estado de la UI cuando cambian las puntuaciones
                            _gameState.update { currentState ->
                                currentState.copy(
                                    playerScores = scores
                                )
                            }
                        }
                    }
                } else {
                    _message.value = "No se encontró el juego"
                }
            } catch (e: Exception) {
                _message.value = "Error al cargar el juego: ${e.message}"
                Log.e("GameViewModel", "Error en loadGame: ${e.message}", e)
            }
        }
    }
    
    /**
     * Verifica si es el turno del Bot y ejecuta su lógica si es necesario
     */
    private fun checkIfBotTurn(currentPlayer: Player) {
        Log.d("GameViewModel", "checkIfBotTurn: currentPlayer=${currentPlayer.name}, isSinglePlayerMode=${_isSinglePlayerMode.value}")

        if (_isSinglePlayerMode.value && currentPlayer.name == "Bot") {
            Log.d("GameViewModel", "Es turno del Bot, preparando ejecución")
            _isBotTurn.value = true
            // Deshabilitar los controles inmediatamente para evitar interacciones del usuario
            _canRoll.value = false
            _canBank.value = false
            // Añadir un pequeño retraso antes de ejecutar el turno del bot
            // para que el usuario pueda ver que es el turno del bot
            viewModelScope.launch {
                delay(1000)
                executeBotTurn()
            }
        } else {
            _isBotTurn.value = false
        }
    }
    
    /**
     * Ejecuta el turno del Bot
     */
    private fun executeBotTurn() {
        val bot = _bot.value ?: return
        
        Log.d("GameViewModel", "Ejecutando turno del Bot: dificultad=${bot.difficulty}")
        
        viewModelScope.launch {
            try {
                _botActionInProgress.value = true
                
                // Obtener la puntuación máxima del jugador humano
                val humanPlayerScore = _playerScores.value.entries
                    .filter { (playerId, _) -> 
                        _gameState.value.players.find { it.id == playerId }?.name != "Bot"
                    }
                    .maxByOrNull { it.value }?.value ?: 0
                
                // Obtener la puntuación actual del Bot
                val botPlayer = _gameState.value.players.find { it.name == "Bot" }
                val botScore = botPlayer?.let { _playerScores.value[it.id] } ?: 0
                
                Log.d("GameViewModel", "Bot va a jugar: botPlayer=$botPlayer, botScore=$botScore, humanScore=$humanPlayerScore")
                
                // Ejecutar el turno del Bot dentro del viewModelScope.launch
                // Usamos una corrutina para llamar a la función suspendida
                viewModelScope.launch {
                    // Limpiar cualquier mensaje anterior ANTES de que el bot empiece
                    // Esto evita que aparezca el mensaje del turno anterior del jugador humano
                    _message.value = null

                    // Asegurarse de que el juego esté en estado "comenzado" para que se muestren los dados
                    _gameStarted.value = true

                    botTurnHandler.executeBotTurn(
                        bot = bot,
                        currentDice = _dice.value,
                        totalScore = botScore,
                        opponentMaxScore = humanPlayerScore,
                        onDiceRolled = { rolledDice ->
                        Log.d("GameViewModel", "Bot lanzando dados: ${rolledDice.size} dados")
                        
                        // Actualizar los dados y marcar como en proceso de lanzamiento
                        _dice.value = rolledDice
                        _isRolling.value = true
                        
                        // Verificar si el sonido y la vibración están habilitados antes de reproducirlos
                        viewModelScope.launch {
                            val soundEnabled = userPreferencesManager.soundEnabled.first()
                            val vibrationEnabled = userPreferencesManager.vibrationEnabled.first()
                            
                            // Reproducir sonido si está habilitado
                            if (soundEnabled) {
                                playDiceRollSound()
                            }
                            
                            // Vibrar si está habilitado
                            if (vibrationEnabled) {
                                vibrate()
                            }
                        }
                        
                        // Finalizar la animación después de un breve retraso
                        // Aumentamos el retraso para que el usuario pueda ver los dados
                        viewModelScope.launch {
                            delay(1000)
                            _isRolling.value = false
                        }
                    },
                    onDiceSelected = { selectedDice ->
                        Log.d("GameViewModel", "Bot seleccionando dados: ${selectedDice.count { it.isSelected }} seleccionados")

                        // Actualizar los dados
                        _dice.value = selectedDice
                        _selectedDice.value = selectedDice.filter { it.isSelected }

                        // Calcular puntuación de los dados seleccionados actualmente
                        val (score, description) = calculateScoreUseCase(_selectedDice.value)

                        // Actualizar la puntuación del turno sumando los puntos de los dados seleccionados
                        _currentTurnScore.value = _currentTurnScore.value + score

                        // Mostrar mensaje con la descripción de la puntuación
                        _message.value = description
                    },
                        onBankScore = { score ->
                            // Guardar la puntuación
                            val botPlayer = _gameState.value.players.find { it.name == "Bot" } ?: return@executeBotTurn
                            val previousScore = _playerScores.value[botPlayer.id] ?: 0
                            val totalScore = previousScore + score
                            
                            // Actualizar el estado del juego
                            viewModelScope.launch {
                                val currentGame = gameStateUseCase.getGameState(gameId).first() ?: return@launch
                                val gameStateData = currentGame.additionalState.toMutableMap()
                                
                                // Verificar si el Bot ha alcanzado exactamente o superado la puntuación objetivo
                                if (totalScore == currentGame.game.targetScore) {
                                    // ¡Victoria! El Bot ha alcanzado exactamente 10,000 puntos
                                    
                                    // Actualizar inmediatamente las puntuaciones en la UI
                                    val updatedScores = _playerScores.value.toMutableMap()
                                    updatedScores[botPlayer.id] = totalScore
                                    _playerScores.value = updatedScores
                                    
                                    // Actualizar datos del estado del juego
                                    gameStateData["playerScore_${botPlayer.id}"] = totalScore
                                    gameStateData["playerInGame_${botPlayer.id}"] = true
                                    
                                    // Guardar puntuación del turno
                                    saveGameUseCase.saveScore(
                                        gameId = gameId,
                                        playerId = botPlayer.id,
                                        round = currentGame.currentRound,
                                        turnScore = score,
                                        totalScore = totalScore,
                                        diceRolls = 0
                                    )
                                    
                                    // Completar el juego con el ganador
                                    saveGameUseCase.completeGame(gameId, botPlayer.id)
                                    
                                    // Actualizar estado de UI
                                    _gameState.update { currentState ->
                                        currentState.copy(
                                            isGameOver = true,
                                            winner = botPlayer,
                                            canRoll = false,
                                            canBank = true
                                        )
                                    }
                                    
                                    // Mensaje de victoria
                                    _message.value = "¡Bot ha ganado con $totalScore puntos!"

                                    // Resetear estados del bot
                                    _botActionInProgress.value = false
                                    _isBotTurn.value = false

                                    // Reproducir sonido de victoria si está habilitado
                                    viewModelScope.launch {
                                        val soundEnabled = userPreferencesManager.soundEnabled.first()
                                        if (soundEnabled) {
                                            playWinSound()
                                        }
                                    }

                                    return@launch
                                } else if (totalScore > currentGame.game.targetScore) {
                                    // El Bot ha superado los 10,000 puntos, pierde el turno
                                    _gameState.update { currentState ->
                                        currentState.copy(scoreExceeded = true)
                                    }
                                    // Usar _message.value (fuente única de verdad para mensajes)
                                    _message.value = "¡Bot ha superado los 10,000 puntos! Pierde su turno y los puntos acumulados."

                                    // Mostrar el mensaje durante más tiempo
                                    delay(2000)

                                    // Resetear estados del bot ANTES de pasar al siguiente jugador
                                    _botActionInProgress.value = false
                                    _isBotTurn.value = false

                                    // No guardar la puntuación y pasar al siguiente jugador
                                    nextPlayer()
                                    return@launch
                                }
                                
                                // Si no ha superado los 10,000 puntos, actualizar la puntuación en la UI
                                val updatedScores = _playerScores.value.toMutableMap()
                                updatedScores[botPlayer.id] = totalScore
                                _playerScores.value = updatedScores
                                
                                // Mostrar mensaje
                                _message.value = "Bot ha guardado $score puntos"
                                
                                // Actualizar datos del estado del juego
                                gameStateData["playerScore_${botPlayer.id}"] = totalScore
                                gameStateData["playerInGame_${botPlayer.id}"] = true
                                
                                // Guardar puntuación del turno
                                saveGameUseCase.saveScore(
                                    gameId = gameId,
                                    playerId = botPlayer.id,
                                    round = currentGame.currentRound,
                                    turnScore = score,
                                    totalScore = totalScore,
                                    diceRolls = 0
                                )
                                
                                // Si no ha ganado ni superado la puntuación objetivo, verificar si ha ganado
                                if (validateTurnUseCase.hasWon(totalScore, currentGame.game.targetScore)) {
                                    // Completar el juego con el ganador
                                    saveGameUseCase.completeGame(gameId, botPlayer.id)

                                    // Actualizar estado de UI
                                    _gameState.update { currentState ->
                                        currentState.copy(
                                            isGameOver = true,
                                            winner = botPlayer,
                                            canRoll = false,
                                            canBank = true
                                        )
                                    }

                                    // Mensaje de victoria
                                    _message.value = "¡Bot ha ganado con $totalScore puntos!"

                                    // Resetear estados del bot
                                    _botActionInProgress.value = false
                                    _isBotTurn.value = false

                                    // Reproducir sonido de victoria si está habilitado
                                    viewModelScope.launch {
                                        val soundEnabled = userPreferencesManager.soundEnabled.first()
                                        if (soundEnabled) {
                                            playWinSound()
                                        }
                                    }

                                    return@launch
                                }

                                // Guardar estado
                                saveGameUseCase(
                                    gameId = gameId,
                                    currentPlayerIndex = currentGame.currentPlayerIndex,
                                    currentRound = currentGame.currentRound,
                                    gameStateData = gameStateData
                                )

                                // Pausa para que el usuario vea el mensaje (igual que jugador humano)
                                delay(1500)

                                // Resetear estados del bot ANTES de pasar al siguiente jugador
                                _botActionInProgress.value = false
                                _isBotTurn.value = false

                                // Pasar al siguiente jugador
                                nextPlayer()
                            }
                    },
                        onTurnLost = {
                            // El Bot pierde el turno
                            _message.value = "¡Bot ha perdido el turno!"

                            // Pausa para que el usuario vea el mensaje
                            viewModelScope.launch {
                                delay(1500)
                                // Resetear estados del bot ANTES de pasar al siguiente jugador
                                _botActionInProgress.value = false
                                _isBotTurn.value = false
                                // Pasar al siguiente jugador
                                nextPlayer()
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error en el turno del Bot: ${e.message}", e)
                _message.value = "Error en el turno del Bot"
                _botActionInProgress.value = false
                _isBotTurn.value = false
                nextPlayer()
            }
            // No usar finally aquí porque los callbacks son asíncronos
            // y se resetean los estados en cada callback individualmente
        }
    }

    private fun initializePlayerScores() {
        viewModelScope.launch {
            val currentGame = gameStateUseCase.getGameState(gameId).first()
            if (currentGame != null) {
                // Inicializar puntuaciones de jugadores
                val initialPlayerScores = mutableMapOf<Long, Int>()
                val initialPlayersInGame = mutableMapOf<Long, Boolean>()

                currentGame.players.forEach { player ->
                    // Obtener puntuación guardada o inicializar a 0
                    val scoreKey = "playerScore_${player.id}"
                    val score = when (val scoreValue = currentGame.additionalState[scoreKey]) {
                        is String -> scoreValue.toIntOrNull() ?: 0
                        is Number -> scoreValue.toInt()
                        else -> 0
                    }
                    initialPlayerScores[player.id] = score

                    // Obtener estado de jugador en juego o determinar basado en puntuación
                    val inGameKey = "playerInGame_${player.id}"
                    val isInGame = when (val inGameValue = currentGame.additionalState[inGameKey]) {
                        is String -> inGameValue.equals("true", ignoreCase = true)
                        is Boolean -> inGameValue
                        else -> (score >= 500) // Si no hay estado guardado, determinar por puntuación
                    }
                    initialPlayersInGame[player.id] = isInGame
                }

                // Actualizar estado
                _playerScores.value = initialPlayerScores
                _playersInGame.value = initialPlayersInGame

                // Mostrar mensaje para el jugador actual si necesita puntos mínimos
                val currentPlayer = currentGame.currentPlayer
                if (currentPlayer != null) {
                    val isInGame = initialPlayersInGame[currentPlayer.id] ?: false
                    if (!isInGame) {
                        _message.value = context.getString(
                            R.string.player_needs_minimum_points,
                            currentPlayer.name
                        )
                    }
                }
            }
        }
    }

    private fun updateGameState(gameState: GameStateUseCase.GameState) {
        // Bloquear interacciones durante la actualización del estado
        blockInteractionsDuringTransition(300)

        val savedDice = gameState.getDice()

        // Actualizar el estado del juego de forma atómica para evitar estados intermedios inconsistentes
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

        // Inicializar las puntuaciones de los jugadores si es necesario
        if (_playerScores.value.isEmpty()) {
            initializePlayerScores()
        }

        if (savedDice.isNotEmpty()) {
            // Actualizar los dados de forma atómica
            _dice.value = savedDice
            _lockedDice.value = savedDice.filter { it.isLocked }
            _selectedDice.value = savedDice.filter { it.isSelected }
            _currentTurnScore.value = gameState.getCurrentTurnScore()
            _gameStarted.value = true

            // Verificar si se puede bancar con los dados seleccionados
            val (canBankByDice, _) = validateTurnUseCase(_selectedDice.value)

            // Verificar si el jugador está en juego o si ha alcanzado los 500 puntos mínimos
            val currentPlayer = _gameState.value.currentPlayer
            val isPlayerInGame = currentPlayer?.let { _playersInGame.value[it.id] } ?: true

            // Solo permitir bancar si el jugador ya está en juego o si ha alcanzado 500 puntos
            _canBank.value = if (!isPlayerInGame) {
                // Si no está en juego, verificar si ha alcanzado 500 puntos
                canBankByDice && _currentTurnScore.value >= 500
            } else {
                // Si ya está en juego, solo verificar que haya puntuación
                canBankByDice && _currentTurnScore.value > 0
            }
        } else {
            // No inicializar dados hasta que el jugador haga clic en "Lanzar dados"
            _dice.value = emptyList()
            _selectedDice.value = emptyList()
            _lockedDice.value = emptyList()
            _currentTurnScore.value = 0
            _gameStarted.value = false
            _canBank.value = false
            // Solo habilitar si no es turno del bot (se verificará después en checkIfBotTurn)
            // Por ahora dejamos en false, checkIfBotTurn o el código posterior lo habilitará si corresponde
            _canRoll.value = !_isBotTurn.value
        }
    }

    fun onDiceClick(dice: Dice) {
        // No permitir interacción durante la animación de lanzamiento
        // o si el dado ya está bloqueado
        if (_isRolling.value || dice.isLocked) return

        val currentDice = _dice.value.toMutableList()
        val index = currentDice.indexOfFirst { it.id == dice.id }

        if (index >= 0) {
            // Si el dado ya está seleccionado, no permitir deseleccionarlo
            // Solo permitir seleccionar dados adicionales
            if (!currentDice[index].isSelected) {
                val updatedDice = currentDice[index].copy(
                    isSelected = true
                )
                currentDice[index] = updatedDice
                _dice.value = currentDice

                // Actualizar dados seleccionados
                _selectedDice.value = currentDice.filter { it.isSelected }

                // Calcular puntuación de los dados seleccionados actualmente
                val (newSelectionScore, description) = calculateScoreUseCase(_selectedDice.value)

                // Calcular puntuación de los dados ya bloqueados
                val lockedDiceScore = calculateScoreUseCase(_lockedDice.value).first

                // Actualizar la puntuación total del turno (bloqueados + seleccionados)
                _currentTurnScore.value = lockedDiceScore + newSelectionScore

                // Actualizar puntuación proyectada para el jugador actual
                updateProjectedScore(_currentTurnScore.value)

                if (newSelectionScore > 0) {
                    _message.value = description

                    // Verificar si el jugador está en juego o si ha alcanzado los 500 puntos mínimos
                    val currentPlayer = _gameState.value.currentPlayer
                    val isPlayerInGame = currentPlayer?.let { _playersInGame.value[it.id] } ?: true

                    // Solo permitir bancar si el jugador ya está en juego o si ha alcanzado 500 puntos
                    _canBank.value = if (!isPlayerInGame) {
                        // Si no está en juego, verificar si ha alcanzado 500 puntos
                        _currentTurnScore.value >= 500
                    } else {
                        // Si ya está en juego, solo verificar que haya puntuación
                        true
                    }
                } else {
                    _message.value = "Selección no válida"
                    _canBank.value = false
                }
            } else {
                // Si intenta deseleccionar, mostrar mensaje informativo
                _message.value = context.getString(R.string.cannot_deselect_dice)
            }
        }

        // Al final, establece el flag
        _gameState.update { currentState ->
            currentState.copy(
                selectedDiceChanged = true
            )
        }
    }

    // Añadir esta nueva función
    private fun updateProjectedScore(currentTurnScore: Int) {
        val currentPlayer = _gameState.value.currentPlayer ?: return
        val currentScore = _playerScores.value[currentPlayer.id] ?: 0
        val projectedScore = currentScore + currentTurnScore

        // Actualizar el mapa de puntuaciones proyectadas
        val updatedProjections = _projectedScores.value.toMutableMap()
        updatedProjections[currentPlayer.id] = projectedScore
        _projectedScores.value = updatedProjections
    }

    /**
     * Bloquea los dados que han sido seleccionados con puntuación
     * para que no puedan ser modificados en futuros lanzamientos
     */
    private fun lockSelectedDice() {
        if (_selectedDice.value.isEmpty()) return

        val currentDice = _dice.value.toMutableList()

        // Encontrar los dados seleccionados y marcarlos como bloqueados
        _selectedDice.value.forEach { selectedDice ->
            val index = currentDice.indexOfFirst { it.id == selectedDice.id }
            if (index >= 0) {
                val updatedDice = currentDice[index].copy(
                    isSelected = false,
                    isLocked = true
                )
                currentDice[index] = updatedDice
            }
        }

        _dice.value = currentDice

        // Actualizar los dados bloqueados
        _lockedDice.value = currentDice.filter { it.isLocked }

        // Limpiar la selección actual
        _selectedDice.value = emptyList()
    }

    fun onRollClick() {
        // Si se ha excedido la puntuación, no permitir lanzar dados
        if (_gameState.value.scoreExceeded) {
            return
        }

        // Si ya está en proceso de lanzamiento o no se puede lanzar, ignorar la acción
        if (_isRolling.value || !_canRoll.value) {
            return
        }

        // Limpiar cualquier mensaje anterior ANTES de iniciar el lanzamiento
        // Esto evita que aparezca brevemente el mensaje del turno anterior
        _message.value = null

        viewModelScope.launch {
            try {
                // Marcar como en proceso de lanzamiento y deshabilitar interacciones
                _isRolling.value = true
                _canRoll.value = false
                _canBank.value = false

                // Verificar si el sonido y la vibración están habilitados
                val soundEnabled = userPreferencesManager.soundEnabled.first()
                val vibrationEnabled = userPreferencesManager.vibrationEnabled.first()

                // Reproducir sonido si está habilitado
                if (soundEnabled) {
                    playDiceRollSound()
                }

                // Vibrar si está habilitado
                if (vibrationEnabled) {
                    vibrate()
                }

                val currentGame = gameStateUseCase.getGameState(gameId).first()
                if (currentGame != null) {
                    val currentPlayer = currentGame.currentPlayer
                    if (currentPlayer != null) {
                        // Verificar si el jugador está en juego
                        val isPlayerInGame = _playersInGame.value[currentPlayer.id] ?: false

                        // Si es el primer lanzamiento (no hay dados) o todos los dados están puntuados
                        // o todos los dados están bloqueados
                        if (_dice.value.isEmpty() || _allDiceScored.value || (_dice.value.isNotEmpty() && _dice.value.all { it.isLocked })) {
                            // Guardar la puntuación acumulada antes de reiniciar los dados
                            val currentTurnScore = _currentTurnScore.value

                            // Reiniciar todos los dados para un nuevo lanzamiento
                            _dice.value = emptyList()
                            _selectedDice.value = emptyList()
                            _lockedDice.value = emptyList()
                            _dice.value = rollDiceUseCase.createNewDiceSet()
                            _allDiceScored.value = false

                            if (_dice.value.isEmpty()) {
                                // Mensaje de error (esencial)
                                setEssentialMessage("Error al crear dados. Intenta de nuevo.")
                                _isRolling.value = false
                                _canRoll.value = true
                                return@launch
                            }

                            // No mostrar mensajes informativos sobre el estado del juego
                            // La UI ya muestra claramente el estado actual
                            _gameStarted.value = true
                        } else {
                            // Si hay dados seleccionados, bloquearlos antes de lanzar
                            lockSelectedDice()
                        }

                        // Obtener dados que no están bloqueados
                        val unlockedDice = _dice.value.filter { !it.isLocked }
                        if (unlockedDice.isEmpty()) {
                            _canRoll.value = true
                            return@launch
                        }

                        // Lanzar dados no bloqueados
                        val rolledDice = rollDiceUseCase(unlockedDice)

                        // Simular animación de lanzamiento (reducir el tiempo para mejorar la experiencia)
                        delay(300)

                        // Actualizar estado de los dados
                        val updatedDiceState = _dice.value.map { dice ->
                            val rolledDie = rolledDice.find { it.id == dice.id }
                            if (rolledDie != null) {
                                dice.copy(value = rolledDie.value, isSelected = false)
                            } else {
                                dice
                            }
                        }

                        _dice.value = updatedDiceState

                        // Verificar si hay dados con puntuación
                        val hasScoringDice =
                            validateTurnUseCase.hasScoringDice(updatedDiceState.filter { !it.isLocked })

                        if (!hasScoringDice) {
                            // No hay dados con puntuación, el jugador pierde el turno (mensaje esencial)
                            setEssentialMessage(context.getString(R.string.no_score))

                            // Mantener deshabilitados los controles
                            _canRoll.value = false
                            _canBank.value = false

                            // Reducir el delay para mejorar la experiencia
                            delay(1000)

                            // Finalizar la animación de lanzamiento antes de pasar al siguiente jugador
                            _isRolling.value = false

                            nextPlayer()
                        } else {
                            // Seleccionar automáticamente los dados con puntuación
                            autoSelectScoringDice()

                            // Finalizar la animación de lanzamiento
                            _isRolling.value = false
                        }
                    }
                }
            } catch (e: Exception) {
                // Manejar errores (mensaje esencial)
                setEssentialMessage("Error al lanzar dados: ${e.message}")
                Log.e("GameViewModel", "Error en onRollClick: ${e.message}", e)

                // Asegurar que se restaure la interactividad en caso de error
                _isRolling.value = false
                _canRoll.value = true
            }
        }
    }

    /**
     * Selecciona automáticamente los dados que puntúan
     */
    private fun autoSelectScoringDice() {
        val unlockedDice = _dice.value.filter { !it.isLocked }
        if (unlockedDice.isEmpty()) return

        // Encontrar la mejor combinación de dados que puntúan
        val bestScoringDice = findBestScoringDice(unlockedDice)

        if (bestScoringDice.isNotEmpty()) {
            // Actualizar los dados para marcar los seleccionados, pero solo modificar
            // el estado de los dados no bloqueados
            val updatedDice = _dice.value.map { dice ->
                // Solo cambiar el estado de los dados no bloqueados
                if (!dice.isLocked) {
                    // Si es uno de los mejores dados que puntúan, marcarlo como seleccionado
                    if (bestScoringDice.any { it.id == dice.id }) {
                        dice.copy(isSelected = true)
                    } else {
                        dice.copy(isSelected = false)
                    }
                } else {
                    // Mantener el estado de los dados bloqueados sin cambios
                    dice
                }
            }

            _dice.value = updatedDice

            // Actualizar la lista de dados seleccionados para que contenga
            // solo los dados seleccionados actualmente (incluyendo los que ya estaban seleccionados)
            _selectedDice.value = updatedDice.filter { it.isSelected }

            // Calcular y actualizar la puntuación
            val (newSelectionScore, description) = calculateScoreUseCase(_selectedDice.value)

            // Calcular puntuación de los dados ya bloqueados
            val lockedDiceScore = calculateScoreUseCase(_lockedDice.value).first

            // Actualizar la puntuación total del turno
            // Nota: No reiniciamos la puntuación, solo sumamos la nueva puntuación
            _currentTurnScore.value =
                _currentTurnScore.value - lockedDiceScore + lockedDiceScore + newSelectionScore

            // Actualizar puntuación proyectada
            updateProjectedScore(_currentTurnScore.value)

            // Verificar si todos los dados disponibles han puntuado
            val allUnlockedSelected = unlockedDice.size == bestScoringDice.size

            // Actualizar el estado de allDiceScored
            _allDiceScored.value = allUnlockedSelected

            // Actualizar mensaje con la descripción de la puntuación
            _message.value = if (allUnlockedSelected && unlockedDice.size == 6) {
                "¡Todos los dados puntúan! Puedes lanzar de nuevo con todos los dados. Puntuación acumulada: ${_currentTurnScore.value}"
            } else if (allUnlockedSelected) {
                "¡Todos los dados disponibles puntúan! Puedes lanzar de nuevo. Puntuación acumulada: ${_currentTurnScore.value}"
            } else {
                description
            }

            // Verificar si el jugador está en juego o si ha alcanzado los 500 puntos mínimos
            val currentPlayer = _gameState.value.currentPlayer
            val isPlayerInGame = currentPlayer?.let { _playersInGame.value[it.id] } ?: true

            // Solo permitir bancar si el jugador ya está en juego o si ha alcanzado 500 puntos
            _canBank.value = if (!isPlayerInGame) {
                // Si no está en juego, verificar si ha alcanzado 500 puntos
                newSelectionScore > 0 && _currentTurnScore.value >= 500
            } else {
                // Si ya está en juego, solo verificar que haya puntuación
                newSelectionScore > 0
            }

            // Siempre permitir lanzar si hay puntuación
            _canRoll.value = true
        }
    }

    /**
     * Encuentra la mejor combinación de dados que puntúan
     * @param availableDice Lista de dados disponibles para seleccionar
     * @return Lista de dados que forman la mejor combinación
     */
    private fun findBestScoringDice(availableDice: List<Dice>): List<Dice> {
        if (availableDice.isEmpty()) return emptyList()

        // Estrategia: probar diferentes combinaciones y quedarse con la mejor
        val scoringDice = mutableListOf<Dice>()

        // 1. Verificar si hay una escalera (1-2-3-4-5-6)
        if (availableDice.size == 6) {
            val values = availableDice.map { it.value }.toSet()
            if (values.size == 6 && values.containsAll(listOf(1, 2, 3, 4, 5, 6))) {
                return availableDice
            }
        }

        // 2. Verificar si hay tres pares
        if (availableDice.size == 6) {
            val valueCounts = availableDice.groupBy { it.value }
                .mapValues { it.value.size }

            if (valueCounts.size == 3 && valueCounts.values.all { it == 2 }) {
                return availableDice
            }
        }

        // Agrupar dados por valor
        val diceByValue = availableDice.groupBy { it.value }

        // 3. Buscar grupos de dados (6, 5, 4, 3 iguales)
        // Primero procesamos los grupos más grandes
        for (count in 6 downTo 3) {
            diceByValue.forEach { (value, diceList) ->
                if (diceList.size >= count) {
                    // Añadir solo la cantidad exacta de dados para formar el grupo
                    scoringDice.addAll(diceList.take(count))

                    // Si hay más dados del mismo valor, los procesaremos después
                    // como dados individuales si son 1 o 5
                }
            }
        }

        // 4. Añadir dados individuales que puntúan (1 y 5)
        // Primero procesamos los 1's (valen más)
        val remainingDice = availableDice.filter { it !in scoringDice }

        // Añadir todos los 1's individuales (100 puntos cada uno)
        remainingDice.filter { it.value == 1 }.forEach { die ->
            // Verificar si ya tenemos 3 o más dados con valor 1
            val existingOnesCount = scoringDice.count { it.value == 1 }

            // Si ya tenemos 3 o más, no añadir más (ya están contados como grupo)
            if (existingOnesCount < 3) {
                scoringDice.add(die)
            }
        }

        // Añadir todos los 5's individuales (50 puntos cada uno)
        remainingDice.filter { it.value == 5 }.forEach { die ->
            // Verificar si ya tenemos 3 o más dados con valor 5
            val existingFivesCount = scoringDice.count { it.value == 5 }

            // Si ya tenemos 3 o más, no añadir más (ya están contados como grupo)
            if (existingFivesCount < 3) {
                scoringDice.add(die)
            }
        }

        // Si no encontramos ningún dado que puntúe, intentar con una estrategia diferente
        if (scoringDice.isEmpty()) {
            // Buscar primero todos los 1's y 5's
            val ones = availableDice.filter { it.value == 1 }
            val fives = availableDice.filter { it.value == 5 }

            scoringDice.addAll(ones)
            scoringDice.addAll(fives)

            // Si aún no hay dados que puntúen, buscar cualquier grupo de 3 o más
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
        // Si se ha excedido la puntuación, pasar al siguiente jugador
        if (_gameState.value.scoreExceeded) {
            onNextPlayerClick()
            return
        }

        viewModelScope.launch {
            if (!_canBank.value) return@launch

            // Deshabilitar interacciones mientras se procesa
            _canRoll.value = false
            _canBank.value = false

            val currentGame = gameStateUseCase.getGameState(gameId).first()
            if (currentGame != null) {
                val currentPlayer = currentGame.currentPlayer
                if (currentPlayer != null) {
                    // Verificar si el jugador ya está en juego o si ha alcanzado los 500 puntos mínimos
                    val isPlayerInGame = _playersInGame.value[currentPlayer.id] ?: false
                    val currentTurnScore = _currentTurnScore.value

                    if (!isPlayerInGame && currentTurnScore < 500) {
                        // El jugador no ha alcanzado los 500 puntos mínimos
                        setEssentialMessage(context.getString(R.string.minimum_points_required))

                        // Pierde el turno y los puntos acumulados
                        delay(1000) // Reducir el delay para mejorar la experiencia
                        nextPlayer()
                        return@launch
                    }

                    // Si el jugador no estaba en juego pero ha alcanzado los 500 puntos, actualizamos su estado
                    if (!isPlayerInGame && currentTurnScore >= 500) {
                        val updatedPlayersInGame = _playersInGame.value.toMutableMap()
                        updatedPlayersInGame[currentPlayer.id] = true
                        _playersInGame.value = updatedPlayersInGame

                        // Este mensaje no es esencial, no lo mostramos
                        // Reducir el delay para mejorar la experiencia
                        delay(500)
                    }

                    // Calcular puntuación total
                    val previousScore = _playerScores.value[currentPlayer.id] ?: 0
                    val totalScore = previousScore + _currentTurnScore.value

                    // Verificar si el jugador alcanza exactamente o supera la puntuación objetivo
                    if (totalScore == currentGame.game.targetScore) {
                        // ¡Victoria! El jugador ha alcanzado exactamente 10,000 puntos
                        try {
                            // Actualizar inmediatamente las puntuaciones en la UI para feedback instantáneo
                            val updatedScores = _playerScores.value.toMutableMap()
                            updatedScores[currentPlayer.id] = totalScore
                            _playerScores.value = updatedScores
                            
                            // Actualizar el estado del juego con la nueva puntuación
                            val gameStateData = currentGame.additionalState.toMutableMap()
                            gameStateData["playerScore_${currentPlayer.id}"] = totalScore
                            gameStateData["playerInGame_${currentPlayer.id}"] = true
                            
                            // Guardar puntuación del turno (operación asíncrona)
                            saveGameUseCase.saveScore(
                                gameId = gameId,
                                playerId = currentPlayer.id,
                                round = currentGame.currentRound,
                                turnScore = _currentTurnScore.value,
                                totalScore = totalScore,
                                diceRolls = 0 // Implementar contador de lanzamientos
                            )
                            
                            // Completar el juego con el ganador
                            saveGameUseCase.completeGame(gameId, currentPlayer.id)

                            // Actualizar estado de UI inmediatamente
                            _gameState.update { currentState ->
                                currentState.copy(
                                    isGameOver = true,
                                    winner = currentPlayer,
                                    canRoll = false, // Deshabilitar el botón de lanzar dados
                                    canBank = true   // Mantener habilitado el botón de fin de juego
                                )
                            }

                            // Mensaje de victoria (mensaje esencial)
                            setEssentialMessage("¡${currentPlayer.name} ha ganado con $totalScore puntos!")

                            // Reproducir sonido de victoria si está habilitado
                            val soundEnabled = userPreferencesManager.soundEnabled.first()
                            if (soundEnabled) {
                                playWinSound()
                            }

                            // No pasar al siguiente jugador, el juego ha terminado
                            return@launch
                        } catch (e: Exception) {
                            // Manejar errores (mensaje esencial)
                            setEssentialMessage("Error al completar el juego: ${e.message}")
                            Log.e("GameViewModel", "Error al completar el juego: ${e.message}", e)
                        }
                    } else if (totalScore > currentGame.game.targetScore) {
                        // Mostrar mensaje de puntuación excedida (mensaje esencial)
                        setEssentialMessage(context.getString(R.string.score_exceeded))

                        // Pierde el turno y los puntos acumulados
                        delay(1000) // Reducir el delay para mejorar la experiencia
                        nextPlayer()
                        return@launch
                    }

                    try {
                        // Actualizar inmediatamente las puntuaciones en la UI para feedback instantáneo
                        val updatedScores = _playerScores.value.toMutableMap()
                        updatedScores[currentPlayer.id] = totalScore
                        _playerScores.value = updatedScores

                        // Mostrar mensaje de puntuación guardada (mensaje esencial)
                        setEssentialMessage("${currentPlayer.name} ha guardado ${_currentTurnScore.value} puntos")

                        // Actualizar el estado del juego con la nueva puntuación
                        val gameStateData = currentGame.additionalState.toMutableMap()
                        gameStateData["playerScore_${currentPlayer.id}"] = totalScore
                        gameStateData["playerInGame_${currentPlayer.id}"] = true

                        // Guardar puntuación del turno (operación asíncrona)
                        saveGameUseCase.saveScore(
                            gameId = gameId,
                            playerId = currentPlayer.id,
                            round = currentGame.currentRound,
                            turnScore = _currentTurnScore.value,
                            totalScore = totalScore,
                            diceRolls = 0 // Implementar contador de lanzamientos
                        )

                        // Verificar si el jugador ha ganado
                        if (validateTurnUseCase.hasWon(totalScore, currentGame.game.targetScore)) {
                            // Completar el juego con el ganador
                            saveGameUseCase.completeGame(gameId, currentPlayer.id)

                            // Actualizar estado de UI inmediatamente
                            _gameState.update { currentState ->
                                currentState.copy(
                                    isGameOver = true,
                                    winner = currentPlayer,
                                    canRoll = false, // Deshabilitar el botón de lanzar dados
                                    canBank = true   // Mantener habilitado el botón de fin de juego
                                )
                            }

                            // Mensaje de victoria (mensaje esencial)
                            setEssentialMessage("¡${currentPlayer.name} ha ganado con $totalScore puntos!")

                            // Reproducir sonido de victoria si está habilitado
                            val soundEnabled = userPreferencesManager.soundEnabled.first()
                            if (soundEnabled) {
                                playWinSound()
                            }

                            // No pasar al siguiente jugador, el juego ha terminado
                            // El usuario debe hacer clic en el botón "Fin del juego" para volver a la pantalla de inicio
                            return@launch
                        } else {
                            // Guardar estado del juego
                            saveGameUseCase(
                                gameId = gameId,
                                currentPlayerIndex = currentGame.currentPlayerIndex,
                                currentRound = currentGame.currentRound,
                                gameStateData = gameStateData
                            )

                            // Breve pausa para que el usuario vea el mensaje
                            delay(1000) // Reducir el delay para mejorar la experiencia

                            // Pasar al siguiente jugador
                            nextPlayer()
                        }
                    } catch (e: Exception) {
                        // Manejar errores (mensaje esencial)
                        setEssentialMessage("Error al guardar puntuación: ${e.message}")
                        Log.e("GameViewModel", "Error en onBankClick: ${e.message}", e)

                        // Restaurar la interactividad
                        _canRoll.value = true
                        _canBank.value = true
                    }
                }
            }
        }
    }

    private fun nextPlayer() {
        viewModelScope.launch {
            // Deshabilitar interacciones mientras se cambia de jugador
            _canRoll.value = false
            _canBank.value = false

            // NO limpiar el mensaje aquí - dejar que GameMessageHandler lo gestione
            // El GameMessageHandler auto-oculta mensajes después de 3 segundos
            // y llama a clearMessage() automáticamente

            val currentGame = gameStateUseCase.getGameState(gameId).first()
            if (currentGame != null) {
                val nextPlayerIndex =
                    (currentGame.currentPlayerIndex + 1) % currentGame.players.size
                val nextRound =
                    if (nextPlayerIndex == 0) currentGame.currentRound + 1 else currentGame.currentRound
                val nextPlayer = currentGame.players.getOrNull(nextPlayerIndex)

                // Reiniciar el estado de los dados inmediatamente para evitar interacciones
                _dice.value = emptyList()
                _selectedDice.value = emptyList()
                _lockedDice.value = emptyList()
                _currentTurnScore.value = 0
                _allDiceScored.value = false
                _gameStarted.value = false

                // Actualizar el estado del juego en la UI inmediatamente con el nuevo jugador
                // para evitar que se muestre información del jugador anterior
                _gameState.update { currentState ->
                    currentState.copy(
                        currentPlayerIndex = nextPlayerIndex,
                        currentRound = nextRound,
                        currentPlayer = nextPlayer,
                        scoreExceeded = false // Resetear el estado de exceso de puntuación
                    )
                }

                // Guardar el estado del juego con el siguiente jugador
                try {
                    saveGameUseCase(
                        gameId = gameId,
                        currentPlayerIndex = nextPlayerIndex,
                        currentRound = nextRound,
                        gameStateData = currentGame.additionalState
                    )

                    // No mostrar mensajes de cambio de turno, son innecesarios
                    // y la UI ya indica claramente de quién es el turno

                    // Verificar PRIMERO si es el turno del Bot (esto deshabilitará controles si es bot)
                    nextPlayer?.let { checkIfBotTurn(it) }

                    // Solo habilitar el botón si NO es turno del bot
                    if (!_isBotTurn.value) {
                        _canRoll.value = true
                    }
                } catch (e: Exception) {
                    // Manejar errores (mensaje esencial)
                    setEssentialMessage("Error al cambiar de jugador: ${e.message}")
                    Log.e("GameViewModel", "Error en nextPlayer: ${e.message}", e)

                    // Asegurar que el usuario pueda interactuar incluso si hay un error
                    // pero solo si no es turno del bot
                    if (!_isBotTurn.value) {
                        _canRoll.value = true
                    }
                }
            }
        }
    }

    fun clearMessage() {
        // Limpiar el StateFlow _message que es la fuente real del mensaje
        // (no _gameState.message que se sobrescribe por el combine)
        _message.value = null
    }

    /**
     * Reproduce el sonido de lanzamiento de dados
     */
    private fun playDiceRollSound() {
        try {
            diceRollSound?.let {
                if (it.isPlaying) {
                    it.stop()
                    it.prepare()
                }
                it.start()
            } ?: run {
                initSounds()
                diceRollSound?.start()
            }
        } catch (e: Exception) {
            Log.e("GameViewModel", "Error al reproducir sonido de dados: ${e.message}")
        }
    }

    /**
     * Reproduce el sonido de victoria
     */
    private fun playWinSound() {
        try {
            winSound?.let {
                if (it.isPlaying) {
                    it.stop()
                    it.prepare()
                }
                it.start()
            } ?: run {
                initSounds()
                winSound?.start()
            }
        } catch (e: Exception) {
            Log.e("GameViewModel", "Error al reproducir sonido de victoria: ${e.message}")
        }
    }

    /**
     * Activa la vibración del dispositivo
     */
    private fun vibrate() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
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

    /**
     * Método para limpiar recursos y eliminar el bot cuando el usuario sale de la partida
     */
    fun exitGame() {
        viewModelScope.launch {
            try {
                // Si es modo individual y tenemos un ID de bot válido, eliminarlo
                if (_isSinglePlayerMode.value && botPlayerId > 0) {
                    Log.d("GameViewModel", "Eliminando bot al salir de la partida, ID: $botPlayerId")
                    
                    // Eliminar el bot de la base de datos
                    saveGameUseCase.deleteBotPlayer(botPlayerId)
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error al eliminar bot: ${e.message}", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        
        // Eliminar el bot si es necesario
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
        
        // Liberar recursos de MediaPlayer
        diceRollSound?.release()
        diceRollSound = null

        winSound?.release()
        winSound = null
    }

    /**
     * Método que se llama cuando un jugador excede la puntuación límite de 10,000 puntos.
     * Muestra un mensaje, reinicia la puntuación del turno actual y pasa al siguiente jugador.
     */
    fun onScoreExceeded() {
        // Usar _message.value (fuente única de verdad para mensajes)
        _message.value = "¡Has superado los 10,000 puntos! Pierdes tu turno y los puntos acumulados."

        // Añadimos un pequeño retraso antes de pasar al siguiente jugador
        viewModelScope.launch {
            delay(500) // Pequeño retraso para asegurar que el mensaje se muestra

            _gameState.update { currentState ->
                val nextPlayerIndex =
                    (currentState.currentPlayerIndex + 1) % currentState.players.size
                currentState.copy(
                    currentTurnScore = 0,
                    currentPlayerIndex = nextPlayerIndex,
                    dice = resetDice(),
                    gameStarted = false,
                    canRoll = true,
                    canBank = false,
                    selectedDiceChanged = false // Resetear el flag
                )
            }
        }
    }

    /**
     * Reinicia los dados a su estado inicial.
     */
    private fun resetDice(): List<Dice> {
        return List(6) { index ->
            Dice(
                id = index,
                value = 1, // Valor inicial, se cambiará al lanzar
                isSelected = false,
                isLocked = false
            )
        }
    }

    fun resetSelectedDiceChangedFlag() {
        _gameState.update { currentState ->
            currentState.copy(
                selectedDiceChanged = false
            )
        }
    }

    /**
     * Establece el estado de puntuación excedida
     */
    fun setScoreExceeded(exceeded: Boolean) {
        _gameState.update { currentState ->
            currentState.copy(
                scoreExceeded = exceeded,
                // Si se excede, deshabilitar la posibilidad de lanzar dados
                canRoll = if (exceeded) false else currentState.canRoll
            )
        }
    }

    /**
     * Muestra el mensaje de puntuación excedida
     */
    fun showScoreExceededMessage() {
        // Usar _message.value (fuente única de verdad para mensajes)
        _message.value = "¡Has superado los 10,000 puntos! Haz clic en 'Pasar Turno'."
    }

    /**
     * Método para manejar el clic en "Pasar Turno" cuando se excede la puntuación
     */
    fun onNextPlayerClick() {
        // Bloquear interacciones durante la transición
        blockInteractionsDuringTransition()

        // Limpiar mensaje (fuente única de verdad)
        _message.value = null

        // Pasar al siguiente jugador
        nextPlayer()

        // Restablecer el estado de exceso de puntuación
        _gameState.update { currentState ->
            currentState.copy(
                scoreExceeded = false,
                currentTurnScore = 0
            )
        }
    }

    /**
     * Bloquea temporalmente las interacciones del usuario durante las transiciones
     * para evitar acciones no deseadas mientras se actualiza el estado del juego.
     */
    private fun blockInteractionsDuringTransition(durationMs: Long = 500) {
        viewModelScope.launch {
            // Deshabilitar interacciones
            _canRoll.value = false
            _canBank.value = false

            // Esperar el tiempo especificado
            delay(durationMs)

            // Restaurar interacciones según el estado actual del juego
            // IMPORTANTE: No restaurar si es el turno del bot
            if (!_isRolling.value && !_gameState.value.scoreExceeded && !_isBotTurn.value) {
                _canRoll.value = true

                // Solo habilitar el banco si hay dados seleccionados con puntuación
                val (canBankByDice, _) = validateTurnUseCase(_selectedDice.value)
                _canBank.value = canBankByDice && _currentTurnScore.value > 0
            }
        }
    }

    /**
     * Establece un mensaje solo si es esencial para la experiencia del usuario
     * Filtra mensajes no esenciales para evitar sobrecarga de información
     */
    private fun setEssentialMessage(message: String?) {
        if (message == null) {
            _message.value = null
            return
        }
        
        // Lista de palabras clave para mensajes esenciales
        val essentialKeywords = listOf(
            "guardado", "puntos",
            "perdido el turno", "sin puntuación",
            "superado los 10,000",
            "ganado",
            "error", "Error"
        )

        // Solo establecer el mensaje si es esencial
        if (essentialKeywords.any { message.contains(it, ignoreCase = true) }) {
            _message.value = message
        }
    }
}
