package com.bigotitech.rokub10000.domain.usecase.game

import com.bigotitech.rokub10000.domain.model.Dice
import com.bigotitech.rokub10000.util.GameUtils
import javax.inject.Inject
import kotlin.random.Random

/**
 * Caso de uso para lanzar dados y crear nuevos conjuntos de dados.
 */
class RollDiceUseCase @Inject constructor() {
    /**
     * Lanza los dados no bloqueados
     * @param currentDice Lista actual de dados
     * @return Lista de dados con nuevos valores para los no bloqueados
     */
    operator fun invoke(currentDice: List<Dice>): List<Dice> {
        val rolledDice = GameUtils.rollDice(currentDice)

        return rolledDice.map { dice ->
            if (!dice.isLocked) {
                dice.copy(isSelected = false)
            } else {
                dice
            }
        }
    }

    /**
     * Crea un nuevo conjunto de dados (6 dados)
     * @return Lista de 6 dados con valores aleatorios
     */
    fun createNewDiceSet(): List<Dice> {
        return (0 until 6).map {
            Dice(id = it, value = Random.nextInt(1, 7))
        }
    }
}
