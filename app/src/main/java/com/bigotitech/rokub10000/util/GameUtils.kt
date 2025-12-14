package com.bigotitech.rokub10000.util

import com.bigotitech.rokub10000.domain.model.Dice

/**
 * Utilidad para operaciones relacionadas con el juego 10000.
 * 
 * Este objeto proporciona funcionalidades para manipular dados, calcular puntuaciones
 * y verificar combinaciones válidas.
 */
object GameUtils {
    /**
     * Genera valores aleatorios para los dados no bloqueados.
     * 
     * @param diceList Lista de dados a procesar
     * @return Lista de dados con nuevos valores aleatorios para los no bloqueados
     */
    fun rollDice(diceList: List<Dice>): List<Dice> {
        return diceList.map { dice ->
            if (!dice.isLocked) {
                dice.copy(value = (1..6).random())
            } else {
                dice
            }
        }
    }

    /**
     * Calcula la puntuación basada en los dados seleccionados.
     * 
     * @param selectedDice Lista de dados seleccionados para calcular la puntuación
     * @return Puntuación total calculada según las reglas del juego
     */
    fun calculateScore(selectedDice: List<Dice>): Int {
        if (selectedDice.isEmpty()) return 0

        val values = selectedDice.map { it.value }
        return calculateScoreFromValues(values)
    }

    /**
     * Calcula la puntuación basada en los valores de los dados.
     * 
     * @param diceValues Lista de valores de los dados (del 1 al 6)
     * @return Puntuación total calculada según las reglas del juego
     */
    fun calculateScoreFromValues(diceValues: List<Int>): Int {
        if (diceValues.isEmpty()) return 0

        var score = 0

        // Verificar escalera (1-2-3-4-5-6)
        if (diceValues.size == 6 && diceValues.sorted() == listOf(1, 2, 3, 4, 5, 6)) {
            return 1500
        }

        // Verificar tres pares
        if (diceValues.size == 6) {
            val grouped = diceValues.groupBy { it }.filter { it.value.size == 2 }
            if (grouped.size == 3) {
                return 1500
            }
        }

        // Contar ocurrencias de cada valor
        val valueCounts = diceValues.groupBy { it }.mapValues { it.value.size }

        // Calcular puntuación para cada grupo de dados
        valueCounts.forEach { (value, count) ->
            when {
                // Seis iguales (cuádruple del valor de tres iguales)
                count == 6 -> {
                    score += when (value) {
                        1 -> 4000
                        else -> value * 400
                    }
                }
                // Cinco iguales (triple del valor de tres iguales)
                count == 5 -> {
                    score += when (value) {
                        1 -> 3000
                        else -> value * 300
                    }
                }
                // Cuatro iguales (doble del valor de tres iguales)
                count == 4 -> {
                    score += when (value) {
                        1 -> 2000
                        else -> value * 200
                    }
                }
                // Tres iguales
                count == 3 -> {
                    score += when (value) {
                        1 -> 1000
                        else -> value * 100
                    }
                }
                // Dados individuales (solo 1 y 5 tienen valor)
                else -> {
                    if (value == 1) {
                        score += 100 * count
                    } else if (value == 5) {
                        score += 50 * count
                    }
                }
            }
        }

        return score
    }

    /**
     * Verifica si hay alguna combinación válida en los dados no bloqueados.
     * 
     * @param diceList Lista de dados a verificar
     * @return true si existe al menos una combinación válida, false en caso contrario
     */
    fun hasValidCombination(diceList: List<Dice>): Boolean {
        val unlockedDice = diceList.filter { !it.isLocked }
        if (unlockedDice.isEmpty()) return false

        val values = unlockedDice.map { it.value }
        return hasValidCombinationFromValues(values)
    }

    /**
     * Verifica si hay alguna combinación válida en los valores de dados proporcionados.
     * 
     * @param diceValues Lista de valores de dados a verificar
     * @return true si existe al menos una combinación válida, false en caso contrario
     */
    fun hasValidCombinationFromValues(diceValues: List<Int>): Boolean {
        if (diceValues.isEmpty()) return false

        // Verificar si hay al menos un 1 o un 5
        if (diceValues.contains(1) || diceValues.contains(5)) return true

        // Verificar si hay al menos tres dados iguales
        val valueCounts = diceValues.groupBy { it }.mapValues { it.value.size }
        if (valueCounts.any { it.value >= 3 }) return true

        // Verificar escalera
        if (diceValues.size == 6 && diceValues.sorted() == listOf(1, 2, 3, 4, 5, 6)) return true

        // Verificar tres pares
        if (diceValues.size == 6) {
            val grouped = diceValues.groupBy { it }.filter { it.value.size == 2 }
            if (grouped.size == 3) return true
        }

        return false
    }

    /**
     * Verifica si la puntuación es suficiente para entrar en juego.
     * 
     * @param score Puntuación a verificar
     * @return true si la puntuación es igual o mayor a 500 puntos, false en caso contrario
     */
    fun isValidEntryScore(score: Int): Boolean {
        return score >= 500
    }
}
