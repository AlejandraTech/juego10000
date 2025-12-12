package com.alejandrapazrivas.juego10000.ui.stats.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Game
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary

/**
 * Sección que muestra los detalles de un jugador seleccionado
 */
@Composable
fun PlayerDetailSection(
    player: Player,
    games: List<Game>
) {
    val dimensions = LocalDimensions.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall),
        shape = CardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.spaceExtraSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            // Encabezado con el nombre del jugador
            PlayerDetailHeader(player)

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensions.spaceSmall),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // Lista de partidas del jugador
            PlayerGamesList(games, player.id)
        }
    }
}

/**
 * Encabezado de la sección de detalles del jugador
 */
@Composable
private fun PlayerDetailHeader(player: Player) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_stats),
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(dimensions.spaceLarge)
        )
        Spacer(modifier = Modifier.width(dimensions.spaceSmall))
        Text(
            text = stringResource(R.string.player_history, player.name),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Lista de partidas del jugador
 */
@Composable
private fun PlayerGamesList(games: List<Game>, playerId: Long) {
    val dimensions = LocalDimensions.current
    if (games.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.buttonHeight * 2),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_registered_games),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        // Usamos el componente de animación para la lista
        AnimatedEntrance {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensions.buttonHeight * 4),
                verticalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
            ) {
                items(games) { game ->
                    GameHistoryItem(game = game, playerId = playerId)
                }
            }
        }
    }
}
