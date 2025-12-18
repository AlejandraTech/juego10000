package com.bigotitech.rokub10000.domain.usecase.game

import com.bigotitech.rokub10000.domain.model.Dice
import com.bigotitech.rokub10000.util.GameUtils
import javax.inject.Inject

/**
 * Caso de uso para calcular la puntuación de los dados seleccionados.
 */
class CalculateScoreUseCase @Inject constructor() {

    /**
     * Calcula la puntuación basada en los dados seleccionados
     * @param selectedDice Lista de dados seleccionados para puntuar
     * @return Par con la puntuación calculada y descripción de la combinación
     */
    operator fun invoke(selectedDice: List<Dice>): Pair<Int, String> {
        if (selectedDice.isEmpty()) return Pair(0, "Sin selección")

        val score = GameUtils.calculateScore(selectedDice)

        val description = generateScoreDescription(selectedDice)

        return Pair(score, description)
    }

    /**
     * Genera una descripción textual de la puntuación basada en los dados seleccionados
     * @param selectedDice Lista de dados seleccionados
     * @return Descripción de la combinación de puntuación
     */
    private fun generateScoreDescription(selectedDice: List<Dice>): String {
        if (selectedDice.isEmpty()) return "Sin selección"

        val valueCounts = selectedDice.groupBy { it.value }
            .mapValues { it.value.size }

        if (selectedDice.size == 6 && valueCounts.size == 6) {
            return "Escalera (1-2-3-4-5-6)"
        }

        if (selectedDice.size == 6 && valueCounts.size == 3 &&
            valueCounts.values.all { it == 2 }
        ) {
            return "Tres pares"
        }

        val scoreDescriptions = mutableListOf<String>()

        valueCounts.forEach { (value, count) ->
            when {
                // Seis iguales
                count == 6 -> {
                    val baseScore = if (value == 1) 1000 else value * 100
                    scoreDescriptions.add("Seis ${value}s (${baseScore * 4})")
                }
                // Cinco iguales
                count == 5 -> {
                    val baseScore = if (value == 1) 1000 else value * 100
                    scoreDescriptions.add("Cinco ${value}s (${baseScore * 3})")
                }
                // Cuatro iguales
                count == 4 -> {
                    val baseScore = if (value == 1) 1000 else value * 100
                    scoreDescriptions.add("Cuatro ${value}s (${baseScore * 2})")
                }
                // Tres iguales
                count == 3 -> {
                    val score = if (value == 1) 1000 else value * 100
                    scoreDescriptions.add("Tres ${value}s ($score)")
                }
                // Dados individuales (solo 1 y 5 valen puntos)
                else -> {
                    if (value == 1) {
                        scoreDescriptions.add("$count unos (${count * 100})")
                    } else if (value == 5) {
                        scoreDescriptions.add("$count cincos (${count * 50})")
                    }
                }
            }
        }

        return scoreDescriptions.joinToString(", ")
    }
}
