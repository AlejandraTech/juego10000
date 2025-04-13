package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.domain.model.Game
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.stats.StatsViewModel.PlayerStats

/**
 * Pestaña que muestra las estadísticas de los jugadores
 */
@Composable
fun PlayerStatsTab(
    players: List<PlayerStats>,
    selectedPlayer: Player?,
    onPlayerSelected: (Player) -> Unit,
    playerGames: List<Game>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (players.isEmpty()) {
            EmptyStateMessage(message = "No hay jugadores registrados")
        } else {
            // Lista de jugadores con animación de aparición
            AnimatedEntrance {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(players) { playerStat ->
                        PlayerStatCard(
                            playerStat = playerStat,
                            isSelected = selectedPlayer?.id == playerStat.player.id,
                            onClick = { onPlayerSelected(playerStat.player) }
                        )
                    }
                }
            }

            // Detalles del jugador seleccionado con animación
            AnimatedVisibility(
                visible = selectedPlayer != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                selectedPlayer?.let { player ->
                    PlayerDetailSection(
                        player = player,
                        games = playerGames
                    )
                }
            }
        }
    }
}
