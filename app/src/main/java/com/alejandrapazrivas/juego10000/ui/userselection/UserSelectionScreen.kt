package com.alejandrapazrivas.juego10000.ui.userselection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

@Composable
fun UserSelectionScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    viewModel: UserSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPlayers()
    }

    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            viewModel.onNavigationHandled()
            onNavigateToHome()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                UserSelectionContent(
                    players = uiState.players,
                    showCreatePlayerHint = uiState.showCreatePlayerHint,
                    onPlayerSelected = { viewModel.selectUser(it) },
                    onCreatePlayer = onNavigateToPlayers
                )
            }
        }
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
            .padding(dimensions.spaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(dimensions.buttonHeight))

        // Header
        Text(
            text = "Juego 10.000",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Text(
            text = "Selecciona tu perfil",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(dimensions.buttonHeight))

        if (showCreatePlayerHint || players.isEmpty()) {
            // Estado vacío - No hay jugadores
            EmptyPlayersState(onCreatePlayer = onCreatePlayer)
        } else {
            // Grid de jugadores
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(dimensions.spaceSmall),
                horizontalArrangement = Arrangement.spacedBy(dimensions.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(dimensions.spaceMedium),
                modifier = Modifier.weight(1f)
            ) {
                items(players) { player ->
                    PlayerSelectionCard(
                        player = player,
                        onClick = { onPlayerSelected(player) }
                    )
                }

                // Botón para añadir nuevo jugador
                item {
                    AddPlayerCard(onClick = onCreatePlayer)
                }
            }
        }
    }
}

@Composable
private fun PlayerSelectionCard(
    player: Player,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensions.spaceMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.spaceExtraSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(dimensions.avatarSizeLarge)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceSmall + dimensions.spaceExtraSmall))

            // Nombre
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(dimensions.spaceExtraSmall))

            // Estadísticas breves
            Text(
                text = "${player.gamesPlayed} partidas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun AddPlayerCard(
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensions.spaceMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de añadir
            Box(
                modifier = Modifier
                    .size(dimensions.avatarSizeLarge)
                    .clip(CircleShape)
                    .border(
                        width = dimensions.spaceExtraSmall / 2,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir jugador",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dimensions.spaceExtraLarge)
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceSmall + dimensions.spaceExtraSmall))

            Text(
                text = "Nuevo jugador",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(dimensions.spaceExtraSmall))

            Text(
                text = "Crear perfil",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun EmptyPlayersState(
    onCreatePlayer: () -> Unit
) {
    val dimensions = LocalDimensions.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.avatarSizeLarge + dimensions.buttonHeight)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(dimensions.avatarSizeMedium + dimensions.spaceExtraSmall),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        Text(
            text = "No hay jugadores",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Text(
            text = "Crea tu primer perfil para comenzar a jugar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

        Card(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clickable(onClick = onCreatePlayer),
            shape = RoundedCornerShape(dimensions.spaceMedium),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.spaceMedium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Crear jugador",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
