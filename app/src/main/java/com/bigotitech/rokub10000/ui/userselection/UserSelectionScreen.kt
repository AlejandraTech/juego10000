package com.bigotitech.rokub10000.ui.userselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary
import com.bigotitech.rokub10000.ui.userselection.components.AddPlayerButton
import com.bigotitech.rokub10000.ui.userselection.components.AnimatedBackground
import com.bigotitech.rokub10000.ui.userselection.components.AnimatedHeader
import com.bigotitech.rokub10000.ui.userselection.components.EmptyPlayersState
import com.bigotitech.rokub10000.ui.userselection.components.LanguageSelectorButton
import com.bigotitech.rokub10000.ui.userselection.components.PlayerSelectionCard

/**
 * Pantalla de selección de usuario
 */
@Composable
fun UserSelectionScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    viewModel: UserSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentLanguage by viewModel.language.collectAsState()
    val dimensions = LocalDimensions.current

    LaunchedEffect(Unit) {
        viewModel.loadPlayers()
    }

    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            viewModel.onNavigationHandled()
            onNavigateToHome()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()

        when {
            uiState.isLoading -> LoadingState()
            else -> UserSelectionContent(
                players = uiState.players,
                showCreatePlayerHint = uiState.showCreatePlayerHint,
                onPlayerSelected = { viewModel.selectUser(it) },
                onCreatePlayer = onNavigateToPlayers
            )
        }

        // Botón de idioma en la esquina superior derecha
        LanguageSelectorButton(
            currentLanguage = currentLanguage,
            onLanguageChange = { viewModel.setLanguage(it) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(dimensions.spaceMedium)
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun UserSelectionContent(
    players: List<Player>,
    showCreatePlayerHint: Boolean,
    onPlayerSelected: (Player) -> Unit,
    onCreatePlayer: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.spaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(dimensions.buttonHeight + dimensions.spaceLarge))

        AnimatedHeader()

        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

        AddPlayerButton(onClick = onCreatePlayer)

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        if (showCreatePlayerHint || players.isEmpty()) {
            EmptyPlayersState()
        } else {
            PlayersList(
                players = players,
                onPlayerSelected = onPlayerSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))
    }
}

@Composable
private fun PlayersList(
    players: List<Player>,
    onPlayerSelected: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    LazyColumn(
        contentPadding = PaddingValues(vertical = dimensions.spaceSmall),
        verticalArrangement = Arrangement.spacedBy(dimensions.spaceMedium),
        modifier = modifier
    ) {
        items(players) { player ->
            PlayerSelectionCard(
                player = player,
                onClick = { onPlayerSelected(player) }
            )
        }
    }
}
