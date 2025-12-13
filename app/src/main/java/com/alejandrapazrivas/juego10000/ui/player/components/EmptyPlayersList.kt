package com.alejandrapazrivas.juego10000.ui.player.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.components.emptystate.EmptyState

/**
 * Pantalla de estado vacío cuando no hay jugadores.
 * Usa el componente EmptyState compartido para consistencia visual.
 */
@Composable
fun EmptyPlayersList(
    modifier: Modifier = Modifier,
    onAddPlayer: () -> Unit
) {
    EmptyState(
        icon = painterResource(id = R.drawable.ic_add_player),
        title = stringResource(id = R.string.no_players),
        description = "Añade jugadores para comenzar a jugar",
        actionButtonText = stringResource(id = R.string.add_player),
        actionButtonIcon = Icons.Default.Add,
        onActionClick = onAddPlayer,
        useGradientBackground = true,
        animated = true,
        modifier = modifier
    )
}
