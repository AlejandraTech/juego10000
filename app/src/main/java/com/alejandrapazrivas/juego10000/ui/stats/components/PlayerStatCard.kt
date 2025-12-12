package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.alejandrapazrivas.juego10000.ui.stats.StatsViewModel.PlayerStats

/**
 * Tarjeta que muestra las estadísticas de un jugador con estilo consistente
 */
@Composable
fun PlayerStatCard(
    playerStat: PlayerStats,
    position: Int = 0
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
            // Header: Posición, Avatar, nombre y porcentaje
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicador de posición para podio
                if (isPodium) {
                    PositionBadge(position = position, color = positionColor)
                    Spacer(modifier = Modifier.width(dimensions.spaceSmall))
                }

                // Avatar del jugador
                PlayerAvatar(
                    name = playerStat.player.name,
                    isPodium = isPodium,
                    podiumColor = positionColor
                )

                Spacer(modifier = Modifier.width(dimensions.spaceMedium))

                // Nombre y partidas jugadas
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = playerStat.player.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.games_played_count, playerStat.player.gamesPlayed),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Porcentaje de victorias
                WinPercentage(winRate = playerStat.player.winRate)
            }

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            Divider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = dimensions.spaceExtraSmall)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            // Barra de progreso de victorias
            WinProgressBar(
                wins = playerStat.player.gamesWon,
                total = playerStat.player.gamesPlayed
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            // Stats en tarjetas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
            ) {
                StatItem(
                    icon = R.drawable.ic_trophy,
                    label = stringResource(R.string.games_won),
                    value = playerStat.player.gamesWon.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = R.drawable.ic_dice,
                    label = stringResource(R.string.games_played),
                    value = playerStat.player.gamesPlayed.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PositionBadge(position: Int, color: Color) {
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

@Composable
private fun WinPercentage(winRate: Float) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "${(winRate * 100).toInt()}%",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = ScorePositive
        )
        Text(
            text = stringResource(R.string.win_rate),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WinProgressBar(wins: Int, total: Int) {
    val dimensions = LocalDimensions.current
    val progress = if (total > 0) wins.toFloat() / total else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "win_progress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.victories_count, wins),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$wins/$total",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ScorePositive
            )
        }
        Spacer(modifier = Modifier.height(dimensions.spaceExtraSmall))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.spaceSmall)
                .clip(RoundedCornerShape(dimensions.spaceExtraSmall)),
            color = ScorePositive,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun StatItem(
    icon: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(dimensions.spaceSmall + dimensions.spaceExtraSmall)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationNone)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceExtraSmall))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ScorePositive
            )
        }
    }
}

private fun getPositionColor(position: Int): Color {
    return when (position) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color.Transparent
    }
}
