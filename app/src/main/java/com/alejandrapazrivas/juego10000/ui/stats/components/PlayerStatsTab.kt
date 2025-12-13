package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.components.animation.AnimatedEntrance
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.stats.model.PlayerStats

/**
 * Pestaña que muestra las estadísticas de los jugadores
 */
@Composable
fun PlayerStatsTab(players: List<PlayerStats>) {
    val dimensions = LocalDimensions.current

    if (players.isEmpty()) {
        EmptyStateMessage(message = stringResource(R.string.no_registered_players))
    } else {
        AnimatedEntrance {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(dimensions.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(dimensions.spaceMedium)
            ) {
                item {
                    TabHeader(
                        title = stringResource(R.string.tab_players),
                        subtitle = stringResource(R.string.players_count, players.size)
                    )
                }

                itemsIndexed(players.sortedByDescending { it.player.gamesWon }) { index, playerStat ->
                    PlayerStatCard(
                        playerStat = playerStat,
                        position = index + 1
                    )
                }
            }
        }
    }
}
