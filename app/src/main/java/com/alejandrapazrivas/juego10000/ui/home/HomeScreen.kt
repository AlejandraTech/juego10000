package com.alejandrapazrivas.juego10000.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.home.components.GameModeSelectionDialog
import com.alejandrapazrivas.juego10000.ui.home.components.HomeDrawerContent
import com.alejandrapazrivas.juego10000.ui.home.components.LastGameCard
import com.alejandrapazrivas.juego10000.ui.home.components.MiniPerformanceChart
import com.alejandrapazrivas.juego10000.ui.home.components.PlayGameCard
import com.alejandrapazrivas.juego10000.ui.home.components.PlayerSelectionDialog
import com.alejandrapazrivas.juego10000.ui.home.components.QuickStatsSection
import com.alejandrapazrivas.juego10000.ui.common.theme.Juego10000Theme
import com.alejandrapazrivas.juego10000.ads.AdConstants
import com.alejandrapazrivas.juego10000.ui.common.components.ads.BannerAd
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGame: (Long) -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToRules: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToUserSelection: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val dimensions = LocalDimensions.current
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estados para controlar la animación de aparición de los elementos
    var showPlayCard by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showChart by remember { mutableStateOf(false) }
    var showLastGame by remember { mutableStateOf(false) }

    // Efecto para animar la aparición secuencial de los elementos
    LaunchedEffect(key1 = true) {
        showPlayCard = true
        delay(150)
        showStats = true
        delay(150)
        showChart = true
        delay(150)
        showLastGame = true
    }

    // Mostrar mensaje de error
    uiState.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            viewModel.clearErrorMessage()
        }
    }

    Juego10000Theme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                HomeDrawerContent(
                    currentUserName = uiState.currentUser?.name,
                    onNavigateToPlayers = onNavigateToPlayers,
                    onNavigateToStats = onNavigateToStats,
                    onNavigateToRules = onNavigateToRules,
                    onNavigateToSettings = onNavigateToSettings,
                    onChangeUser = {
                        scope.launch { drawerState.close() }
                        onNavigateToUserSelection()
                    },
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = stringResource(R.string.menu)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
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
                                .padding(horizontal = dimensions.screenPaddingHorizontal)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                            // Card principal para jugar
                            AnimatedVisibility(
                                visible = showPlayCard,
                                enter = fadeIn(animationSpec = tween(500)) +
                                        slideInVertically(
                                            initialOffsetY = { -50 },
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                            ) {
                                PlayGameCard(
                                    onPlayClick = { viewModel.onNewGameClick() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(dimensions.spaceLarge))

                            // Sección de estadísticas rápidas
                            AnimatedVisibility(
                                visible = showStats,
                                enter = fadeIn(animationSpec = tween(500)) +
                                        slideInVertically(
                                            initialOffsetY = { 50 },
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                            ) {
                                QuickStatsSection(
                                    stats = uiState.userStats,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                            // Gráfica de rendimiento
                            AnimatedVisibility(
                                visible = showChart,
                                enter = fadeIn(animationSpec = tween(500)) +
                                        slideInVertically(
                                            initialOffsetY = { 50 },
                                            animationSpec = tween(500)
                                        )
                            ) {
                                MiniPerformanceChart(
                                    scores = uiState.recentScores,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                            // Última partida
                            AnimatedVisibility(
                                visible = showLastGame && uiState.lastGame != null,
                                enter = fadeIn(animationSpec = tween(500)) +
                                        slideInVertically(
                                            initialOffsetY = { 50 },
                                            animationSpec = tween(500)
                                        )
                            ) {
                                LastGameCard(
                                    lastGame = uiState.lastGame,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                            // Banner publicitario
                            BannerAd(
                                adUnitId = AdConstants.BANNER_HOME,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))
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

    // Diálogo de selección de oponentes (solo para multijugador)
    if (uiState.showPlayerSelectionDialog) {
        // Filtrar el usuario actual de la lista de jugadores disponibles
        val otherPlayers = uiState.availablePlayers.filter { it.id != uiState.currentUser?.id }

        PlayerSelectionDialog(
            availablePlayers = otherPlayers,
            selectedPlayers = uiState.selectedPlayers,
            onPlayerSelected = { player -> viewModel.onPlayerSelected(player) },
            onDismiss = { viewModel.onPlayerSelectionDialogDismiss() },
            onConfirm = {
                viewModel.createNewGame()
                viewModel.onPlayerSelectionDialogDismiss()
            }
        )
    }

    LaunchedEffect(uiState.currentGameId) {
        if (uiState.currentGameId > 0) {
            onNavigateToGame(uiState.currentGameId)
        }
    }
}
