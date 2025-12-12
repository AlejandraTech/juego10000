package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.stats.StatsViewModel.ScoreWithPlayer

/**
 * Tarjeta que muestra una puntuación en el ranking
 */
@Composable
fun TopScoreCard(
    scoreWithPlayer: ScoreWithPlayer,
    position: Int
) {
    val dimensions = LocalDimensions.current
    // Colores según la posición
    val medalColors = getMedalColors(position)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (position <= 3) dimensions.spaceExtraSmall / 2 else 0.dp,
                color = medalColors.border,
                shape = CardShape
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = medalColors.background),
        elevation = CardDefaults.cardElevation(
            defaultElevation = when (position) {
                1 -> dimensions.spaceSmall
                2 -> dimensions.spaceSmall - dimensions.spaceExtraSmall / 2
                3 -> dimensions.spaceExtraSmall
                else -> dimensions.spaceExtraSmall / 2
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Posición con círculo
            PositionCircle(position, medalColors.medal)

            Spacer(modifier = Modifier.width(dimensions.spaceMedium))

            // Puntuación con estilo grande
            Text(
                text = "${scoreWithPlayer.score.turnScore}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Spacer(modifier = Modifier.width(dimensions.spaceMedium))

            // Información del jugador y la puntuación
            ScoreDetails(scoreWithPlayer)

            // Icono de trofeo para los tres primeros
            if (position <= 3) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trophy),
                    contentDescription = null,
                    tint = medalColors.medal,
                    modifier = Modifier.size(dimensions.spaceExtraLarge)
                )
            }
        }
    }
}

/**
 * Círculo que muestra la posición en el ranking
 */
@Composable
private fun PositionCircle(position: Int, color: Color) {
    val dimensions = LocalDimensions.current
    Box(
        modifier = Modifier
            .size(dimensions.avatarSizeSmall)
            .background(
                color = color,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$position",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * Detalles de la puntuación y el jugador
 */
@Composable
private fun ScoreDetails(scoreWithPlayer: ScoreWithPlayer) {
    val dimensions = LocalDimensions.current
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = scoreWithPlayer.player.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dice),
                contentDescription = null,
                modifier = Modifier.size(dimensions.spaceMedium),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(dimensions.spaceExtraSmall))
            Text(
                text = stringResource(R.string.round_number, scoreWithPlayer.score.round),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = DateFormatUtils.formatShortDate(scoreWithPlayer.score.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Colores para las medallas según la posición
 */
@Composable
private fun getMedalColors(position: Int): MedalColors {
    val defaultBackground = MaterialTheme.colorScheme.surface
    val defaultBorder = MaterialTheme.colorScheme.surfaceVariant
    val defaultMedal = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    
    return when (position) {
        1 -> MedalColors(
            background = Color(0xFFFFF9C4), // Gold background
            border = Color(0xFFFFD700),     // Gold border
            medal = Color(0xFFFFD700)       // Gold medal
        )
        2 -> MedalColors(
            background = Color(0xFFE0E0E0), // Silver background
            border = Color(0xFFC0C0C0),     // Silver border
            medal = Color(0xFFC0C0C0)       // Silver medal
        )
        3 -> MedalColors(
            background = Color(0xFFFFCCBC), // Bronze background
            border = Color(0xFFCD7F32),     // Bronze border
            medal = Color(0xFFCD7F32)       // Bronze medal
        )
        else -> MedalColors(
            background = defaultBackground,
            border = defaultBorder,
            medal = defaultMedal
        )
    }
}

/**
 * Clase de datos para los colores de las medallas
 */
private data class MedalColors(
    val background: Color,
    val border: Color,
    val medal: Color
)
