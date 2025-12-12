package com.alejandrapazrivas.juego10000.ui.game.components

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.BotDifficulty
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary

/**
 * Componente que muestra la fila de un jugador en la tabla de puntuaciones
 */
@Composable
private fun PlayerScoreRow(
    player: Player,
    score: Int,
    isCurrentPlayer: Boolean,
    position: Int,
    botDifficulty: BotDifficulty? = null
) {
    val dimensions = LocalDimensions.current
    val infiniteTransition = rememberInfiniteTransition(label = "current_player")

    // Animación de pulso para el jugador actual
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Progreso hacia 10,000
    val progress = (score / 10000f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "progress"
    )

    val accentColor = Color(0xFFFFD700) // Dorado

    Box(
        modifier = Modifier
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
            // Avatar con inicial - tamaño responsive
            Box(
                modifier = Modifier
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

            Spacer(modifier = Modifier.width(dimensions.spaceSmall))

            // Nombre y barra de progreso
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre del jugador
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

                // Barra de progreso hacia 10,000
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

            Spacer(modifier = Modifier.width(dimensions.spaceSmall))

            // Puntuación
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCurrentPlayer) Primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "/ 10,000",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ScoreboardSection(
    players: List<Player>,
    playerScores: Map<Long, Int>,
    currentPlayerIndex: Int,
    isSinglePlayerMode: Boolean = false,
    botDifficulty: BotDifficulty? = null,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val scrollState = rememberScrollState()

    // Altura máxima responsive según el tamaño de pantalla
    val maxPlayersHeight = dimensions.scoreboardMaxHeight

    // Altura aproximada de cada fila de jugador (usar la dimensión)
    val playerRowHeight = dimensions.scoreboardPlayerRowHeight.value.toInt()

    // Auto-scroll al jugador actual cuando cambia el turno
    LaunchedEffect(currentPlayerIndex) {
        if (players.size > 2) {
            val targetScroll = (currentPlayerIndex * playerRowHeight).coerceAtLeast(0)
            scrollState.animateScrollTo(targetScroll)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la tabla de puntuaciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.scoreboard),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = dimensions.titleFontSize),
                fontWeight = FontWeight.Bold,
                color = Primary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        HorizontalDivider(
            modifier = Modifier.padding(bottom = dimensions.spaceSmall),
            color = Primary.copy(alpha = 0.2f),
            thickness = 1.dp
        )

        // Lista de jugadores ordenados por puntuación (para mostrar posición)
        val sortedPlayers = players.mapIndexed { index, player ->
            Triple(player, playerScores[player.id] ?: 0, index)
        }.sortedByDescending { it.second }

        // Lista de jugadores con scroll cuando hay muchos (más de 2)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (players.size > 2) {
                        Modifier
                            .heightIn(max = maxPlayersHeight)
                            .verticalScroll(scrollState)
                    } else {
                        Modifier
                    }
                )
        ) {
            players.forEachIndexed { index, player ->
                val isCurrentPlayer = index == currentPlayerIndex
                val playerScore = playerScores[player.id] ?: 0
                val position = sortedPlayers.indexOfFirst { it.first.id == player.id } + 1

                PlayerScoreRow(
                    player = player,
                    score = playerScore,
                    isCurrentPlayer = isCurrentPlayer,
                    position = position,
                    botDifficulty = if (player.name == "Bot") botDifficulty else null
                )
            }
        }
    }
}
