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
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.components.dialog.BaseDialog
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

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
    val dimensions = LocalDimensions.current

    BaseDialog(onDismiss = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(dimensions.iconSizeExtraLarge)
                        .padding(bottom = dimensions.spaceMedium)
                )

                Text(
                    text = stringResource(id = R.string.delete_player),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                Text(
                    text = stringResource(id = R.string.delete_player_confirmation, playerName),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(dimensions.spaceLarge))

                DialogButtons(
                    onCancel = onDismiss,
                    onConfirm = onConfirm,
                    confirmText = stringResource(id = R.string.delete),
                    confirmColor = MaterialTheme.colorScheme.error,
                    confirmTextColor = MaterialTheme.colorScheme.onError
                )
    }
}
