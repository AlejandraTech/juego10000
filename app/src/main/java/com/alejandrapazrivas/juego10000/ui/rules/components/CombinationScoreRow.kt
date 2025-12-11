package com.alejandrapazrivas.juego10000.ui.rules.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
 * Fila que muestra la puntuación de una combinación específica.
 * 
 * @param combination Descripción de la combinación
 * @param points Puntos que otorga esa combinación
 */
@Composable
fun CombinationScoreRow(combination: String, points: Int) {
    val dimensions = LocalDimensions.current

    ScoreRow(
        points = points,
        useGradient = true,
        leadingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.spaceExtraLarge)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        )
                        .padding(dimensions.spaceSmall - dimensions.spaceExtraSmall),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dice),
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.iconSizeSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.spaceSmall + dimensions.spaceExtraSmall))

                Text(
                    text = combination,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}
