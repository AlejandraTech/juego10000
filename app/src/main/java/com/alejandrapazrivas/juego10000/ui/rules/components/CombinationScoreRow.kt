package com.alejandrapazrivas.juego10000.ui.rules.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary
import com.alejandrapazrivas.juego10000.ui.rules.components.base.ScoreRow

/**
 * Fila moderna que muestra la puntuación de una combinación específica.
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
                // Icono con fondo redondeado
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Secondary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dice),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Secondary
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.spaceSmall))

                Text(
                    text = combination,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}
