package com.bigotitech.rokub10000.presentation.feature.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.core.ads.AdConstants
import com.bigotitech.rokub10000.domain.model.BotDifficulty
import com.bigotitech.rokub10000.domain.model.Dice
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.presentation.common.components.ads.BannerAd
import com.bigotitech.rokub10000.presentation.common.components.backgrounds.AnimatedBackground
import com.bigotitech.rokub10000.presentation.common.components.backgrounds.BackgroundConfig
import com.bigotitech.rokub10000.presentation.common.components.dialog.BaseDialog
import com.bigotitech.rokub10000.presentation.common.components.dialog.DialogButtons
import com.bigotitech.rokub10000.presentation.common.components.dialog.DialogHeader
import com.bigotitech.rokub10000.presentation.common.components.indicator.GameIndicator
import com.bigotitech.rokub10000.presentation.common.components.toast.GameMessageHandler
import com.bigotitech.rokub10000.presentation.common.theme.ButtonShape
import com.bigotitech.rokub10000.presentation.common.theme.CardShape
import com.bigotitech.rokub10000.presentation.common.theme.GoldColor
import com.bigotitech.rokub10000.presentation.common.theme.LocalDimensions
import com.bigotitech.rokub10000.presentation.common.theme.LocalWindowInfo
import com.bigotitech.rokub10000.presentation.common.theme.Primary
import com.bigotitech.rokub10000.presentation.common.theme.ScreenOrientation
import com.bigotitech.rokub10000.presentation.common.theme.Secondary
import com.bigotitech.rokub10000.presentation.common.util.getDiceDrawable
import com.bigotitech.rokub10000.presentation.feature.game.state.GameUiState
import com.bigotitech.rokub10000.presentation.feature.winner.GameVictoryScreen
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val APP_BAR_ELEVATION = 4.dp
private val AccentColor = Color(0xFFFFD700)

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

    // Manejar ciclo de vida para pausar/reanudar el juego
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.pauseGame()
                Lifecycle.Event.ON_RESUME -> viewModel.resumeGame()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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

// ============================================================================
// Top App Bar
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameTopAppBar(
    title: String,
    onBackClick: () -> Unit
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.shadow(elevation = APP_BAR_ELEVATION)
    )
}

// ============================================================================
// Round Indicator
// ============================================================================

@Composable
private fun RoundIndicator(
    round: Int,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier
            .padding(vertical = dimensions.spaceSmall)
            .clip(RoundedCornerShape(40))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceExtraSmall)
    ) {
        Text(
            text = stringResource(R.string.round, round),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = dimensions.spaceSmall)
        )
    }
}

// ============================================================================
// Scoreboard
// ============================================================================

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
private fun ScoreboardSection(
    players: List<Player>,
    playerScores: Map<Long, Int>,
    currentPlayerIndex: Int,
    isSinglePlayerMode: Boolean = false,
    botDifficulty: BotDifficulty? = null,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val scrollState = rememberScrollState()

    val maxPlayersHeight = dimensions.scoreboardMaxHeight
    val playerRowHeight = dimensions.scoreboardPlayerRowHeight.value.toInt()

    LaunchedEffect(currentPlayerIndex) {
        if (players.size > 2) {
            val targetScroll = (currentPlayerIndex * playerRowHeight).coerceAtLeast(0)
            scrollState.animateScrollTo(targetScroll)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.scoreboard),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = dimensions.titleFontSize),
                fontWeight = FontWeight.Bold,
                color = Primary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        HorizontalDivider(
            modifier = Modifier.padding(bottom = dimensions.spaceSmall),
            color = Primary.copy(alpha = 0.2f),
            thickness = 1.dp
        )

        val sortedPlayers = players.mapIndexed { index, player ->
            Triple(player, playerScores[player.id] ?: 0, index)
        }.sortedByDescending { it.second }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (players.size > 2) {
                        Modifier
                            .heightIn(max = maxPlayersHeight)
                            .verticalScroll(scrollState)
                    } else {
                        Modifier
                    }
                )
        ) {
            players.forEachIndexed { index, player ->
                val isCurrentPlayer = index == currentPlayerIndex
                val playerScore = playerScores[player.id] ?: 0
                val position = sortedPlayers.indexOfFirst { it.first.id == player.id } + 1

                PlayerScoreRow(
                    player = player,
                    score = playerScore,
                    isCurrentPlayer = isCurrentPlayer,
                    position = position,
                    botDifficulty = if (player.name == "Bot") botDifficulty else null
                )
            }
        }
    }
}

@Composable
private fun PlayerScoreRow(
    player: Player,
    score: Int,
    isCurrentPlayer: Boolean,
    position: Int,
    botDifficulty: BotDifficulty? = null,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val infiniteTransition = rememberInfiniteTransition(label = "current_player")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val progress = (score / 10000f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .scale(if (isCurrentPlayer) pulseScale else 1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isCurrentPlayer) 3.dp else 0.dp,
                    shape = RoundedCornerShape(dimensions.spaceSmall),
                    spotColor = if (isCurrentPlayer) Primary.copy(alpha = 0.3f) else Color.Transparent
                )
                .background(
                    brush = if (isCurrentPlayer) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.15f),
                                Primary.copy(alpha = 0.08f)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    shape = RoundedCornerShape(dimensions.spaceSmall)
                )
                .then(
                    if (isCurrentPlayer) {
                        Modifier.border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Primary, Primary.copy(alpha = 0.5f))
                            ),
                            shape = RoundedCornerShape(dimensions.spaceSmall)
                        )
                    } else Modifier
                )
                .padding(horizontal = dimensions.spaceSmall, vertical = dimensions.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(dimensions.scoreboardAvatarSize)
                    .clip(CircleShape)
                    .background(
                        brush = if (isCurrentPlayer) {
                            Brush.radialGradient(
                                colors = listOf(Primary, Primary.copy(alpha = 0.7f))
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                val initial = if (player.name == "Bot") "B" else player.name.firstOrNull()?.uppercase() ?: "?"
                Text(
                    text = initial,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentPlayer) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(dimensions.spaceSmall))

            // Name and Progress
            Column(modifier = Modifier.weight(1f)) {
                val displayName = if (player.name == "Bot" && botDifficulty != null) {
                    "Bot ${
                        when (botDifficulty) {
                            BotDifficulty.BEGINNER -> stringResource(R.string.bot_beginner)
                            BotDifficulty.INTERMEDIATE -> stringResource(R.string.bot_intermediate)
                            BotDifficulty.EXPERT -> stringResource(R.string.bot_expert)
                        }
                    }"
                } else {
                    player.name
                }

                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCurrentPlayer) Primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (isCurrentPlayer) Primary else Secondary.copy(alpha = 0.6f),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.width(dimensions.spaceSmall))

            // Score
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCurrentPlayer) Primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.score_target),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ============================================================================
// Dice Area
// ============================================================================

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

@Composable
private fun GameStartMessage(
    currentPlayer: Player?,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentPlayer?.let {
                stringResource(R.string.player_turn, it.name)
            } ?: stringResource(R.string.select_dice_to_keep),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        Text(
            text = stringResource(R.string.roll_dice_instruction),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun BotTurnIndicator(
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.bot_turn),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(dimensions.progressIndicatorSize),
            strokeWidth = 4.dp
        )
    }
}

// ============================================================================
// Dice Section
// ============================================================================

@Composable
private fun DiceSection(
    dice: List<Dice>,
    onDiceClick: (Dice) -> Unit,
    isRolling: Boolean,
    modifier: Modifier = Modifier,
    showTurnLostIndicator: Boolean = false,
    showPointsSavedIndicator: Boolean = false,
    showScoreExceededIndicator: Boolean = false
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DiceGrid(
            dice = dice,
            onDiceClick = onDiceClick,
            isRolling = isRolling
        )

        TurnIndicators(
            showTurnLostIndicator = showTurnLostIndicator,
            showPointsSavedIndicator = showPointsSavedIndicator,
            showScoreExceededIndicator = showScoreExceededIndicator
        )
    }
}

@Composable
private fun DiceGrid(
    dice: List<Dice>,
    onDiceClick: (Dice) -> Unit,
    isRolling: Boolean,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (dice.isNotEmpty()) {
            val firstRow = dice.take(3)
            val secondRow = dice.drop(3)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                firstRow.forEach { die ->
                    DiceView(
                        dice = die,
                        onClick = { onDiceClick(die) },
                        isRolling = isRolling
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.diceSpacing))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                secondRow.forEach { die ->
                    DiceView(
                        dice = die,
                        onClick = { onDiceClick(die) },
                        isRolling = isRolling
                    )
                }
            }
        }
    }
}

@Composable
private fun TurnIndicators(
    showTurnLostIndicator: Boolean,
    showPointsSavedIndicator: Boolean,
    showScoreExceededIndicator: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        GameIndicator(
            visible = showTurnLostIndicator,
            icon = Icons.Default.Close,
            title = stringResource(R.string.turn_lost_title),
            message = stringResource(R.string.turn_lost_message),
            isError = true
        )

        GameIndicator(
            visible = showPointsSavedIndicator,
            icon = Icons.Default.Check,
            title = stringResource(R.string.points_saved_title),
            isError = false
        )

        GameIndicator(
            visible = showScoreExceededIndicator,
            icon = Icons.Default.Warning,
            title = stringResource(R.string.score_exceeded_title),
            message = stringResource(R.string.score_exceeded_message),
            detailMessage = stringResource(R.string.score_exceeded_detail),
            isError = true
        )
    }
}

@Composable
private fun DiceView(
    dice: Dice,
    onClick: () -> Unit,
    isRolling: Boolean,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val diceResource = getDiceDrawable(dice.value)

    val infiniteTransition = rememberInfiniteTransition(label = "diceRoll")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.9f
            dice.isSelected || dice.isLocked -> 1.08f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val stateColor = when {
        dice.isLocked -> Primary
        dice.isSelected -> Secondary
        else -> Color.Transparent
    }

    Box(
        modifier = modifier.size(dimensions.diceSize + 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect
        if ((dice.isSelected || dice.isLocked) && !isRolling) {
            Box(
                modifier = Modifier
                    .size(dimensions.diceSize + 12.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                stateColor.copy(alpha = glowAlpha * 0.4f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(dimensions.diceCornerRadius + 4.dp)
                    )
            )
        }

        // Dice body
        Box(
            modifier = Modifier
                .size(dimensions.diceSize)
                .rotate(if (isRolling) rotation else 0f)
                .scale(scale)
                .shadow(
                    elevation = when {
                        dice.isLocked -> 8.dp
                        dice.isSelected -> 6.dp
                        else -> 3.dp
                    },
                    shape = RoundedCornerShape(dimensions.diceCornerRadius),
                    spotColor = stateColor.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(dimensions.diceCornerRadius))
                .background(brush = getDiceBackgroundBrush(dice))
                .then(
                    if (dice.isSelected || dice.isLocked) {
                        Modifier.border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    stateColor,
                                    stateColor.copy(alpha = 0.5f)
                                )
                            ),
                            shape = RoundedCornerShape(dimensions.diceCornerRadius)
                        )
                    } else Modifier
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !isRolling && !dice.isLocked
                ) { onClick() }
                .padding(dimensions.spaceExtraSmall),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = diceResource),
                contentDescription = stringResource(R.string.dice_content_description, dice.value),
                modifier = Modifier.size(dimensions.diceIconSize),
                tint = Color.Unspecified
            )
        }

        // Locked indicator
        if (dice.isLocked && !isRolling) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun getDiceBackgroundBrush(dice: Dice): Brush {
    return when {
        dice.isLocked -> Brush.linearGradient(
            colors = listOf(
                Primary.copy(alpha = 0.15f),
                Primary.copy(alpha = 0.25f)
            )
        )
        dice.isSelected -> Brush.linearGradient(
            colors = listOf(
                Secondary.copy(alpha = 0.15f),
                GoldColor.copy(alpha = 0.2f)
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surface
            )
        )
    }
}

// ============================================================================
// Score Display
// ============================================================================

@Composable
private fun ScoreDisplayCard(
    currentScore: Int,
    scoreScale: Float,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spaceExtraSmall)
            .scale(scoreScale),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (currentScore > 0) 8.dp else 2.dp,
                    shape = CardShape,
                    spotColor = Primary.copy(alpha = 0.3f)
                ),
            shape = CardShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            val hasScore = currentScore > 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = if (hasScore) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Primary,
                                    Primary.copy(alpha = 0.9f),
                                    AccentColor.copy(alpha = 0.6f)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                                )
                            )
                        }
                    )
                    .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.current_score),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (hasScore) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "$currentScore",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (hasScore) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// ============================================================================
// Action Buttons
// ============================================================================

@Composable
private fun GameActionButtons(
    canRoll: Boolean,
    canBank: Boolean,
    isGameOver: Boolean,
    scoreExceeded: Boolean,
    minimumPointsNeeded: Boolean,
    onRollClick: () -> Unit,
    onBankClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    val buttonColor = when {
        isGameOver -> MaterialTheme.colorScheme.tertiary
        scoreExceeded -> MaterialTheme.colorScheme.error
        else -> Secondary
    }

    val buttonText = when {
        isGameOver -> stringResource(R.string.end_game)
        scoreExceeded -> stringResource(R.string.pass_turn)
        minimumPointsNeeded -> stringResource(R.string.minimum_points_required)
        else -> stringResource(R.string.bank_score)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spaceSmall),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
    ) {
        // Roll Dice Button
        Button(
            onClick = onRollClick,
            modifier = Modifier
                .weight(1f)
                .height(dimensions.buttonHeight)
                .shadow(
                    elevation = if (canRoll) 8.dp else 2.dp,
                    shape = ButtonShape,
                    spotColor = Primary.copy(alpha = 0.4f)
                ),
            shape = ButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                disabledContainerColor = Primary.copy(alpha = 0.3f)
            ),
            enabled = canRoll
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dice),
                    contentDescription = null,
                    modifier = Modifier.size(dimensions.iconSizeSmall),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.size(dimensions.spaceSmall))
                Text(
                    text = stringResource(R.string.roll_dice),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Bank Score Button
        Button(
            onClick = onBankClick,
            modifier = Modifier
                .weight(1f)
                .height(dimensions.buttonHeight)
                .shadow(
                    elevation = if (canBank) 8.dp else 2.dp,
                    shape = ButtonShape,
                    spotColor = buttonColor.copy(alpha = 0.4f)
                ),
            shape = ButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                disabledContainerColor = buttonColor.copy(alpha = 0.3f)
            ),
            enabled = canBank
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(dimensions.iconSizeSmall),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.size(dimensions.spaceSmall))
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}

// ============================================================================
// Exit Confirmation Dialog
// ============================================================================

@Composable
private fun ExitConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val dimensions = LocalDimensions.current

    BaseDialog(onDismiss = onDismiss) {
        DialogHeader(
            title = stringResource(R.string.confirm),
            description = stringResource(R.string.confirm_exit_game),
            icon = Icons.AutoMirrored.Rounded.ExitToApp,
            iconTint = MaterialTheme.colorScheme.error,
            iconBackgroundColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        )

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        DialogButtons(
            onCancel = onDismiss,
            onConfirm = onConfirm,
            confirmText = stringResource(R.string.yes),
            confirmColor = MaterialTheme.colorScheme.error,
            confirmTextColor = MaterialTheme.colorScheme.onError
        )
    }
}
