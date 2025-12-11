package com.alejandrapazrivas.juego10000.ui.rules.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.rules.components.base.ScoreRow

/**
 * Fila que muestra la puntuación de un valor de dado específico.
 * 
 * @param diceValue Valor del dado (1-6)
 * @param points Puntos que otorga ese valor
 */
@Composable
fun DiceScoreRow(diceValue: Int, points: Int) {
    val dimensions = LocalDimensions.current

    ScoreRow(
        points = points,
        leadingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.avatarSizeSmall + dimensions.spaceExtraSmall)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        )
                        .padding(dimensions.spaceSmall - dimensions.spaceExtraSmall),
                    contentAlignment = Alignment.Center
                ) {
                    // Mostrar el dado correspondiente
                    Image(
                        painter = painterResource(
                            id = when (diceValue) {
                                1 -> R.drawable.dice_1
                                2 -> R.drawable.dice_2
                                3 -> R.drawable.dice_3
                                4 -> R.drawable.dice_4
                                5 -> R.drawable.dice_5
                                else -> R.drawable.dice_6
                            }
                        ),
                        contentDescription = "Dado $diceValue",
                        modifier = Modifier.size(dimensions.spaceExtraLarge)
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.spaceSmall + dimensions.spaceExtraSmall))

                Text(
                    text = "Valor $diceValue",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}
