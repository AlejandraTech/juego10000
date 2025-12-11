package com.alejandrapazrivas.juego10000.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.BotDifficulty
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Componente que muestra la fila de un jugador en la tabla de puntuaciones
 */
@Composable
private fun PlayerScoreRow(
    player: Player,
    score: Int,
    isCurrentPlayer: Boolean,
    botDifficulty: BotDifficulty? = null
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spaceExtraSmall)
            .background(
                color = if (isCurrentPlayer)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(dimensions.spaceSmall)
            )
            .padding(horizontal = dimensions.spaceSmall, vertical = dimensions.spaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Nombre del jugador con indicador de turno actual
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (isCurrentPlayer) {
                Box(
                    modifier = Modifier
                        .size(dimensions.indicatorSize)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Spacer(modifier = Modifier.width(dimensions.spaceSmall))
            }

            // Mostrar el nombre del jugador o el nombre del bot con su nivel
            val displayName = if (player.name == "Bot" && botDifficulty != null) {
                // Formato: "Bot Principiante", "Bot Intermedio", "Bot Experto"
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
                fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrentPlayer)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Puntuación del jugador
        Text(
            text = "$score",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            color = if (isCurrentPlayer)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
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

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la tabla de puntuaciones
        Text(
            text = stringResource(R.string.scoreboard),
            style = MaterialTheme.typography.titleMedium.copy(fontSize = dimensions.titleFontSize),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = dimensions.spaceSmall)
        )

        Divider(
            modifier = Modifier.padding(bottom = dimensions.spaceExtraSmall),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        // Lista de jugadores con sus puntuaciones
        players.forEachIndexed { index, player ->
            val isCurrentPlayer = index == currentPlayerIndex
            // Asegurarse de que siempre hay una puntuación para cada jugador (0 por defecto)
            val playerScore = playerScores[player.id] ?: 0

            PlayerScoreRow(
                player = player,
                score = playerScore,
                isCurrentPlayer = isCurrentPlayer,
                botDifficulty = botDifficulty
            )

            // Separador entre jugadores (solo si no es el último jugador)
            if (index < players.size - 1) {
                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    modifier = Modifier.padding(horizontal = dimensions.spaceSmall)
                )
            }
        }
    }
}
