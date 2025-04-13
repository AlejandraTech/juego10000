package com.alejandrapazrivas.juego10000.domain.usecase

import com.alejandrapazrivas.juego10000.domain.model.Dice

/**
 * Utilidades para manipulación de dados en los casos de uso.
 * Centraliza operaciones comunes sobre dados para evitar duplicación de código.
 */
object DiceUtils {
    /**
     * Actualiza el estado de selección de los dados
     * @param currentDice Lista actual de dados
     * @param diceToSelect Lista de dados a seleccionar
     * @return Lista de dados con los seleccionados marcados
     */
    fun updateDiceSelection(currentDice: List<Dice>, diceToSelect: List<Dice>): List<Dice> {
        return currentDice.map { dice ->
            if (diceToSelect.any { it.id == dice.id } && !dice.isLocked) {
                dice.copy(isSelected = true)
            } else {
                dice
            }
        }
    }

    /**
     * Bloquea los dados seleccionados
     * @param currentDice Lista actual de dados
     * @return Lista de dados con los seleccionados ahora bloqueados
     */
    fun lockSelectedDice(currentDice: List<Dice>): List<Dice> {
        return currentDice.map { dice ->
            if (dice.isSelected) {
                dice.copy(isSelected = false, isLocked = true)
            } else {
                dice
            }
        }
    }

    /**
     * Encuentra todas las posibles combinaciones de dados que puntúan
     * @param availableDice Lista de dados disponibles
     * @return Lista de combinaciones de dados que puntúan
     */
    fun findScoringOptions(availableDice: List<Dice>): List<List<Dice>> {
        if (availableDice.isEmpty()) return emptyList()

        if (!com.alejandrapazrivas.juego10000.util.GameUtils.hasValidCombination(availableDice)) {
            return emptyList()
        }
        
        val options = mutableListOf<List<Dice>>()

        if (availableDice.size == 6) {
            val values = availableDice.map { it.value }.toSet()
            if (values.size == 6 && values.containsAll(listOf(1, 2, 3, 4, 5, 6))) {
                options.add(availableDice)
                return options
            }
        }

        if (availableDice.size == 6) {
            val valueCounts = availableDice.groupBy { it.value }
                .mapValues { it.value.size }
            
            if (valueCounts.size == 3 && valueCounts.values.all { it == 2 }) {
                options.add(availableDice)
                return options
            }
        }

        val diceByValue = availableDice.groupBy { it.value }

        for (count in 6 downTo 3) {
            diceByValue.forEach { (value, diceList) ->
                if (diceList.size >= count) {
                    options.add(diceList.take(count))
                }
            }
        }

        val ones = availableDice.filter { it.value == 1 }
        val fives = availableDice.filter { it.value == 5 }
        
        if (ones.isNotEmpty()) options.add(ones)
        if (fives.isNotEmpty()) options.add(fives)
        
        return options
    }
}
