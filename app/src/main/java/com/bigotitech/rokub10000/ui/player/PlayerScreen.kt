package com.bigotitech.rokub10000.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ads.AdConstants
import com.bigotitech.rokub10000.ui.common.components.ads.BannerAd
import com.bigotitech.rokub10000.ui.common.components.animation.AnimatedContentWrapper
import com.bigotitech.rokub10000.ui.player.components.DeleteConfirmationDialog
import com.bigotitech.rokub10000.ui.player.components.EmptyPlayersList
import com.bigotitech.rokub10000.ui.player.components.PlayerDialog
import com.bigotitech.rokub10000.ui.player.components.PlayerTopAppBar
import com.bigotitech.rokub10000.ui.player.components.PlayersList
import com.bigotitech.rokub10000.ui.player.model.PlayerDialogState
import com.bigotitech.rokub10000.ui.player.model.PlayerUiState
import com.bigotitech.rokub10000.ui.player.model.PlayerWithBestTurn

@Composable
fun PlayerScreen(
    navController: NavController,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playersWithBestTurn by viewModel.playersWithBestTurn.collectAsState(initial = emptyList())
    var uiState by remember { mutableStateOf(PlayerUiState()) }

    PlayerScreenContent(
        playersWithBestTurn = playersWithBestTurn,
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onAddClick = { uiState = PlayerUiState(dialogState = PlayerDialogState.Add) },
        onEditPlayer = { player ->
            uiState = PlayerUiState(
                dialogState = PlayerDialogState.Edit(player),
                playerName = player.name
            )
        },
        onDeletePlayer = { player ->
            uiState = PlayerUiState(dialogState = PlayerDialogState.Delete(player))
        },
        onPlayerNameChange = { uiState = uiState.copy(playerName = it) },
        onConfirmAdd = {
            viewModel.createPlayer(uiState.playerName)
            uiState = PlayerUiState()
        },
        onConfirmEdit = { player ->
            viewModel.updatePlayer(player.id, uiState.playerName)
            uiState = PlayerUiState()
        },
        onConfirmDelete = { player ->
            viewModel.deactivatePlayer(player.id)
            uiState = PlayerUiState()
        },
        onDismissDialog = { uiState = PlayerUiState() }
    )
}

@Composable
private fun PlayerScreenContent(
    playersWithBestTurn: List<PlayerWithBestTurn>,
    uiState: PlayerUiState,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditPlayer: (com.bigotitech.rokub10000.domain.model.Player) -> Unit,
    onDeletePlayer: (com.bigotitech.rokub10000.domain.model.Player) -> Unit,
    onPlayerNameChange: (String) -> Unit,
    onConfirmAdd: () -> Unit,
    onConfirmEdit: (com.bigotitech.rokub10000.domain.model.Player) -> Unit,
    onConfirmDelete: (com.bigotitech.rokub10000.domain.model.Player) -> Unit,
    onDismissDialog: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            PlayerTopAppBar(
                title = stringResource(id = R.string.manage_players),
                onBackClick = onBackClick,
                onAddClick = onAddClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContentWrapper {
                    if (playersWithBestTurn.isEmpty()) {
                        EmptyPlayersList(onAddPlayer = onAddClick)
                    } else {
                        PlayersList(
                            playersWithBestTurn = playersWithBestTurn,
                            onEditPlayer = onEditPlayer,
                            onDeletePlayer = onDeletePlayer
                        )
                    }
                }
            }

            BannerAd(
                adUnitId = AdConstants.BANNER_PLAYERS,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    PlayerDialogs(
        dialogState = uiState.dialogState,
        playerName = uiState.playerName,
        onPlayerNameChange = onPlayerNameChange,
        onConfirmAdd = onConfirmAdd,
        onConfirmEdit = onConfirmEdit,
        onConfirmDelete = onConfirmDelete,
        onDismiss = onDismissDialog
    )
}

@Composable
private fun PlayerDialogs(
    dialogState: PlayerDialogState,
    playerName: String,
    onPlayerNameChange: (String) -> Unit,
    onConfirmAdd: () -> Unit,
    onConfirmEdit: (com.bigotitech.rokub10000.domain.model.Player) -> Unit,
    onConfirmDelete: (com.bigotitech.rokub10000.domain.model.Player) -> Unit,
    onDismiss: () -> Unit
) {
    when (dialogState) {
        is PlayerDialogState.None -> { /* No dialog */ }

        is PlayerDialogState.Add -> {
            PlayerDialog(
                title = stringResource(id = R.string.add_player),
                playerName = playerName,
                onNameChange = onPlayerNameChange,
                confirmButtonText = stringResource(id = R.string.confirm),
                onConfirm = onConfirmAdd,
                onDismiss = onDismiss
            )
        }

        is PlayerDialogState.Edit -> {
            PlayerDialog(
                title = stringResource(id = R.string.edit_player),
                playerName = playerName,
                onNameChange = onPlayerNameChange,
                confirmButtonText = stringResource(id = R.string.confirm),
                onConfirm = { onConfirmEdit(dialogState.player) },
                onDismiss = onDismiss
            )
        }

        is PlayerDialogState.Delete -> {
            DeleteConfirmationDialog(
                playerName = dialogState.player.name,
                onConfirm = { onConfirmDelete(dialogState.player) },
                onDismiss = onDismiss
            )
        }
    }
}
