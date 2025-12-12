package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.ScorePositive
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
    val isPodium = position in 1..3
    val positionColor = getPositionColor(position)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimensions.cardElevation,
                shape = CardShape,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Posición con círculo
                PositionCircle(position, positionColor)

                Spacer(modifier = Modifier.width(dimensions.spaceMedium))

                // Avatar del jugador
                PlayerAvatar(
                    name = scoreWithPlayer.player.name,
                    isPodium = isPodium,
                    podiumColor = positionColor
                )

                Spacer(modifier = Modifier.width(dimensions.spaceMedium))

                // Información del jugador
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scoreWithPlayer.player.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = DateFormatUtils.formatShortDate(scoreWithPlayer.score.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Puntuación grande
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${scoreWithPlayer.score.turnScore}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ScorePositive
                    )
                    Text(
                        text = stringResource(R.string.points),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            Divider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = dimensions.spaceExtraSmall)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            // Detalles adicionales
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ronda
                Surface(
                    shape = RoundedCornerShape(dimensions.spaceMedium),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = dimensions.spaceSmall, vertical = dimensions.spaceExtraSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_dice),
                            contentDescription = null,
                            modifier = Modifier.size(dimensions.spaceMedium),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(dimensions.spaceExtraSmall))
                        Text(
                            text = stringResource(R.string.round_number, scoreWithPlayer.score.round),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Trofeo para podio
                if (isPodium) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trophy),
                        contentDescription = null,
                        tint = positionColor,
                        modifier = Modifier.size(dimensions.spaceLarge)
                    )
                }
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
            .size(dimensions.spaceLarge)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "#$position",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * Avatar del jugador
 */
@Composable
private fun PlayerAvatar(
    name: String,
    isPodium: Boolean,
    podiumColor: Color
) {
    val dimensions = LocalDimensions.current
    val avatarColor = if (isPodium) podiumColor else MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .size(dimensions.avatarSizeMedium)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        avatarColor.copy(alpha = 0.7f),
                        avatarColor.copy(alpha = 0.2f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        avatarColor,
                        avatarColor.copy(alpha = 0.5f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isPodium) Color.White else MaterialTheme.colorScheme.onPrimary
        )
    }
}

/**
 * Colores para las posiciones del podio
 */
private fun getPositionColor(position: Int): Color {
    return when (position) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color(0xFF6B7280) // Gray for others
    }
}
