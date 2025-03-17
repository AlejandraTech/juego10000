package com.alejandrapazrivas.juego10000.domain.usecase

import com.alejandrapazrivas.juego10000.domain.model.Bot
import com.alejandrapazrivas.juego10000.domain.model.BotDifficulty
import com.alejandrapazrivas.juego10000.domain.model.Dice
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Clase encargada de gestionar el turno del Bot
 * Implementa la lógica para que el Bot simule un jugador real
 */
class BotTurnHandler @Inject constructor(
    private val calculateScoreUseCase: CalculateScoreUseCase,
    private val validateTurnUseCase: ValidateTurnUseCase,
    private val rollDiceUseCase: RollDiceUseCase
) {
    /**
     * Ejecuta el turno del Bot
     * @param bot Instancia del Bot con su dificultad
     * @param currentDice Dados actuales en juego
     * @param totalScore Puntuación total del Bot
     * @param opponentMaxScore Puntuación máxima de los oponentes
     * @param onDiceRolled Callback cuando el Bot lanza los dados
     * @param onDiceSelected Callback cuando el Bot selecciona dados
     * @param onBankScore Callback cuando el Bot guarda puntos
     * @param onTurnLost Callback cuando el Bot pierde el turno
     */
    suspend fun executeBotTurn(
        bot: Bot,
        currentDice: List<Dice>,
        totalScore: Int,
        opponentMaxScore: Int,
        onDiceRolled: (List<Dice>) -> Unit,
        onDiceSelected: (List<Dice>) -> Unit,
        onBankScore: (Int) -> Unit,
        onTurnLost: () -> Unit
    ) {
        var dice = if (currentDice.isEmpty()) rollDiceUseCase.createNewDiceSet() else currentDice
        var lockedDice = dice.filter { it.isLocked }
        var currentTurnScore = calculateScoreUseCase(lockedDice).first
        var continueRolling = true
        var turnLost = false

        // Simular tiempo de "pensamiento" del Bot
        delay(getThinkingTime(bot.difficulty))

        // Primer lanzamiento si no hay dados
        if (dice.all { !it.isLocked }) {
            handleFirstRoll(
                dice = dice,
                bot = bot,
                onDiceRolled = onDiceRolled,
                onDiceSelected = onDiceSelected,
                onTurnLost = onTurnLost
            )?.let { result ->
                dice = result.first
                lockedDice = dice.filter { it.isLocked }
                currentTurnScore = result.second
            } ?: return // Si es null, el bot perdió el turno
        }

        // Bucle principal del turno
        while (continueRolling && !turnLost) {
            // Decidir si seguir lanzando o guardar puntos
            val availableDice = dice.count { !it.isLocked }
            continueRolling = bot.shouldContinueRolling(
                currentTurnScore = currentTurnScore,
                totalScore = totalScore,
                opponentMaxScore = opponentMaxScore,
                availableDice = availableDice
            )

            if (!continueRolling) {
                // El Bot decide guardar puntos
                delay(getThinkingTime(bot.difficulty))
                onBankScore(currentTurnScore)
                return
            }

            // El Bot decide seguir lanzando
            delay(getThinkingTime(bot.difficulty))
            
            // Manejar el lanzamiento subsiguiente
            val rollResult = handleSubsequentRoll(
                dice = dice,
                bot = bot,
                onDiceRolled = onDiceRolled,
                onDiceSelected = onDiceSelected,
                onTurnLost = onTurnLost
            )
            
            if (rollResult == null) {
                // El bot perdió el turno
                turnLost = true
                continue
            }
            
            dice = rollResult.first
            lockedDice = dice.filter { it.isLocked }
            currentTurnScore += rollResult.second
        }
    }

    /**
     * Maneja el primer lanzamiento del turno
     * @return Par con los dados actualizados y la puntuación obtenida, o null si perdió el turno
     */
    private suspend fun handleFirstRoll(
        dice: List<Dice>,
        bot: Bot,
        onDiceRolled: (List<Dice>) -> Unit,
        onDiceSelected: (List<Dice>) -> Unit,
        onTurnLost: () -> Unit
    ): Pair<List<Dice>, Int>? {
        // Lanzar los dados
        var updatedDice = rollDiceUseCase(dice)
        onDiceRolled(updatedDice)
        
        // Simular tiempo para que el usuario vea el lanzamiento
        delay(1000)
        
        // Verificar si hay dados con puntuación
        val hasScoringDice = validateTurnUseCase.hasScoringDice(updatedDice)
        
        if (!hasScoringDice) {
            // El Bot pierde el turno
            onTurnLost()
            return null
        }
        
        // Encontrar la mejor combinación de dados que puntúan
        val scoringOptions = DiceUtils.findScoringOptions(updatedDice)
        val selectedDice = bot.selectDice(updatedDice, scoringOptions)
        
        // Marcar los dados seleccionados
        updatedDice = DiceUtils.updateDiceSelection(updatedDice, selectedDice)
        onDiceSelected(updatedDice)
        
        // Calcular puntuación
        val (selectionScore, _) = calculateScoreUseCase(selectedDice)
        
        // Simular tiempo para que el usuario vea la selección
        delay(800)
        
        // Bloquear los dados seleccionados
        updatedDice = DiceUtils.lockSelectedDice(updatedDice)
        
        return Pair(updatedDice, selectionScore)
    }

    /**
     * Maneja los lanzamientos subsiguientes del turno
     * @return Par con los dados actualizados y la puntuación adicional obtenida, o null si perdió el turno
     */
    private suspend fun handleSubsequentRoll(
        dice: List<Dice>,
        bot: Bot,
        onDiceRolled: (List<Dice>) -> Unit,
        onDiceSelected: (List<Dice>) -> Unit,
        onTurnLost: () -> Unit
    ): Pair<List<Dice>, Int>? {
        var updatedDice = dice
        
        // Lanzar dados no bloqueados
        val unlockedDice = updatedDice.filter { !it.isLocked }
        if (unlockedDice.isEmpty()) {
            // Si todos los dados están bloqueados, crear un nuevo conjunto
            updatedDice = rollDiceUseCase.createNewDiceSet()
            onDiceRolled(updatedDice)
        } else {
            // Lanzar los dados no bloqueados
            val rolledDice = rollDiceUseCase(unlockedDice)
            
            // Actualizar el estado de los dados
            updatedDice = updatedDice.map { dice ->
                val rolledDie = rolledDice.find { it.id == dice.id }
                if (rolledDie != null) {
                    dice.copy(value = rolledDie.value, isSelected = false)
                } else {
                    dice
                }
            }
            
            onDiceRolled(updatedDice)
        }
        
        // Simular tiempo para que el usuario vea el lanzamiento
        delay(1000)
        
        // Verificar si hay dados con puntuación
        val hasScoringDice = validateTurnUseCase.hasScoringDice(updatedDice.filter { !it.isLocked })
        
        if (!hasScoringDice) {
            // El Bot pierde el turno
            delay(800)
            onTurnLost()
            return null
        }
        
        // Encontrar la mejor combinación de dados que puntúan
        val scoringOptions = DiceUtils.findScoringOptions(updatedDice.filter { !it.isLocked })
        val selectedDice = bot.selectDice(updatedDice.filter { !it.isLocked }, scoringOptions)
        
        // Marcar los dados seleccionados
        updatedDice = DiceUtils.updateDiceSelection(updatedDice, selectedDice)
        onDiceSelected(updatedDice)
        
        // Calcular puntuación
        val (selectionScore, _) = calculateScoreUseCase(selectedDice)
        
        // Simular tiempo para que el usuario vea la selección
        delay(800)
        
        // Bloquear los dados seleccionados
        updatedDice = DiceUtils.lockSelectedDice(updatedDice)
        
        return Pair(updatedDice, selectionScore)
    }

    /**
     * Obtiene el tiempo de "pensamiento" según la dificultad
     */
    private fun getThinkingTime(difficulty: BotDifficulty): Long {
        return when (difficulty) {
            BotDifficulty.BEGINNER -> 1500L      // Más lento
            BotDifficulty.INTERMEDIATE -> 1000L  // Velocidad media
            BotDifficulty.EXPERT -> 700L         // Más rápido
        }
    }
}
