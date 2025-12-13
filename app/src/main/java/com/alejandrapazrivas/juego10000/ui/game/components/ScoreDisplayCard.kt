package com.alejandrapazrivas.juego10000.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary

private val AccentColor = Color(0xFFFFD700)

/**
 * Card compacta para mostrar la puntuación actual del turno.
 * Cambia de estilo según si hay puntos acumulados.
 */
@Composable
fun ScoreDisplayCard(
    currentScore: Int,
    scoreScale: Float,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spaceExtraSmall)
            .scale(scoreScale),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (currentScore > 0) 8.dp else 2.dp,
                    shape = CardShape,
                    spotColor = Primary.copy(alpha = 0.3f)
                ),
            shape = CardShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            ScoreContent(currentScore = currentScore)
        }
    }
}

@Composable
private fun ScoreContent(currentScore: Int) {
    val dimensions = LocalDimensions.current
    val hasScore = currentScore > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = if (hasScore) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Primary,
                            Primary.copy(alpha = 0.9f),
                            AccentColor.copy(alpha = 0.6f)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                        )
                    )
                }
            )
            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.current_score),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (hasScore) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "$currentScore",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = if (hasScore) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}
