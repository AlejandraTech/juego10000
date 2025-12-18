package com.bigotitech.rokub10000.presentation.feature.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.core.ads.AdConstants
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.presentation.common.components.ads.BannerAd
import com.bigotitech.rokub10000.presentation.common.components.animation.AnimatedContentWrapper
import com.bigotitech.rokub10000.presentation.common.components.avatar.PlayerAvatar
import com.bigotitech.rokub10000.presentation.common.components.card.IconStatCard
import com.bigotitech.rokub10000.presentation.common.components.dialog.BaseDialog
import com.bigotitech.rokub10000.presentation.common.components.dialog.DialogButtons
import com.bigotitech.rokub10000.presentation.common.components.emptystate.EmptyState
import com.bigotitech.rokub10000.presentation.common.theme.CardShape
import com.bigotitech.rokub10000.presentation.common.theme.LocalDimensions
import com.bigotitech.rokub10000.presentation.feature.player.state.PlayerDialogState
import com.bigotitech.rokub10000.presentation.feature.player.state.PlayerUiState
import com.bigotitech.rokub10000.presentation.feature.player.state.PlayerWithBestTurn
import kotlinx.coroutines.delay

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
    onEditPlayer: (Player) -> Unit,
    onDeletePlayer: (Player) -> Unit,
    onPlayerNameChange: (String) -> Unit,
    onConfirmAdd: () -> Unit,
    onConfirmEdit: (Player) -> Unit,
    onConfirmDelete: (Player) -> Unit,
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

// ============================================================================
// Top App Bar
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_player),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.shadow(elevation = 4.dp)
    )
}

// ============================================================================
// Players List
// ============================================================================

@Composable
private fun PlayersList(
    playersWithBestTurn: List<PlayerWithBestTurn>,
    onEditPlayer: (Player) -> Unit,
    onDeletePlayer: (Player) -> Unit
) {
    val dimensions = LocalDimensions.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall),
        verticalArrangement = Arrangement.spacedBy(dimensions.spaceSmall + dimensions.spaceExtraSmall)
    ) {
        itemsIndexed(
            items = playersWithBestTurn,
            key = { _, playerWithBestTurn -> playerWithBestTurn.player.id }
        ) { index, playerWithBestTurn ->
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(100L * index)
                visible = true
            }

            AnimatedPlayerCard(
                visible = visible,
                player = playerWithBestTurn.player,
                bestTurnScore = playerWithBestTurn.bestTurnScore,
                onEditPlayer = { onEditPlayer(playerWithBestTurn.player) },
                onDeletePlayer = { onDeletePlayer(playerWithBestTurn.player) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(dimensions.headerIconSize))
        }
    }
}

@Composable
private fun AnimatedPlayerCard(
    visible: Boolean,
    player: Player,
    bestTurnScore: Int,
    onEditPlayer: () -> Unit,
    onDeletePlayer: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(durationMillis = 500)
                )
    ) {
        PlayerCard(
            player = player,
            bestTurnScore = bestTurnScore,
            onEditPlayer = onEditPlayer,
            onDeletePlayer = onDeletePlayer
        )
    }
}

// ============================================================================
// Player Card
// ============================================================================

@Composable
private fun PlayerCard(
    player: Player,
    bestTurnScore: Int,
    onEditPlayer: () -> Unit,
    onDeletePlayer: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimensions.cardElevation,
                shape = CardShape,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            PlayerCardHeader(
                playerName = player.name,
                onEditPlayer = onEditPlayer,
                onDeletePlayer = onDeletePlayer
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = dimensions.spaceExtraSmall)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            PlayerStatsRow(player = player, bestTurnScore = bestTurnScore)
        }
    }
}

@Composable
private fun PlayerCardHeader(
    playerName: String,
    onEditPlayer: () -> Unit,
    onDeletePlayer: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerAvatar(name = playerName)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensions.spaceMedium)
        ) {
            Text(
                text = playerName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        PlayerActions(
            onEditPlayer = onEditPlayer,
            onDeletePlayer = onDeletePlayer
        )
    }
}

@Composable
private fun PlayerActions(
    onEditPlayer: () -> Unit,
    onDeletePlayer: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Row {
        ActionButton(
            onClick = onEditPlayer,
            icon = Icons.Default.Edit,
            contentDescription = stringResource(id = R.string.edit_player),
            backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
            iconTint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(dimensions.spaceMedium))

        ActionButton(
            onClick = onDeletePlayer,
            icon = Icons.Default.Delete,
            contentDescription = stringResource(id = R.string.delete_player),
            backgroundColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
            iconTint = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    backgroundColor: Color,
    iconTint: Color
) {
    val dimensions = LocalDimensions.current

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(dimensions.avatarSizeSmall)
            .clip(CircleShape)
            .background(backgroundColor)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(dimensions.iconSizeSmall)
        )
    }
}

@Composable
private fun PlayerStatsRow(player: Player, bestTurnScore: Int) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconStatCard(
            icon = R.drawable.ic_dice,
            label = stringResource(id = R.string.games_played),
            value = player.gamesPlayed.toString(),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        IconStatCard(
            icon = R.drawable.ic_trophy,
            label = stringResource(id = R.string.games_won),
            value = player.gamesWon.toString(),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        IconStatCard(
            icon = R.drawable.ic_stats,
            label = stringResource(id = R.string.highest_score),
            value = bestTurnScore.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

// ============================================================================
// Empty Players List
// ============================================================================

@Composable
private fun EmptyPlayersList(
    modifier: Modifier = Modifier,
    onAddPlayer: () -> Unit
) {
    EmptyState(
        icon = painterResource(id = R.drawable.ic_add_player),
        title = stringResource(id = R.string.no_players),
        description = stringResource(id = R.string.add_players_to_start),
        actionButtonText = stringResource(id = R.string.add_player),
        actionButtonIcon = Icons.Default.Add,
        onActionClick = onAddPlayer,
        useGradientBackground = true,
        animated = true,
        iconTint = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

// ============================================================================
// Dialogs
// ============================================================================

@Composable
private fun PlayerDialogs(
    dialogState: PlayerDialogState,
    playerName: String,
    onPlayerNameChange: (String) -> Unit,
    onConfirmAdd: () -> Unit,
    onConfirmEdit: (Player) -> Unit,
    onConfirmDelete: (Player) -> Unit,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerDialog(
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

@Composable
private fun DeleteConfirmationDialog(
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
