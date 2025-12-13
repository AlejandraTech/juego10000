package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Game
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.components.animation.AnimatedEntrance
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
        AnimatedEntrance {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(dimensions.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(dimensions.spaceMedium)
            ) {
                item {
                    TabHeader(
                        title = stringResource(R.string.game_history),
                        subtitle = stringResource(R.string.games_count, gameHistory.size)
                    )
                }

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
