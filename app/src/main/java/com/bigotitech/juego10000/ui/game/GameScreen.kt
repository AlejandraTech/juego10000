package com.bigotitech.juego10000.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.ads.AdConstants
import com.bigotitech.juego10000.domain.model.BotDifficulty
import com.bigotitech.juego10000.domain.model.Dice
import com.bigotitech.juego10000.domain.model.Player
import com.bigotitech.juego10000.ui.common.components.ads.BannerAd
import com.bigotitech.juego10000.ui.common.components.backgrounds.AnimatedBackground
import com.bigotitech.juego10000.ui.common.components.backgrounds.BackgroundConfig
import com.bigotitech.juego10000.ui.common.components.toast.GameMessageHandler
import com.bigotitech.juego10000.ui.common.theme.CardShape
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions
import com.bigotitech.juego10000.ui.common.theme.LocalWindowInfo
import com.bigotitech.juego10000.ui.common.theme.ScreenOrientation
import com.bigotitech.juego10000.ui.game.components.BotTurnIndicator
import com.bigotitech.juego10000.ui.game.components.DiceSection
import com.bigotitech.juego10000.ui.game.components.ExitConfirmationDialog
import com.bigotitech.juego10000.ui.game.components.GameActionButtons
import com.bigotitech.juego10000.ui.game.components.GameStartMessage
import com.bigotitech.juego10000.ui.game.components.GameTopAppBar
import com.bigotitech.juego10000.ui.game.components.RoundIndicator
import com.bigotitech.juego10000.ui.game.components.ScoreDisplayCard
import com.bigotitech.juego10000.ui.game.components.ScoreboardSection
import com.bigotitech.juego10000.ui.game.model.GameUiState
import com.bigotitech.juego10000.ui.gamewinner.GameVictoryScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    navController: NavController,
    gameId: Long,
    viewModel: GameViewModel = hiltViewModel()
) {
    val dimensions = LocalDimensions.current
    val windowInfo = LocalWindowInfo.current
    val isLandscape = windowInfo.screenOrientation == ScreenOrientation.Landscape

    val gameState by viewModel.gameState.collectAsState()
    val scope = rememberCoroutineScope()
    val isBotTurn by viewModel.isBotTurn.collectAsState()
    val botActionInProgress by viewModel.botActionInProgress.collectAsState()

    var showTurnLostIndicator by remember { mutableStateOf(false) }
    var showPointsSavedIndicator by remember { mutableStateOf(false) }
    var showScoreExceededIndicator by remember { mutableStateOf(false) }
    var showExitConfirmationDialog by remember { mutableStateOf(false) }

    val contentVisible = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) { contentVisible.targetState = true }

    val navigateToHome = {
        viewModel.exitGame()
        navController.navigate("home") {
            popUpTo("home") { inclusive = true }
        }
    }

    val scoreScale by animateFloatAsState(
        targetValue = if (gameState.currentTurnScore > 0) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scoreScaleAnimation"
    )

    GameStateEffects(
        gameState = gameState,
        viewModel = viewModel,
        onTurnLost = { showTurnLostIndicator = it },
        onPointsSaved = { showPointsSavedIndicator = it },
        onScoreExceeded = { showScoreExceededIndicator = it },
        scope = scope
    )

    if (gameState.isGameOver && gameState.winner != null) {
        GameVictoryScreen(
            winner = gameState.winner,
            score = gameState.winner?.id?.let { gameState.playerScores[it] } ?: 0,
            onBackToHome = navigateToHome,
            adManager = viewModel.adManager
        )
        return
    }

    BackHandler {
        showExitConfirmationDialog = true
    }

    if (showExitConfirmationDialog) {
        ExitConfirmationDialog(
            onDismiss = { showExitConfirmationDialog = false },
            onConfirm = {
                showExitConfirmationDialog = false
                navigateToHome()
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GameTopAppBar(
                title = stringResource(R.string.app_name),
                onBackClick = { showExitConfirmationDialog = true }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedBackground(config = BackgroundConfig.gameConfig)

            AnimatedVisibility(
                visibleState = contentVisible,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { it / 3 }
                        )
            ) {
                GameContent(
                    gameState = gameState,
                    isBotTurn = isBotTurn,
                    scoreScale = scoreScale,
                    showTurnLostIndicator = showTurnLostIndicator,
                    showPointsSavedIndicator = showPointsSavedIndicator,
                    showScoreExceededIndicator = showScoreExceededIndicator,
                    onDiceClick = { dice ->
                        if (!isBotTurn) viewModel.onDiceClick(dice)
                    },
                    onRollClick = { viewModel.onRollClick() },
                    onBankClick = {
                        when {
                            gameState.isGameOver -> navigateToHome()
                            gameState.scoreExceeded -> viewModel.onNextPlayerClick()
                            else -> viewModel.onBankClick()
                        }
                    },
                    onMessageShown = { viewModel.clearMessage() }
                )
            }
        }
    }
}

@Composable
private fun GameStateEffects(
    gameState: GameUiState,
    viewModel: GameViewModel,
    onTurnLost: (Boolean) -> Unit,
    onPointsSaved: (Boolean) -> Unit,
    onScoreExceeded: (Boolean) -> Unit,
    scope: CoroutineScope
) {
    LaunchedEffect(gameState.currentPlayerIndex) {
        viewModel.resetSelectedDiceChangedFlag()
    }

    LaunchedEffect(gameState.message) {
        onTurnLost(false)
        onPointsSaved(false)
        onScoreExceeded(false)

        val message = gameState.message ?: return@LaunchedEffect

        when {
            message.contains("sin puntuación", ignoreCase = true) ||
                    message.contains("perdido el turno", ignoreCase = true) -> {
                onTurnLost(true)
            }
            message.contains("guardado", ignoreCase = true) &&
                    message.contains("puntos", ignoreCase = true) -> {
                onPointsSaved(true)
            }
            message.contains("superado los 10,000", ignoreCase = true) -> {
                onScoreExceeded(true)
            }
        }

        scope.launch {
            delay(2000)
            onTurnLost(false)
            onPointsSaved(false)
            onScoreExceeded(false)
        }
    }

    LaunchedEffect(gameState.scoreExceeded) {
        if (gameState.scoreExceeded) {
            onScoreExceeded(true)
            scope.launch {
                delay(2000)
                onScoreExceeded(false)
            }
        }
    }

    LaunchedEffect(gameState.currentTurnScore) {
        val currentPlayer = gameState.currentPlayer
        val playerScore = currentPlayer?.let { gameState.playerScores[it.id] } ?: 0
        val totalScore = playerScore + gameState.currentTurnScore

        if (gameState.gameStarted &&
            currentPlayer != null &&
            gameState.playersInGame[currentPlayer.id] == true &&
            totalScore > 10000 &&
            gameState.currentTurnScore > 0
        ) {
            viewModel.setScoreExceeded(true)
            if (gameState.message == null) {
                viewModel.showScoreExceededMessage()
            }
        }
    }
}

@Composable
private fun GameContent(
    gameState: GameUiState,
    isBotTurn: Boolean,
    scoreScale: Float,
    showTurnLostIndicator: Boolean,
    showPointsSavedIndicator: Boolean,
    showScoreExceededIndicator: Boolean,
    onDiceClick: (Dice) -> Unit,
    onRollClick: () -> Unit,
    onBankClick: () -> Unit,
    onMessageShown: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensions.screenPaddingHorizontal,
                vertical = dimensions.screenPaddingVertical
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoundIndicator(round = gameState.currentRound)

        ScoreboardCard(
            players = gameState.players,
            playerScores = gameState.playerScores,
            currentPlayerIndex = gameState.currentPlayerIndex,
            isSinglePlayerMode = gameState.isSinglePlayerMode,
            botDifficulty = gameState.botDifficulty
        )

        DiceAreaCard(
            gameState = gameState,
            isBotTurn = isBotTurn,
            showTurnLostIndicator = showTurnLostIndicator,
            showPointsSavedIndicator = showPointsSavedIndicator,
            showScoreExceededIndicator = showScoreExceededIndicator,
            onDiceClick = onDiceClick,
            onMessageShown = onMessageShown,
            modifier = Modifier.weight(1f)
        )

        ScoreDisplayCard(
            currentScore = gameState.currentTurnScore,
            scoreScale = scoreScale
        )

        GameActionButtons(
            canRoll = gameState.canRoll && !gameState.scoreExceeded && !gameState.isGameOver && !isBotTurn,
            canBank = (gameState.canBank || gameState.scoreExceeded || gameState.isGameOver) && (!isBotTurn || gameState.isGameOver),
            isGameOver = gameState.isGameOver,
            scoreExceeded = gameState.scoreExceeded,
            minimumPointsNeeded = run {
                val currentPlayer = gameState.currentPlayer
                val isPlayerInGame = currentPlayer?.let { gameState.playersInGame[it.id] } ?: false
                val isBot = currentPlayer?.name == "Bot"
                !isPlayerInGame && gameState.currentTurnScore < 500 && !isBot
            },
            onRollClick = onRollClick,
            onBankClick = onBankClick
        )

        BannerAd(
            adUnitId = AdConstants.BANNER_GAME,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensions.spaceSmall)
        )
    }
}

@Composable
private fun ScoreboardCard(
    players: List<Player>,
    playerScores: Map<Long, Int>,
    currentPlayerIndex: Int,
    isSinglePlayerMode: Boolean,
    botDifficulty: BotDifficulty?
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spaceSmall)
            .graphicsLayer {
                shadowElevation = 6f
                shape = RoundedCornerShape(dimensions.cardCornerRadius)
            },
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        ScoreboardSection(
            players = players,
            playerScores = playerScores,
            currentPlayerIndex = currentPlayerIndex,
            isSinglePlayerMode = isSinglePlayerMode,
            botDifficulty = botDifficulty,
            modifier = Modifier.padding(dimensions.cardPadding)
        )
    }
}

@Composable
private fun DiceAreaCard(
    gameState: GameUiState,
    isBotTurn: Boolean,
    showTurnLostIndicator: Boolean,
    showPointsSavedIndicator: Boolean,
    showScoreExceededIndicator: Boolean,
    onDiceClick: (Dice) -> Unit,
    onMessageShown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spaceSmall)
            .graphicsLayer {
                shadowElevation = 12f
                shape = CardShape
            },
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensions.screenPaddingHorizontal),
            contentAlignment = Alignment.Center
        ) {
            when {
                !gameState.gameStarted && !isBotTurn -> {
                    GameStartMessage(currentPlayer = gameState.currentPlayer)
                }
                isBotTurn && gameState.dice.isEmpty() -> {
                    BotTurnIndicator()
                }
                else -> {
                    DiceAreaContent(
                        gameState = gameState,
                        isBotTurn = isBotTurn,
                        showTurnLostIndicator = showTurnLostIndicator,
                        showPointsSavedIndicator = showPointsSavedIndicator,
                        showScoreExceededIndicator = showScoreExceededIndicator,
                        onDiceClick = onDiceClick,
                        onMessageShown = onMessageShown
                    )
                }
            }
        }
    }
}

@Composable
private fun DiceAreaContent(
    gameState: GameUiState,
    isBotTurn: Boolean,
    showTurnLostIndicator: Boolean,
    showPointsSavedIndicator: Boolean,
    showScoreExceededIndicator: Boolean,
    onDiceClick: (Dice) -> Unit,
    onMessageShown: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DiceSection(
            dice = gameState.dice,
            onDiceClick = onDiceClick,
            isRolling = gameState.isRolling,
            showTurnLostIndicator = showTurnLostIndicator,
            showPointsSavedIndicator = showPointsSavedIndicator,
            showScoreExceededIndicator = showScoreExceededIndicator
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.screenPaddingHorizontal)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.TopCenter
        ) {
            GameMessageHandler(
                message = gameState.message,
                onMessageShown = onMessageShown
            )
        }
    }
}
