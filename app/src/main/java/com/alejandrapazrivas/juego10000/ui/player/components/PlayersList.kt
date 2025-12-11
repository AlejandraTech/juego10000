package com.alejandrapazrivas.juego10000.ui.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import kotlinx.coroutines.delay

/**
 * Componente que muestra una lista de jugadores.
 * 
 * @param players Lista de jugadores a mostrar
 * @param onEditPlayer Callback para editar un jugador
 * @param onDeletePlayer Callback para eliminar un jugador
 */
@Composable
fun PlayersList(
    players: List<Player>,
    onEditPlayer: (Player) -> Unit,
    onDeletePlayer: (Player) -> Unit
) {
    val dimensions = LocalDimensions.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall),
        verticalArrangement = Arrangement.spacedBy(dimensions.spaceSmall + dimensions.spaceExtraSmall)
    ) {
        itemsIndexed(
            items = players,
            key = { _, player -> player.id }
        ) { index, player ->
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(100L * index)
                visible = true
            }

            AnimatedPlayerCard(
                visible = visible,
                player = player,
                onEditPlayer = { onEditPlayer(player) },
                onDeletePlayer = { onDeletePlayer(player) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(dimensions.headerIconSize))
        }
    }
}

/**
 * Componente que muestra una tarjeta de jugador con animación.
 */
@Composable
private fun AnimatedPlayerCard(
    visible: Boolean,
    player: Player,
    onEditPlayer: () -> Unit,
    onDeletePlayer: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(durationMillis = 500)
                )
    ) {
        PlayerCard(
            player = player,
            onEditPlayer = onEditPlayer,
            onDeletePlayer = onDeletePlayer
        )
    }
}
