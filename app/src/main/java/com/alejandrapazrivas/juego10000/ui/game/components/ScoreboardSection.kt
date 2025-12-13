package com.alejandrapazrivas.juego10000.ui.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.BotDifficulty
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary

/**
 * Sección de la tabla de puntuaciones que muestra todos los jugadores.
 * Incluye auto-scroll al jugador actual cuando hay más de 2 jugadores.
 *
 * @param players Lista de jugadores
 * @param playerScores Mapa de puntuaciones por ID de jugador
 * @param currentPlayerIndex Índice del jugador actual
 * @param isSinglePlayerMode Si es modo un jugador
 * @param botDifficulty Dificultad del bot (opcional)
 * @param modifier Modificador opcional
 */
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

    val maxPlayersHeight = dimensions.scoreboardMaxHeight
    val playerRowHeight = dimensions.scoreboardPlayerRowHeight.value.toInt()

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
        ScoreboardHeader()

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        HorizontalDivider(
            modifier = Modifier.padding(bottom = dimensions.spaceSmall),
            color = Primary.copy(alpha = 0.2f),
            thickness = 1.dp
        )

        PlayersList(
            players = players,
            playerScores = playerScores,
            currentPlayerIndex = currentPlayerIndex,
            botDifficulty = botDifficulty,
            maxPlayersHeight = maxPlayersHeight,
            scrollState = scrollState
        )
    }
}

@Composable
private fun ScoreboardHeader(modifier: Modifier = Modifier) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = modifier.fillMaxWidth(),
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
}

@Composable
private fun PlayersList(
    players: List<Player>,
    playerScores: Map<Long, Int>,
    currentPlayerIndex: Int,
    botDifficulty: BotDifficulty?,
    maxPlayersHeight: androidx.compose.ui.unit.Dp,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier
) {
    val sortedPlayers = players.mapIndexed { index, player ->
        Triple(player, playerScores[player.id] ?: 0, index)
    }.sortedByDescending { it.second }

    Column(
        modifier = modifier
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
