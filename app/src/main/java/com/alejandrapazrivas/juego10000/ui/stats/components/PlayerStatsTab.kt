package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.stats.StatsViewModel.PlayerStats

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
                // Header con título
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = dimensions.spaceSmall)
                    ) {
                        Text(
                            text = stringResource(R.string.tab_players),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = stringResource(R.string.players_count, players.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                // Lista de jugadores con posición
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
