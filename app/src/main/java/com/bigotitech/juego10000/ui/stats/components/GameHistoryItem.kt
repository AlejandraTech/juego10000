package com.bigotitech.juego10000.ui.stats.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions
import com.bigotitech.juego10000.domain.model.Game
import com.bigotitech.juego10000.ui.common.theme.AmberColor
import com.bigotitech.juego10000.ui.common.theme.ScorePositive
import com.bigotitech.juego10000.ui.common.util.DateFormatUtils

/**
 * Elemento que muestra una partida en el historial de un jugador específico
 */
@Composable
fun GameHistoryItem(game: Game, playerId: Long) {
    val dimensions = LocalDimensions.current
    val isWinner = game.winnerPlayerId == playerId
    val backgroundColor = if (isWinner) ScorePositive.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val statusColor = if (isWinner) ScorePositive else Color.Gray
    val statusText = if (isWinner) stringResource(R.string.victory_result) else stringResource(R.string.defeat_result)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spaceExtraSmall),
        shape = RoundedCornerShape(dimensions.spaceSmall),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de trofeo para victorias
            if (isWinner) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trophy),
                    contentDescription = stringResource(R.string.victory_result),
                    tint = AmberColor,
                    modifier = Modifier.size(dimensions.iconSizeSmall + dimensions.spaceExtraSmall)
                )
                Spacer(modifier = Modifier.width(dimensions.spaceSmall))
            }

            // Fecha de la partida
            Text(
                text = DateFormatUtils.formatShortDate(game.completedAt ?: game.startedAt),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            // Indicador de resultado
            Surface(
                shape = RoundedCornerShape(dimensions.spaceMedium),
                color = statusColor.copy(alpha = 0.2f)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = dimensions.spaceSmall + dimensions.spaceExtraSmall, vertical = dimensions.spaceExtraSmall)
                )
            }
        }
    }
}
