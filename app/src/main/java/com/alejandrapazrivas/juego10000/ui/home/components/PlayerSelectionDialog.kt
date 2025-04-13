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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Player

/**
 * Diálogo para seleccionar jugadores para una partida.
 * Permite selección única en modo individual o múltiple en modo multijugador.
 */
@Composable
fun PlayerSelectionDialog(
    availablePlayers: List<Player>,
    selectedPlayers: List<Player>,
    onPlayerSelected: (Player) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isSinglePlayerMode: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.manage_players)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isSinglePlayerMode) {
                        "Selecciona tu jugador para la partida contra el bot"
                    } else {
                        "Selecciona entre 2 y 6 jugadores para la partida multijugador"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de jugadores disponibles
                availablePlayers.forEach { player ->
                    val isSelected = selectedPlayers.contains(player)
                    
                    SelectableOption(
                        title = player.name,
                        description = if (isSinglePlayerMode) {
                            "Selecciona este jugador para la partida"
                        } else {
                            if (isSelected) "Jugador seleccionado" else "Haz clic para seleccionar"
                        },
                        isSelected = isSelected,
                        onClick = { onPlayerSelected(player) },
                        leadingContent = {
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSinglePlayerMode) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                } else {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = null
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = if (isSinglePlayerMode) {
                    // En modo individual, se requiere exactamente 1 jugador
                    selectedPlayers.size == 1
                } else {
                    // En modo multijugador, se requieren entre 2 y 6 jugadores
                    selectedPlayers.size >= 2 && selectedPlayers.size <= 6
                }
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
