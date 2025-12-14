package com.bigotitech.rokub10000.ui.game.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.domain.model.BotDifficulty
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary
import com.bigotitech.rokub10000.ui.common.theme.Secondary

/**
 * Fila individual de la tabla de puntuaciones de un jugador.
 * Muestra avatar, nombre, barra de progreso y puntuación.
 *
 * @param player Datos del jugador
 * @param score Puntuación actual del jugador
 * @param isCurrentPlayer Si es el jugador actual
 * @param position Posición en el ranking
 * @param botDifficulty Dificultad del bot (opcional, solo para bots)
 * @param modifier Modificador opcional
 */
@Composable
fun PlayerScoreRow(
    player: Player,
    score: Int,
    isCurrentPlayer: Boolean,
    position: Int,
    botDifficulty: BotDifficulty? = null,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val infiniteTransition = rememberInfiniteTransition(label = "current_player")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val progress = (score / 10000f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .scale(if (isCurrentPlayer) pulseScale else 1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isCurrentPlayer) 3.dp else 0.dp,
                    shape = RoundedCornerShape(dimensions.spaceSmall),
                    spotColor = if (isCurrentPlayer) Primary.copy(alpha = 0.3f) else Color.Transparent
                )
                .background(
                    brush = if (isCurrentPlayer) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.15f),
                                Primary.copy(alpha = 0.08f)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    shape = RoundedCornerShape(dimensions.spaceSmall)
                )
                .then(
                    if (isCurrentPlayer) {
                        Modifier.border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Primary, Primary.copy(alpha = 0.5f))
                            ),
                            shape = RoundedCornerShape(dimensions.spaceSmall)
                        )
                    } else Modifier
                )
                .padding(horizontal = dimensions.spaceSmall, vertical = dimensions.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PlayerAvatar(
                player = player,
                isCurrentPlayer = isCurrentPlayer
            )

            Spacer(modifier = Modifier.width(dimensions.spaceSmall))

            PlayerNameAndProgress(
                player = player,
                isCurrentPlayer = isCurrentPlayer,
                animatedProgress = animatedProgress,
                botDifficulty = botDifficulty,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(dimensions.spaceSmall))

            ScoreColumn(
                score = score,
                isCurrentPlayer = isCurrentPlayer
            )
        }
    }
}

@Composable
private fun PlayerAvatar(
    player: Player,
    isCurrentPlayer: Boolean,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier
            .size(dimensions.scoreboardAvatarSize)
            .clip(CircleShape)
            .background(
                brush = if (isCurrentPlayer) {
                    Brush.radialGradient(
                        colors = listOf(Primary, Primary.copy(alpha = 0.7f))
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        )
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        val initial = if (player.name == "Bot") "B" else player.name.firstOrNull()?.uppercase() ?: "?"
        Text(
            text = initial,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isCurrentPlayer) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PlayerNameAndProgress(
    player: Player,
    isCurrentPlayer: Boolean,
    animatedProgress: Float,
    botDifficulty: BotDifficulty?,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(modifier = modifier) {
        val displayName = if (player.name == "Bot" && botDifficulty != null) {
            "Bot ${
                when (botDifficulty) {
                    BotDifficulty.BEGINNER -> stringResource(R.string.bot_beginner)
                    BotDifficulty.INTERMEDIATE -> stringResource(R.string.bot_intermediate)
                    BotDifficulty.EXPERT -> stringResource(R.string.bot_expert)
                }
            }"
        } else {
            player.name
        }

        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Medium,
            color = if (isCurrentPlayer) Primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = if (isCurrentPlayer) Primary else Secondary.copy(alpha = 0.6f),
            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun ScoreColumn(
    score: Int,
    isCurrentPlayer: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "$score",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = if (isCurrentPlayer) Primary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.score_target),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}
