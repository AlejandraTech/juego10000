package com.alejandrapazrivas.juego10000.ui.rules.components

import androidx.compose.runtime.Composable
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.rules.components.base.RuleCard

/**
 * Tarjeta que muestra el sistema de puntuación del juego.
 */
@Composable
fun ScoringCard() {
    RuleCard(
        icon = R.drawable.ic_stats,
        title = "Sistema de Puntuación"
    ) {
        // Sección de dados individuales
        ScoringSection(
            title = "Dados Individuales:",
            content = {
                DiceScoreRow(diceValue = 1, points = 100)
                DiceScoreRow(diceValue = 5, points = 50)
            }
        )

        // Sección de combinaciones
        ScoringSection(
            title = "Combinaciones:",
            content = {
                CombinationScoreRow(combination = "Tres 1's", points = 1000)
                CombinationScoreRow(combination = "Tres 2's", points = 200)
                CombinationScoreRow(combination = "Tres 3's", points = 300)
                CombinationScoreRow(combination = "Tres 4's", points = 400)
                CombinationScoreRow(combination = "Tres 5's", points = 500)
                CombinationScoreRow(combination = "Tres 6's", points = 600)
                CombinationScoreRow(combination = "Escalera (1-2-3-4-5-6)", points = 1500)
                CombinationScoreRow(combination = "Tres pares", points = 1500)
            }
        )

        // Sección de multiplicadores
        ScoringSection(
            title = "Multiplicadores:",
            content = {
                MultiplierRow(text = "Cuatro iguales: Doble del valor de tres iguales")
                MultiplierRow(text = "Cinco iguales: Triple del valor de tres iguales")
                MultiplierRow(text = "Seis iguales: Cuádruple del valor de tres iguales")
            }
        )
    }
}
