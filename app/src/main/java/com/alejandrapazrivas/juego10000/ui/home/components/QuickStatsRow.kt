package com.alejandrapazrivas.juego10000.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alejandrapazrivas.juego10000.R

/**
 * Fila horizontal con las estadísticas rápidas del usuario.
 */
@Composable
fun QuickStatsRow(
    gamesPlayed: Int,
    wins: Int,
    winRate: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickStatItem(
            value = gamesPlayed.toString(),
            label = stringResource(R.string.games_played),
            icon = R.drawable.ic_dice
        )
        QuickStatItem(
            value = wins.toString(),
            label = stringResource(R.string.games_won),
            icon = R.drawable.ic_trophy
        )
        QuickStatItem(
            value = "$winRate%",
            label = stringResource(R.string.win_rate),
            icon = R.drawable.ic_stats
        )
    }
}
