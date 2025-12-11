package com.alejandrapazrivas.juego10000.ui.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Diálogo para seleccionar oponentes para una partida multijugador.
 * El usuario actual ya está incluido automáticamente en la partida.
 */
@Composable
fun PlayerSelectionDialog(
    availablePlayers: List<Player>,
    selectedPlayers: List<Player>,
    onPlayerSelected: (Player) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val dimensions = LocalDimensions.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Seleccionar Oponentes") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Selecciona entre 1 y 5 oponentes para la partida",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                // Lista de jugadores disponibles (sin el usuario actual)
                availablePlayers.forEach { player ->
                    val isSelected = selectedPlayers.contains(player)

                    SelectableOption(
                        title = player.name,
                        description = if (isSelected) "Oponente seleccionado" else "Haz clic para seleccionar",
                        isSelected = isSelected,
                        onClick = { onPlayerSelected(player) },
                        leadingContent = {
                            Box(
                                modifier = Modifier.size(dimensions.avatarSizeSmall),
                                contentAlignment = Alignment.Center
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimensions.spaceExtraSmall)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                // Se requiere entre 1 y 5 oponentes
                enabled = selectedPlayers.isNotEmpty() && selectedPlayers.size <= 5
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}
