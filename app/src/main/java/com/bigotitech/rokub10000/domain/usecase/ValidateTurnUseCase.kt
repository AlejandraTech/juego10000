package com.bigotitech.rokub10000.domain.usecase

import com.bigotitech.rokub10000.domain.model.Dice
import com.bigotitech.rokub10000.util.GameUtils
import javax.inject.Inject

/**
 * Caso de uso para validar las acciones del turno de un jugador.
 */
class ValidateTurnUseCase @Inject constructor(
    private val calculateScoreUseCase: CalculateScoreUseCase
) {
    /**
     * Valida si los dados seleccionados tienen puntuación válida
     * @param selectedDice Lista de dados seleccionados
     * @return Par con resultado (true si es válido) y mensaje
     */
    operator fun invoke(selectedDice: List<Dice>): Pair<Boolean, String> {
        if (selectedDice.isEmpty()) {
            return Pair(false, "No hay dados seleccionados")
        }

        val (score, description) = calculateScoreUseCase(selectedDice)

        return if (score > 0) {
            Pair(true, description)
        } else {
            Pair(false, "Selección no válida: no hay puntuación")
        }
    }

    /**
     * Verifica si hay dados con puntuación en la lista proporcionada
     * @param dice Lista de dados para verificar
     * @return true si hay al menos un dado o combinación que puntúa
     */
    fun hasScoringDice(dice: List<Dice>): Boolean {
        if (dice.isEmpty()) return false

        return GameUtils.hasValidCombination(dice)
    }

    /**
     * Verifica si el jugador ha alcanzado la puntuación objetivo
     * @param totalScore Puntuación total del jugador
     * @param targetScore Puntuación objetivo para ganar
     * @return true si el jugador ha ganado
     */
    fun hasWon(totalScore: Int, targetScore: Int): Boolean {
        return totalScore == targetScore
    }
}
