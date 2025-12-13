package com.alejandrapazrivas.juego10000.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Indicador visual del número de ronda actual.
 * Muestra la ronda en un chip con fondo semitransparente.
 *
 * @param round Número de ronda actual
 * @param modifier Modificador opcional
 */
@Composable
fun RoundIndicator(
    round: Int,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier
            .padding(vertical = dimensions.spaceSmall)
            .clip(RoundedCornerShape(40))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceExtraSmall)
    ) {
        Text(
            text = stringResource(R.string.round, round),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = dimensions.spaceSmall)
        )
    }
}
