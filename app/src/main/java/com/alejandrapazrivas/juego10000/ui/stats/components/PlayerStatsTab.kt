package com.alejandrapazrivas.juego10000.ui.stats.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.stats.StatsViewModel.PlayerStats

/**
 * Pestaña que muestra las estadísticas de los jugadores
 */
@Composable
fun PlayerStatsTab(players: List<PlayerStats>) {
    val dimensions = LocalDimensions.current
    Column(modifier = Modifier.fillMaxSize()) {
        if (players.isEmpty()) {
            EmptyStateMessage(message = stringResource(R.string.no_registered_players))
        } else {
            // Lista de jugadores con animación de aparición
            AnimatedEntrance {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(dimensions.spaceMedium),
                    verticalArrangement = Arrangement.spacedBy(dimensions.spaceSmall + dimensions.spaceExtraSmall)
                ) {
                    // Gráfica de tasa de victorias
                    item {
                        WinRateChart(
                            players = players,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(dimensions.spaceSmall))
                    }

                    // Lista de jugadores
                    items(players) { playerStat ->
                        PlayerStatCard(playerStat = playerStat)
                    }
                }
            }
        }
    }
}
