package com.bigotitech.juego10000.ui.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.domain.model.BotDifficulty
import com.bigotitech.juego10000.ui.common.components.dialog.BaseDialog
import com.bigotitech.juego10000.ui.common.components.dialog.DialogButtons
import com.bigotitech.juego10000.ui.common.components.dialog.DialogHeader
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions

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

    BaseDialog(
        onDismiss = {
            if (showDifficultySelection) {
                showDifficultySelection = false
            } else {
                onDismiss()
            }
        }
    ) {
        if (!showDifficultySelection) {
            GameModeStep(
                selectedMode = selectedMode,
                onModeSelected = { selectedMode = it },
                onConfirm = {
                    if (selectedMode == GameMode.MULTIPLAYER) {
                        onMultiplayerSelected()
                    } else {
                        showDifficultySelection = true
                    }
                },
                onCancel = onDismiss
            )
        } else {
            DifficultyStep(
                selectedDifficulty = selectedDifficulty,
                onDifficultySelected = { selectedDifficulty = it },
                onConfirm = { onSinglePlayerSelected(selectedDifficulty) },
                onBack = { showDifficultySelection = false }
            )
        }
    }
}

@Composable
private fun GameModeStep(
    selectedMode: GameMode,
    onModeSelected: (GameMode) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val dimensions = LocalDimensions.current

    DialogHeader(
        title = stringResource(id = R.string.game_mode),
        description = stringResource(R.string.select_game_mode_description),
        iconPainter = painterResource(id = R.drawable.ic_dice)
    )

    Spacer(modifier = Modifier.height(dimensions.spaceLarge))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        SelectableOption(
            title = stringResource(id = R.string.multiplayer_mode),
            description = stringResource(R.string.multiplayer_description),
            iconResId = R.drawable.ic_add_player,
            isSelected = selectedMode == GameMode.MULTIPLAYER,
            onClick = { onModeSelected(GameMode.MULTIPLAYER) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        SelectableOption(
            title = stringResource(id = R.string.single_player_mode),
            description = stringResource(R.string.single_player_description),
            iconResId = R.drawable.ic_dice,
            isSelected = selectedMode == GameMode.SINGLE_PLAYER,
            onClick = { onModeSelected(GameMode.SINGLE_PLAYER) },
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(dimensions.spaceLarge))

    DialogButtons(
        onCancel = onCancel,
        onConfirm = onConfirm,
        confirmText = stringResource(id = R.string.confirm)
    )
}

@Composable
private fun DifficultyStep(
    selectedDifficulty: BotDifficulty,
    onDifficultySelected: (BotDifficulty) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val dimensions = LocalDimensions.current

    DialogHeader(
        title = stringResource(id = R.string.select_bot_difficulty),
        description = stringResource(R.string.select_difficulty_description),
        iconPainter = painterResource(id = R.drawable.ic_dice)
    )

    Spacer(modifier = Modifier.height(dimensions.spaceLarge))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        SelectableOption(
            title = stringResource(id = R.string.bot_beginner),
            description = stringResource(R.string.beginner_description),
            isSelected = selectedDifficulty == BotDifficulty.BEGINNER,
            onClick = { onDifficultySelected(BotDifficulty.BEGINNER) },
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

        SelectableOption(
            title = stringResource(id = R.string.bot_intermediate),
            description = stringResource(R.string.intermediate_description),
            isSelected = selectedDifficulty == BotDifficulty.INTERMEDIATE,
            onClick = { onDifficultySelected(BotDifficulty.INTERMEDIATE) },
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

        SelectableOption(
            title = stringResource(id = R.string.bot_expert),
            description = stringResource(R.string.expert_description),
            isSelected = selectedDifficulty == BotDifficulty.EXPERT,
            onClick = { onDifficultySelected(BotDifficulty.EXPERT) },
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

    Spacer(modifier = Modifier.height(dimensions.spaceLarge))

    DialogButtons(
        onCancel = onBack,
        onConfirm = onConfirm,
        confirmText = stringResource(id = R.string.confirm)
    )
}
