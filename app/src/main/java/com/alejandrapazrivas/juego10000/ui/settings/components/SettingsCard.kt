package com.alejandrapazrivas.juego10000.ui.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Modelo de datos para un elemento de configuración
 */
data class SettingItem(
    val title: String,
    val description: String,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)

/**
 * Componente que muestra una fila de configuración con un switch
 */
@Composable
private fun SettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * Componente que muestra una tarjeta con configuraciones
 */
@Composable
fun SettingsCard(
    title: String,
    settings: List<SettingItem>,
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
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            settings.forEachIndexed { index, item ->
                SettingRow(
                    title = item.title,
                    description = item.description,
                    checked = item.checked,
                    onCheckedChange = item.onCheckedChange
                )

                if (index < settings.size - 1) {
                    Spacer(modifier = Modifier.height(dimensions.spaceMedium))
                }
            }
        }
    }
}
