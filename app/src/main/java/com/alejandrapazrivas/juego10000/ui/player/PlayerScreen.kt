package com.alejandrapazrivas.juego10000.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.player.components.AnimatedContent
import com.alejandrapazrivas.juego10000.ui.player.components.DeleteConfirmationDialog
import com.alejandrapazrivas.juego10000.ui.player.components.EmptyPlayersList
import com.alejandrapazrivas.juego10000.ui.player.components.PlayerDialog
import com.alejandrapazrivas.juego10000.ui.player.components.PlayerTopAppBar
import com.alejandrapazrivas.juego10000.ui.player.components.PlayersList

@Composable
fun PlayerScreen(
    navController: NavController,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val players by viewModel.players.collectAsState(initial = emptyList())
    val showAddDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }
    val showDeleteConfirmation = remember { mutableStateOf(false) }
    val selectedPlayer = remember { mutableStateOf<Player?>(null) }
    val newPlayerName = remember { mutableStateOf("") }


    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            PlayerTopAppBar(
                title = stringResource(id = R.string.manage_players),
                onBackClick = { navController.popBackStack() },
                onAddClick = {
                    newPlayerName.value = ""
                    showAddDialog.value = true
                }
            )
        }
    ) { paddingValues ->
        Box(
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
            AnimatedContent() {
                if (players.isEmpty()) {
                    EmptyPlayersList {
                        newPlayerName.value = ""
                        showAddDialog.value = true
                    }
                } else {
                    PlayersList(
                        players = players,
                        onEditPlayer = { player ->
                            selectedPlayer.value = player
                            newPlayerName.value = player.name
                            showEditDialog.value = true
                        },
                        onDeletePlayer = { player ->
                            selectedPlayer.value = player
                            showDeleteConfirmation.value = true
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog.value) {
        PlayerDialog(
            title = stringResource(id = R.string.add_player),
            playerName = newPlayerName.value,
            onNameChange = { newPlayerName.value = it },
            confirmButtonText = stringResource(id = R.string.confirm),
            onConfirm = {
                viewModel.createPlayer(newPlayerName.value)
                showAddDialog.value = false
            },
            onDismiss = { showAddDialog.value = false }
        )
    }

    if (showEditDialog.value && selectedPlayer.value != null) {
        PlayerDialog(
            title = stringResource(id = R.string.edit_player),
            playerName = newPlayerName.value,
            onNameChange = { newPlayerName.value = it },
            confirmButtonText = stringResource(id = R.string.confirm),
            onConfirm = {
                selectedPlayer.value?.let { player ->
                    viewModel.updatePlayer(player.id, newPlayerName.value)
                }
                showEditDialog.value = false
            },
            onDismiss = { showEditDialog.value = false }
        )
    }

    if (showDeleteConfirmation.value && selectedPlayer.value != null) {
        DeleteConfirmationDialog(
            playerName = selectedPlayer.value?.name ?: "",
            onConfirm = {
                selectedPlayer.value?.let { player ->
                    viewModel.deactivatePlayer(player.id)
                }
                showDeleteConfirmation.value = false
            },
            onDismiss = { showDeleteConfirmation.value = false }
        )
    }
}
