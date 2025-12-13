package com.alejandrapazrivas.juego10000.ui.player.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.components.dialog.BaseDialog
import com.alejandrapazrivas.juego10000.ui.common.components.dialog.DialogButtons
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Diálogo para añadir o editar un jugador.
 * 
 * @param title Título del diálogo
 * @param playerName Nombre del jugador
 * @param onNameChange Callback para cambiar el nombre del jugador
 * @param confirmButtonText Texto del botón de confirmación
 * @param onConfirm Callback para confirmar la acción
 * @param onDismiss Callback para cancelar la acción
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDialog(
    title: String,
    playerName: String,
    onNameChange: (String) -> Unit,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val dimensions = LocalDimensions.current

    BaseDialog(onDismiss = onDismiss) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(dimensions.spaceLarge))

                OutlinedTextField(
                    value = playerName,
                    onValueChange = onNameChange,
                    label = {
                        Text(
                            text = stringResource(id = R.string.player_name),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.player_name),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(dimensions.spaceSmall + dimensions.spaceExtraSmall),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(dimensions.spaceLarge))

                DialogButtons(
                    onCancel = onDismiss,
                    onConfirm = onConfirm,
                    confirmText = confirmButtonText,
                    confirmEnabled = playerName.isNotBlank()
                )
    }
}
