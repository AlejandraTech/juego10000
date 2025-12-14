package com.bigotitech.rokub10000.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ads.AdConstants
import com.bigotitech.rokub10000.ui.common.components.ads.BannerAd
import com.bigotitech.rokub10000.ui.common.components.backgrounds.AnimatedBackground
import com.bigotitech.rokub10000.ui.common.theme.Juego10000Theme
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.home.components.GameModeSelectionDialog
import com.bigotitech.rokub10000.ui.home.components.HomeDrawerContent
import com.bigotitech.rokub10000.ui.home.components.PlayButton
import com.bigotitech.rokub10000.ui.home.components.PlayerSelectionDialog
import com.bigotitech.rokub10000.ui.home.components.QuickAccessSection
import com.bigotitech.rokub10000.ui.home.components.QuickStatsRow
import com.bigotitech.rokub10000.ui.home.components.UserGreetingHeader
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.domain.model.BotDifficulty
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

    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        delay(100)
        showContent = true
    }

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
                    HomeTopAppBar(
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedBackground()

                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn(animationSpec = tween(500)) +
                                slideInVertically(
                                    initialOffsetY = { 50 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                    ) {
                        HomeContent(
                            userName = uiState.currentUser?.name ?: "",
                            gamesPlayed = uiState.userStats.totalGamesPlayed,
                            wins = uiState.userStats.totalWins,
                            winRate = uiState.userStats.winRate.toInt(),
                            onPlayClick = { viewModel.onNewGameClick() },
                            onStatsClick = onNavigateToStats,
                            onRulesClick = onNavigateToRules,
                            onPlayersClick = onNavigateToPlayers,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(horizontal = dimensions.screenPaddingHorizontal)
                        )
                    }
                }
            }
        }
    }

    GameDialogs(
        showGameModeDialog = uiState.showGameModeDialog,
        showPlayerSelectionDialog = uiState.showPlayerSelectionDialog,
        availablePlayers = uiState.availablePlayers.filter { it.id != uiState.currentUser?.id },
        selectedPlayers = uiState.selectedPlayers,
        onGameModeDialogDismiss = { viewModel.onGameModeDialogDismiss() },
        onMultiplayerSelected = { viewModel.onMultiplayerModeSelected() },
        onSinglePlayerSelected = { difficulty -> viewModel.onSinglePlayerModeSelected(difficulty) },
        onPlayerSelected = { player -> viewModel.onPlayerSelected(player) },
        onPlayerSelectionDismiss = { viewModel.onPlayerSelectionDialogDismiss() },
        onPlayerSelectionConfirm = {
            viewModel.createNewGame()
            viewModel.onPlayerSelectionDialogDismiss()
        }
    )

    LaunchedEffect(uiState.currentGameId) {
        if (uiState.currentGameId > 0) {
            onNavigateToGame(uiState.currentGameId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
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

@Composable
private fun HomeContent(
    userName: String,
    gamesPlayed: Int,
    wins: Int,
    winRate: Int,
    onPlayClick: () -> Unit,
    onStatsClick: () -> Unit,
    onRulesClick: () -> Unit,
    onPlayersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        UserGreetingHeader(userName = userName)

        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

        PlayButton(onPlayClick = onPlayClick)

        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

        QuickStatsRow(
            gamesPlayed = gamesPlayed,
            wins = wins,
            winRate = winRate
        )

        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

        QuickAccessSection(
            onStatsClick = onStatsClick,
            onRulesClick = onRulesClick,
            onPlayersClick = onPlayersClick
        )

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        BannerAd(
            adUnitId = AdConstants.BANNER_HOME,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))
    }
}

@Composable
private fun GameDialogs(
    showGameModeDialog: Boolean,
    showPlayerSelectionDialog: Boolean,
    availablePlayers: List<Player>,
    selectedPlayers: List<Player>,
    onGameModeDialogDismiss: () -> Unit,
    onMultiplayerSelected: () -> Unit,
    onSinglePlayerSelected: (BotDifficulty) -> Unit,
    onPlayerSelected: (Player) -> Unit,
    onPlayerSelectionDismiss: () -> Unit,
    onPlayerSelectionConfirm: () -> Unit
) {
    if (showGameModeDialog) {
        GameModeSelectionDialog(
            onDismiss = onGameModeDialogDismiss,
            onMultiplayerSelected = onMultiplayerSelected,
            onSinglePlayerSelected = onSinglePlayerSelected
        )
    }

    if (showPlayerSelectionDialog) {
        PlayerSelectionDialog(
            availablePlayers = availablePlayers,
            selectedPlayers = selectedPlayers,
            onPlayerSelected = onPlayerSelected,
            onDismiss = onPlayerSelectionDismiss,
            onConfirm = onPlayerSelectionConfirm
        )
    }
}
