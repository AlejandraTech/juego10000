package com.bigotitech.juego10000.ui.game.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.domain.model.Player
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions

/**
 * Mensaje inicial que se muestra al comenzar el turno de un jugador.
 * Indica el nombre del jugador y las instrucciones para lanzar los dados.
 *
 * @param currentPlayer Jugador actual (puede ser null)
 * @param modifier Modificador opcional
 */
@Composable
fun GameStartMessage(
    currentPlayer: Player?,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentPlayer?.let {
                stringResource(R.string.player_turn, it.name)
            } ?: stringResource(R.string.select_dice_to_keep),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        Text(
            text = stringResource(R.string.roll_dice_instruction),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
