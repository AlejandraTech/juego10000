package com.bigotitech.juego10000.domain.usecase

import com.bigotitech.juego10000.domain.model.Bot
import com.bigotitech.juego10000.domain.model.BotDifficulty
import com.bigotitech.juego10000.domain.model.Dice
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

        delay(getThinkingTime(bot.difficulty))

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
            } ?: return
        }

        while (continueRolling && !turnLost) {
            val availableDice = dice.count { !it.isLocked }
            continueRolling = bot.shouldContinueRolling(
                currentTurnScore = currentTurnScore,
                totalScore = totalScore,
                opponentMaxScore = opponentMaxScore,
                availableDice = availableDice
            )

            if (!continueRolling) {
                delay(getThinkingTime(bot.difficulty))
                onBankScore(currentTurnScore)
                return
            }

            delay(getThinkingTime(bot.difficulty))

            val rollResult = handleSubsequentRoll(
                dice = dice,
                bot = bot,
                onDiceRolled = onDiceRolled,
                onDiceSelected = onDiceSelected,
                onTurnLost = onTurnLost
            )
            
            if (rollResult == null) {
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
        var updatedDice = rollDiceUseCase(dice)
        onDiceRolled(updatedDice)

        delay(1000)

        val hasScoringDice = validateTurnUseCase.hasScoringDice(updatedDice)
        
        if (!hasScoringDice) {
            onTurnLost()
            return null
        }

        val scoringOptions = DiceUtils.findScoringOptions(updatedDice)
        val selectedDice = bot.selectDice(updatedDice, scoringOptions)

        updatedDice = DiceUtils.updateDiceSelection(updatedDice, selectedDice)
        onDiceSelected(updatedDice)

        val (selectionScore, _) = calculateScoreUseCase(selectedDice)

        delay(800)

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

        val unlockedDice = updatedDice.filter { !it.isLocked }
        if (unlockedDice.isEmpty()) {
            updatedDice = rollDiceUseCase.createNewDiceSet()
            onDiceRolled(updatedDice)
        } else {
            val rolledDice = rollDiceUseCase(unlockedDice)

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

        delay(1000)

        val hasScoringDice = validateTurnUseCase.hasScoringDice(updatedDice.filter { !it.isLocked })
        
        if (!hasScoringDice) {
            delay(800)
            onTurnLost()
            return null
        }

        val scoringOptions = DiceUtils.findScoringOptions(updatedDice.filter { !it.isLocked })
        val selectedDice = bot.selectDice(updatedDice.filter { !it.isLocked }, scoringOptions)

        updatedDice = DiceUtils.updateDiceSelection(updatedDice, selectedDice)
        onDiceSelected(updatedDice)

        val (selectionScore, _) = calculateScoreUseCase(selectedDice)

        delay(800)

        updatedDice = DiceUtils.lockSelectedDice(updatedDice)
        
        return Pair(updatedDice, selectionScore)
    }

    /**
     * Obtiene el tiempo de "pensamiento" según la dificultad
     */
    private fun getThinkingTime(difficulty: BotDifficulty): Long {
        return when (difficulty) {
            BotDifficulty.BEGINNER -> 1500L
            BotDifficulty.INTERMEDIATE -> 1000L
            BotDifficulty.EXPERT -> 700L
        }
    }
}
