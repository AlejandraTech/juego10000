package com.alejandrapazrivas.juego10000.ui.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.BotDifficulty
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Enumeración para los modos de juego disponibles
 */
enum class GameMode {
    MULTIPLAYER, SINGLE_PLAYER
}

/**
 * Diálogo para seleccionar el modo de juego (multijugador o un jugador)
 * y la dificultad del bot en caso de seleccionar modo un jugador.
 */
@Composable
fun GameModeSelectionDialog(
    onDismiss: () -> Unit,
    onMultiplayerSelected: () -> Unit,
    onSinglePlayerSelected: (BotDifficulty) -> Unit
) {
    val dimensions = LocalDimensions.current
    var selectedMode by remember { mutableStateOf(GameMode.MULTIPLAYER) }
    var selectedDifficulty by remember { mutableStateOf(BotDifficulty.BEGINNER) }
    var showDifficultySelection by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = if (!showDifficultySelection) 
                    stringResource(id = R.string.game_mode)
                else 
                    stringResource(id = R.string.select_bot_difficulty),
                style = MaterialTheme.typography.headlineSmall
            ) 
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (!showDifficultySelection) {
                    // Descripción del modo de juego
                    Text(
                        text = "Selecciona el modo de juego para esta partida",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                    // Modo de juego - Multijugador
                    SelectableOption(
                        title = stringResource(id = R.string.multiplayer_mode),
                        description = "Juega con amigos en el mismo dispositivo.",
                        iconResId = R.drawable.ic_add_player,
                        isSelected = selectedMode == GameMode.MULTIPLAYER,
                        onClick = { selectedMode = GameMode.MULTIPLAYER },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                    // Modo de juego - Un jugador
                    SelectableOption(
                        title = stringResource(id = R.string.single_player_mode),
                        description = "Juega contra un bot inteligente.",
                        iconResId = R.drawable.ic_dice,
                        isSelected = selectedMode == GameMode.SINGLE_PLAYER,
                        onClick = { selectedMode = GameMode.SINGLE_PLAYER },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Descripción de la dificultad
                    Text(
                        text = "Selecciona la dificultad del bot para la partida",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                    // Dificultad - Principiante
                    SelectableOption(
                        title = stringResource(id = R.string.bot_beginner),
                        description = "Para jugadores nuevos o casuales.",
                        isSelected = selectedDifficulty == BotDifficulty.BEGINNER,
                        onClick = { selectedDifficulty = BotDifficulty.BEGINNER },
                        leadingContent = {
                            Box(
                                modifier = Modifier.size(dimensions.avatarSizeSmall),
                                contentAlignment = Alignment.Center
                            ) {
                                DifficultyLevelIcon(
                                    level = 1,
                                    isSelected = selectedDifficulty == BotDifficulty.BEGINNER
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                    // Dificultad - Intermedio
                    SelectableOption(
                        title = stringResource(id = R.string.bot_intermediate),
                        description = "Para jugadores con experiencia.",
                        isSelected = selectedDifficulty == BotDifficulty.INTERMEDIATE,
                        onClick = { selectedDifficulty = BotDifficulty.INTERMEDIATE },
                        leadingContent = {
                            Box(
                                modifier = Modifier.size(dimensions.avatarSizeSmall),
                                contentAlignment = Alignment.Center
                            ) {
                                DifficultyLevelIcon(
                                    level = 2,
                                    isSelected = selectedDifficulty == BotDifficulty.INTERMEDIATE
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                    // Dificultad - Experto
                    SelectableOption(
                        title = stringResource(id = R.string.bot_expert),
                        description = "Para jugadores avanzados.",
                        isSelected = selectedDifficulty == BotDifficulty.EXPERT,
                        onClick = { selectedDifficulty = BotDifficulty.EXPERT },
                        leadingContent = {
                            Box(
                                modifier = Modifier.size(dimensions.avatarSizeSmall),
                                contentAlignment = Alignment.Center
                            ) {
                                DifficultyLevelIcon(
                                    level = 3,
                                    isSelected = selectedDifficulty == BotDifficulty.EXPERT
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!showDifficultySelection) {
                        if (selectedMode == GameMode.MULTIPLAYER) {
                            onMultiplayerSelected()
                        } else {
                            showDifficultySelection = true
                        }
                    } else {
                        onSinglePlayerSelected(selectedDifficulty)
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (showDifficultySelection) {
                        showDifficultySelection = false
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(
                    text = if (showDifficultySelection) 
                        stringResource(id = R.string.back) 
                    else 
                        stringResource(id = R.string.cancel)
                )
            }
        }
    )
}
