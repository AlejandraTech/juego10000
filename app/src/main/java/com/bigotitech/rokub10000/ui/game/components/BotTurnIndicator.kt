package com.bigotitech.rokub10000.ui.game.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions

/**
 * Indicador visual que se muestra durante el turno del bot.
 * Muestra un mensaje y un indicador de progreso circular.
 *
 * @param modifier Modificador opcional
 */
@Composable
fun BotTurnIndicator(
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.bot_turn),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(dimensions.progressIndicatorSize),
            strokeWidth = 4.dp
        )
    }
}
