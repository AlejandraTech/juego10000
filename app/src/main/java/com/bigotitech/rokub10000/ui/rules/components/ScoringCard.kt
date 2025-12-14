package com.bigotitech.rokub10000.ui.rules.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.rules.components.base.RuleCard

/**
 * Tarjeta que muestra el sistema de puntuación del juego.
 */
@Composable
fun ScoringCard() {
    RuleCard(
        icon = R.drawable.ic_stats,
        title = stringResource(R.string.scoring_system)
    ) {
        // Sección de dados individuales
        ScoringSection(
            title = stringResource(R.string.individual_dice),
            content = {
                DiceScoreRow(diceValue = 1, points = 100)
                DiceScoreRow(diceValue = 5, points = 50)
            }
        )

        // Sección de combinaciones
        ScoringSection(
            title = stringResource(R.string.combinations),
            content = {
                CombinationScoreRow(combination = stringResource(R.string.three_ones), points = 1000)
                CombinationScoreRow(combination = stringResource(R.string.three_twos), points = 200)
                CombinationScoreRow(combination = stringResource(R.string.three_threes), points = 300)
                CombinationScoreRow(combination = stringResource(R.string.three_fours), points = 400)
                CombinationScoreRow(combination = stringResource(R.string.three_fives), points = 500)
                CombinationScoreRow(combination = stringResource(R.string.three_sixes), points = 600)
                CombinationScoreRow(combination = stringResource(R.string.straight), points = 1500)
                CombinationScoreRow(combination = stringResource(R.string.three_pairs), points = 1500)
            }
        )

        // Sección de multiplicadores
        ScoringSection(
            title = stringResource(R.string.multipliers),
            content = {
                MultiplierRow(text = stringResource(R.string.four_of_a_kind_desc))
                MultiplierRow(text = stringResource(R.string.five_of_a_kind_desc))
                MultiplierRow(text = stringResource(R.string.six_of_a_kind_desc))
            }
        )
    }
}
