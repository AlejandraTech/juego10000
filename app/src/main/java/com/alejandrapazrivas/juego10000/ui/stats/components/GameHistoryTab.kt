package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Game
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Pestaña que muestra el historial de partidas
 */
@Composable
fun GameHistoryTab(gameHistory: List<Pair<Game, Player?>>) {
    val dimensions = LocalDimensions.current
    if (gameHistory.isEmpty()) {
        EmptyStateMessage(
            message = stringResource(R.string.no_registered_games),
            iconResId = R.drawable.ic_dice
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensions.spaceMedium)
        ) {
            Text(
                text = stringResource(R.string.game_history),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = dimensions.spaceSmall)
            )

            // Usamos el componente de animación para la lista
            AnimatedEntrance {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimensions.spaceSmall + dimensions.spaceExtraSmall)
                ) {
                    items(gameHistory) { (game, winner) ->
                        GameHistoryCard(
                            game = game,
                            winner = winner
                        )
                    }
                }
            }
        }
    }
}
