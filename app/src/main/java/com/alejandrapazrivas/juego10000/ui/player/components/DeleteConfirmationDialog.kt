package com.alejandrapazrivas.juego10000.ui.player.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R

/**
 * Diálogo de confirmación para eliminar un jugador.
 * 
 * @param playerName Nombre del jugador a eliminar
 * @param onConfirm Callback para confirmar la eliminación
 * @param onDismiss Callback para cancelar la eliminación
 */
@Composable
fun DeleteConfirmationDialog(
    playerName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BaseDialog(onDismiss = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = stringResource(id = R.string.delete_player),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¿Estás seguro de que quieres eliminar a $playerName? Esta acción es permanente y el jugador se eliminará de todos los registros, incluidas estadísticas e historial de partidas.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                DialogButtons(
                    onCancel = onDismiss,
                    onConfirm = onConfirm,
                    confirmText = stringResource(id = R.string.delete),
                    confirmColor = MaterialTheme.colorScheme.error,
                    confirmTextColor = MaterialTheme.colorScheme.onError
                )
    }
}
