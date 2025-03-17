package com.alejandrapazrivas.juego10000.ui.player.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.ButtonShape

/**
 * Componente que muestra los botones de cancelar y confirmar para un diálogo.
 * 
 * @param onCancel Callback para cancelar la acción
 * @param onConfirm Callback para confirmar la acción
 * @param confirmText Texto del botón de confirmación
 * @param confirmEnabled Si el botón de confirmación está habilitado
 * @param confirmColor Color del botón de confirmación
 * @param confirmTextColor Color del texto del botón de confirmación
 */
@Composable
fun DialogButtons(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = stringResource(id = R.string.confirm),
    confirmEnabled: Boolean = true,
    confirmColor: Color = MaterialTheme.colorScheme.primary,
    confirmTextColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Botón Cancelar
        OutlinedButton(
            onClick = onCancel,
            shape = ButtonShape,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        ) {
            Text(
                text = stringResource(id = R.string.cancel),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Botón Confirmar
        Button(
            onClick = onConfirm,
            enabled = confirmEnabled,
            shape = ButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = confirmColor,
                disabledContainerColor = confirmColor.copy(alpha = 0.3f)
            ),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        ) {
            Text(
                text = confirmText,
                color = confirmTextColor
            )
        }
    }
}
