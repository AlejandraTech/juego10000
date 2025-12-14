package com.bigotitech.juego10000.ui.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.ui.common.theme.CardShape
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions

/**
 * Modelo para los enlaces de atribución de sonidos
 */
data class SoundAttribution(
    val textResId: Int,
    val url: String
)

/**
 * Tarjeta de créditos con atribuciones de sonidos
 */
@Composable
fun CreditsCard(
    attributions: List<SoundAttribution>,
    onAttributionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensions.spaceMedium)
        ) {
            Text(
                text = stringResource(R.string.credits),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Text(
                text = stringResource(R.string.sound_credits),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            attributions.forEach { attribution ->
                LinkRow(
                    text = stringResource(attribution.textResId),
                    onClick = { onAttributionClick(attribution.url) }
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            Text(
                text = stringResource(R.string.pixabay_attribution),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
