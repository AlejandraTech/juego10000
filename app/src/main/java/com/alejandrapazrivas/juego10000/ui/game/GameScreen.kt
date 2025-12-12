package com.alejandrapazrivas.juego10000.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutQuad
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.math.sin
import androidx.navigation.NavController
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.game.components.DiceSection
import com.alejandrapazrivas.juego10000.ui.gamewinner.GameVictoryScreen
import com.alejandrapazrivas.juego10000.ui.common.components.toast.GameMessageHandler
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.ButtonShape
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalWindowInfo
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.ScreenOrientation
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary
import com.alejandrapazrivas.juego10000.ui.game.components.GameTopAppBar
import com.alejandrapazrivas.juego10000.ui.game.components.ScoreboardSection
import com.alejandrapazrivas.juego10000.ads.AdConstants
import com.alejandrapazrivas.juego10000.ui.common.components.ads.BannerAd
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

    val infiniteTransition = rememberInfiniteTransition(label = "backgroundAnimation")
    val backgroundAlpha by infiniteTransition.animateFloat(
        initialValue = 0.02f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "backgroundAlphaAnimation"
    )

    val scoreScale by animateFloatAsState(
        targetValue = if (gameState.currentTurnScore > 0) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scoreScaleAnimation"
    )

    LaunchedEffect(gameState.currentPlayerIndex) {
        viewModel.resetSelectedDiceChangedFlag()
    }

    LaunchedEffect(gameState.message) {
        showTurnLostIndicator = false
        showPointsSavedIndicator = false
        showScoreExceededIndicator = false

        val message = gameState.message ?: return@LaunchedEffect

        when {
            message.contains("sin puntuación", ignoreCase = true) ||
                    message.contains("perdido el turno", ignoreCase = true) -> {
                showTurnLostIndicator = true
            }
            message.contains("guardado", ignoreCase = true) &&
                    message.contains("puntos", ignoreCase = true) -> {
                showPointsSavedIndicator = true
            }
            message.contains("superado los 10,000", ignoreCase = true) -> {
                showScoreExceededIndicator = true
            }
        }

        if (showTurnLostIndicator || showPointsSavedIndicator || showScoreExceededIndicator) {
            scope.launch {
                delay(2000)
                if (showTurnLostIndicator) showTurnLostIndicator = false
                if (showPointsSavedIndicator) showPointsSavedIndicator = false
                if (showScoreExceededIndicator) showScoreExceededIndicator = false
            }
        }
    }

    LaunchedEffect(gameState.scoreExceeded) {
        if (gameState.scoreExceeded) {
            showScoreExceededIndicator = true
            scope.launch {
                delay(2000)
                showScoreExceededIndicator = false
            }
        }
    }

    if (gameState.isGameOver && gameState.winner != null) {
        GameVictoryScreen(
            winner = gameState.winner,
            score = gameState.winner?.id?.let { gameState.playerScores[it] } ?: 0,
            onBackToHome = navigateToHome,
            adManager = viewModel.adManager
        )
        return
    }

    // Interceptar botón de retroceso del sistema
    BackHandler {
        showExitConfirmationDialog = true
    }

    // Diálogo de confirmación de salida
    if (showExitConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmationDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.confirm),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.confirm_exit_game),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirmationDialog = false
                        navigateToHome()
                    }
                ) {
                    Text(text = stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitConfirmationDialog = false }
                ) {
                    Text(text = stringResource(R.string.no))
                }
            }
        )
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
            // Fondo animado mejorado
            GameAnimatedBackground(
                floatOffset = backgroundAlpha,
                primaryColor = Primary,
                secondaryColor = Secondary
            )

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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = dimensions.screenPaddingHorizontal, vertical = dimensions.screenPaddingVertical),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = dimensions.spaceSmall)
                            .clip(RoundedCornerShape(40))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceExtraSmall)
                    ) {
                        Text(
                            text = stringResource(R.string.round, gameState.currentRound),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = dimensions.spaceSmall)
                        )
                    }

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
                            players = gameState.players,
                            playerScores = gameState.playerScores,
                            currentPlayerIndex = gameState.currentPlayerIndex,
                            isSinglePlayerMode = gameState.isSinglePlayerMode,
                            botDifficulty = gameState.botDifficulty,
                            modifier = Modifier.padding(dimensions.cardPadding)
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .heightIn(min = dimensions.diceAreaMinHeight)
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
                            if (!gameState.gameStarted && !isBotTurn) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = gameState.currentPlayer?.let {
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
                            } else if (isBotTurn && gameState.dice.isEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
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
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    DiceSection(
                                        dice = gameState.dice,
                                        onDiceClick = { dice ->
                                            if (!isBotTurn) {
                                                viewModel.onDiceClick(dice)
                                            }
                                        },
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
                                            onMessageShown = { viewModel.clearMessage() }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Card de puntuación mejorada
                    ScoreDisplayCard(
                        currentScore = gameState.currentTurnScore,
                        scoreScale = scoreScale
                    )

                    // Botones de acción mejorados
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
                        onRollClick = { viewModel.onRollClick() },
                        onBankClick = {
                            when {
                                gameState.isGameOver -> navigateToHome()
                                gameState.scoreExceeded -> viewModel.onNextPlayerClick()
                                else -> viewModel.onBankClick()
                            }
                        }
                    )

                    // Banner publicitario
                    BannerAd(
                        adUnitId = AdConstants.BANNER_GAME,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensions.spaceSmall)
                    )
                }
            }
        }
    }
}

/**
 * Fondo animado para la pantalla de juego
 */
@Composable
private fun GameAnimatedBackground(
    floatOffset: Float,
    primaryColor: Color,
    secondaryColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "game_bg")

    val float1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val float2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val diceRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dice_rotation"
    )

    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor,
                        primaryColor.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Círculos decorativos flotantes
            val circles = listOf(
                Triple(0.08f, 0.1f, 60.dp.toPx()),
                Triple(0.92f, 0.15f, 45.dp.toPx()),
                Triple(0.85f, 0.75f, 70.dp.toPx()),
                Triple(0.1f, 0.65f, 50.dp.toPx())
            )

            circles.forEachIndexed { index, (xRatio, yRatio, baseRadius) ->
                val offsetMultiplier = if (index % 2 == 0) float1 else float2
                val yOffset = sin(offsetMultiplier * Math.PI).toFloat() * 20.dp.toPx()

                val color = if (index % 2 == 0) primaryColor else secondaryColor
                drawCircle(
                    color = color.copy(alpha = 0.04f + (floatOffset * 0.5f)),
                    radius = baseRadius + (offsetMultiplier * 10.dp.toPx()),
                    center = Offset(
                        x = width * xRatio,
                        y = height * yRatio + yOffset
                    )
                )
            }

            // Dados decorativos pequeños
            val dicePositions = listOf(
                Triple(0.05f, 0.25f, 16.dp.toPx()),
                Triple(0.95f, 0.4f, 14.dp.toPx()),
                Triple(0.08f, 0.8f, 18.dp.toPx()),
                Triple(0.9f, 0.85f, 12.dp.toPx())
            )

            dicePositions.forEachIndexed { index, (xRatio, yRatio, diceSize) ->
                val rotation = diceRotation + (index * 60f)
                val yOffset = sin((float1 + index * 0.25f) * Math.PI).toFloat() * 10.dp.toPx()

                rotate(rotation, pivot = Offset(width * xRatio, height * yRatio + yOffset)) {
                    drawRoundRect(
                        color = primaryColor.copy(alpha = 0.08f),
                        topLeft = Offset(
                            x = width * xRatio - diceSize / 2,
                            y = height * yRatio + yOffset - diceSize / 2
                        ),
                        size = androidx.compose.ui.geometry.Size(diceSize, diceSize),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                    )
                }
            }
        }
    }
}

/**
 * Card de visualización de puntuación mejorada
 */
@Composable
private fun ScoreDisplayCard(
    currentScore: Int,
    scoreScale: Float
) {
    val dimensions = LocalDimensions.current
    val infiniteTransition = rememberInfiniteTransition(label = "score_animation")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val accentColor = Color(0xFFFFD700) // Dorado

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spaceSmall)
            .scale(scoreScale),
        contentAlignment = Alignment.Center
    ) {
        // Efecto de glow cuando hay puntos
        if (currentScore > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensions.buttonHeight * 2 + 8.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Primary.copy(alpha = glowAlpha * 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CardShape
                    )
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (currentScore > 0) 12.dp else 4.dp,
                    shape = CardShape,
                    spotColor = Primary.copy(alpha = 0.3f)
                ),
            shape = CardShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = if (currentScore > 0) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Primary,
                                    Primary.copy(alpha = 0.9f),
                                    accentColor.copy(alpha = 0.7f)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                                )
                            )
                        }
                    )
                    .padding(dimensions.cardPadding)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.current_score),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (currentScore > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val textSize = when {
                        currentScore >= 1000 -> dimensions.scoreSmall
                        currentScore >= 100 -> dimensions.scoreMedium
                        else -> dimensions.scoreLarge
                    }

                    Text(
                        text = "$currentScore",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = textSize),
                        fontWeight = FontWeight.ExtraBold,
                        color = if (currentScore > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

/**
 * Botones de acción del juego mejorados
 */
@Composable
private fun GameActionButtons(
    canRoll: Boolean,
    canBank: Boolean,
    isGameOver: Boolean,
    scoreExceeded: Boolean,
    minimumPointsNeeded: Boolean,
    onRollClick: () -> Unit,
    onBankClick: () -> Unit
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spaceSmall),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
    ) {
        // Botón de lanzar dados
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

        // Botón de guardar/pasar
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
