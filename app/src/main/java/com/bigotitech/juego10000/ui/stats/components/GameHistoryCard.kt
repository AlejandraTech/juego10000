package com.bigotitech.juego10000.ui.stats.components

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions
import com.bigotitech.juego10000.domain.model.Game
import com.bigotitech.juego10000.domain.model.Player
import com.bigotitech.juego10000.ui.common.theme.AmberColor
import com.bigotitech.juego10000.ui.common.theme.CardShape
import com.bigotitech.juego10000.ui.common.util.DateFormatUtils

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
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimensions.cardElevation,
                shape = CardShape,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            // Encabezado con ID de partida y fecha
            GameCardHeader(game)

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = dimensions.spaceExtraSmall)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensions.iconSizeSmall + dimensions.spaceExtraSmall)
            )
            Spacer(modifier = Modifier.width(dimensions.spaceSmall))
            Text(
                text = stringResource(R.string.game_number, game.id),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Surface(
            shape = RoundedCornerShape(dimensions.spaceMedium),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Text(
                text = DateFormatUtils.formatFullDate(game.completedAt ?: game.startedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            contentDescription = stringResource(R.string.trophy),
            tint = AmberColor,
            modifier = Modifier.size(dimensions.spaceLarge)
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        Text(
            text = winner?.name ?: stringResource(R.string.no_winner),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        // Chip para mostrar el número de jugadores
        GameStatChip(
            icon = R.drawable.ic_add_player,
            value = game.playerCount.toString()
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        // Chip para mostrar el número de rondas
        GameStatChip(
            icon = R.drawable.ic_dice,
            value = game.currentRound.toString()
        )
    }
}

/**
 * Chip para mostrar estadísticas de la partida
 */
@Composable
private fun GameStatChip(
    icon: Int,
    value: String
) {
    val dimensions = LocalDimensions.current
    Surface(
        shape = RoundedCornerShape(dimensions.spaceMedium),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = dimensions.spaceSmall, vertical = dimensions.spaceExtraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensions.spaceMedium)
            )
            Spacer(modifier = Modifier.width(dimensions.spaceExtraSmall))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
