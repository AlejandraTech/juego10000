package com.alejandrapazrivas.juego10000.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alejandrapazrivas.juego10000.ui.home.components.GameHeader
import com.alejandrapazrivas.juego10000.ui.home.components.GameModeSelectionDialog
import com.alejandrapazrivas.juego10000.ui.home.components.MainMenuOptions
import com.alejandrapazrivas.juego10000.ui.home.components.PlayerSelectionDialog
import com.alejandrapazrivas.juego10000.ui.home.components.StartGameButton
import com.alejandrapazrivas.juego10000.ui.common.theme.Juego10000Theme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGame: (Long) -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToRules: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estados para controlar la animación de aparición de los elementos
    var showHeader by remember { mutableStateOf(false) }
    var showMenuOptions by remember { mutableStateOf(false) }
    var showStartButton by remember { mutableStateOf(false) }
    
    // Efecto para animar la aparición secuencial de los elementos
    LaunchedEffect(key1 = true) {
        showHeader = true
        delay(200)
        showMenuOptions = true
        delay(400)
        showStartButton = true
    }
    
    // Animación de escala para el botón de inicio
    val startButtonScale by animateFloatAsState(
        targetValue = if (showStartButton) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    // Mostrar mensaje de error
    uiState.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            viewModel.clearErrorMessage()
        }
    }

    Juego10000Theme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { /* Título vacío para mantener el diseño centrado */ },
                    actions = {
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configuración",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    )
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
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedVisibility(
                            visible = showHeader,
                            enter = fadeIn(animationSpec = tween(700)) +
                                    slideInVertically(
                                        initialOffsetY = { -100 },
                                        animationSpec = tween(700)
                                    )
                        ) {
                            GameHeader(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        AnimatedVisibility(
                            visible = showMenuOptions,
                            enter = fadeIn(animationSpec = tween(700)) +
                                    slideInVertically(
                                        initialOffsetY = { 100 },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                        ) {
                            MainMenuOptions(
                                onNavigateToGame = { viewModel.onNewGameClick() },
                                onNavigateToPlayers = onNavigateToPlayers,
                                onNavigateToRules = onNavigateToRules,
                                onNavigateToStats = onNavigateToStats,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                                .graphicsLayer {
                                    scaleX = startButtonScale
                                    scaleY = startButtonScale
                                    alpha = startButtonScale
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            StartGameButton(
                                enabled = true,
                                onStartGame = {
                                    viewModel.onNewGameClick()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de selección de modo de juego
    if (uiState.showGameModeDialog) {
        GameModeSelectionDialog(
            onDismiss = { viewModel.onGameModeDialogDismiss() },
            onMultiplayerSelected = { viewModel.onMultiplayerModeSelected() },
            onSinglePlayerSelected = { difficulty -> viewModel.onSinglePlayerModeSelected(difficulty) }
        )
    }

    // Diálogo de selección de jugadores
    if (uiState.showPlayerSelectionDialog) {
        PlayerSelectionDialog(
            availablePlayers = uiState.availablePlayers,
            selectedPlayers = uiState.selectedPlayers,
            onPlayerSelected = { player -> viewModel.onPlayerSelected(player) },
            onDismiss = { viewModel.onPlayerSelectionDialogDismiss() },
            onConfirm = {
                viewModel.createNewGame()
                viewModel.onPlayerSelectionDialogDismiss()
            },
            isSinglePlayerMode = uiState.isSinglePlayerMode
        )
    }

    LaunchedEffect(uiState.currentGameId) {
        if (uiState.currentGameId > 0) {
            onNavigateToGame(uiState.currentGameId)
        }
    }
}
