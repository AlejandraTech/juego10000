package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.domain.model.Game
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary

/**
 * Tarjeta que muestra la información de una partida en el historial
 */
@Composable
fun GameHistoryCard(
    game: Game,
    winner: Player?,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.spaceExtraSmall / 2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            // Encabezado con ID de partida y fecha
            GameCardHeader(game)

            Spacer(modifier = Modifier.height(dimensions.spaceSmall + dimensions.spaceExtraSmall))

            // Información del ganador y estadísticas
            GameCardDetails(game, winner)
        }
    }
}

/**
 * Encabezado de la tarjeta de partida
 */
@Composable
private fun GameCardHeader(game: Game) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dice),
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(dimensions.iconSizeSmall + dimensions.spaceExtraSmall)
            )
            Spacer(modifier = Modifier.width(dimensions.spaceSmall))
            Text(
                text = "Partida #${game.id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Surface(
            shape = RoundedCornerShape(dimensions.spaceMedium),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ) {
            Text(
                text = DateFormatUtils.formatFullDate(game.completedAt ?: game.startedAt),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = dimensions.spaceSmall, vertical = dimensions.spaceExtraSmall)
            )
        }
    }
}

/**
 * Detalles de la tarjeta de partida (ganador, jugadores, rondas)
 */
@Composable
private fun GameCardDetails(game: Game, winner: Player?) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_trophy),
            contentDescription = "Ganador",
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(dimensions.spaceLarge)
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        Text(
            text = winner?.name ?: "Sin ganador",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        // Chip para mostrar el número de jugadores
        GameStatChip(
            icon = R.drawable.ic_add_player,
            value = game.playerCount.toString(),
            color = Primary
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        // Chip para mostrar el número de rondas
        GameStatChip(
            icon = R.drawable.ic_dice,
            value = game.currentRound.toString(),
            color = Secondary
        )
    }
}

/**
 * Chip para mostrar estadísticas de la partida
 */
@Composable
private fun GameStatChip(
    icon: Int,
    value: String,
    color: Color
) {
    val dimensions = LocalDimensions.current
    Surface(
        shape = RoundedCornerShape(dimensions.spaceMedium),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = dimensions.spaceSmall, vertical = dimensions.spaceExtraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(dimensions.spaceMedium)
            )
            Spacer(modifier = Modifier.width(dimensions.spaceExtraSmall))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}
