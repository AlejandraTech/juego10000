package com.bigotitech.rokub10000.ui.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.domain.model.Dice
import com.bigotitech.rokub10000.ui.common.components.indicator.GameIndicator
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions

/**
 * Sección principal que muestra los dados del juego en una cuadrícula de 2x3.
 * Incluye indicadores visuales para estados especiales del turno.
 *
 * @param dice Lista de dados a mostrar
 * @param onDiceClick Callback cuando se hace click en un dado
 * @param isRolling Si los dados están en animación de lanzamiento
 * @param modifier Modificador opcional
 * @param showTurnLostIndicator Si mostrar indicador de turno perdido
 * @param showPointsSavedIndicator Si mostrar indicador de puntos guardados
 * @param showScoreExceededIndicator Si mostrar indicador de puntuación excedida
 */
@Composable
fun DiceSection(
    dice: List<Dice>,
    onDiceClick: (Dice) -> Unit,
    isRolling: Boolean,
    modifier: Modifier = Modifier,
    showTurnLostIndicator: Boolean = false,
    showPointsSavedIndicator: Boolean = false,
    showScoreExceededIndicator: Boolean = false
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DiceGrid(
            dice = dice,
            onDiceClick = onDiceClick,
            isRolling = isRolling
        )

        TurnIndicators(
            showTurnLostIndicator = showTurnLostIndicator,
            showPointsSavedIndicator = showPointsSavedIndicator,
            showScoreExceededIndicator = showScoreExceededIndicator
        )
    }
}

@Composable
private fun DiceGrid(
    dice: List<Dice>,
    onDiceClick: (Dice) -> Unit,
    isRolling: Boolean,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (dice.isNotEmpty()) {
            val firstRow = dice.take(3)
            val secondRow = dice.drop(3)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                firstRow.forEach { die ->
                    DiceView(
                        dice = die,
                        onClick = { onDiceClick(die) },
                        isRolling = isRolling
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.diceSpacing))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                secondRow.forEach { die ->
                    DiceView(
                        dice = die,
                        onClick = { onDiceClick(die) },
                        isRolling = isRolling
                    )
                }
            }
        }
    }
}

@Composable
private fun TurnIndicators(
    showTurnLostIndicator: Boolean,
    showPointsSavedIndicator: Boolean,
    showScoreExceededIndicator: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        GameIndicator(
            visible = showTurnLostIndicator,
            icon = Icons.Default.Close,
            title = stringResource(R.string.turn_lost_title),
            message = stringResource(R.string.turn_lost_message),
            isError = true
        )

        GameIndicator(
            visible = showPointsSavedIndicator,
            icon = Icons.Default.Check,
            title = stringResource(R.string.points_saved_title),
            isError = false
        )

        GameIndicator(
            visible = showScoreExceededIndicator,
            icon = Icons.Default.Warning,
            title = stringResource(R.string.score_exceeded_title),
            message = stringResource(R.string.score_exceeded_message),
            detailMessage = stringResource(R.string.score_exceeded_detail),
            isError = true
        )
    }
}
