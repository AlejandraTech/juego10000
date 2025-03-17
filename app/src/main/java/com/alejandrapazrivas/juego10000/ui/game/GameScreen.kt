package com.alejandrapazrivas.juego10000.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutQuad
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.game.components.DiceSection
import com.alejandrapazrivas.juego10000.ui.gamewinner.GameVictoryScreen
import com.alejandrapazrivas.juego10000.ui.common.components.toast.GameMessageHandler
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.ButtonShape
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary
import com.alejandrapazrivas.juego10000.ui.game.components.GameTopAppBar
import com.alejandrapazrivas.juego10000.ui.game.components.ScoreboardSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Constantes para el componente GameScreen
private val ROUND_INDICATOR_CORNER_RADIUS = 40
private val CARD_CORNER_RADIUS = 14.dp
private val BUTTON_HEIGHT = 52.dp
private val DECORATIVE_CIRCLE_TOP_SIZE = 200.dp
private val DECORATIVE_CIRCLE_BOTTOM_SIZE = 180.dp
private val SCORE_TEXT_SIZE_LARGE = 40.sp
private val SCORE_TEXT_SIZE_MEDIUM = 36.sp
private val SCORE_TEXT_SIZE_SMALL = 32.sp

@Composable
fun GameScreen(
    navController: NavController,
    gameId: Long,
    viewModel: GameViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val scope = rememberCoroutineScope()

    // Estados y animaciones
    val isBotTurn by viewModel.isBotTurn.collectAsState()
    val botActionInProgress by viewModel.botActionInProgress.collectAsState()
    
    // Estados para los indicadores visuales
    var showTurnLostIndicator by remember { mutableStateOf(false) }
    var showPointsSavedIndicator by remember { mutableStateOf(false) }
    var showScoreExceededIndicator by remember { mutableStateOf(false) }

    // Estado para animar la aparición del contenido
    val contentVisible = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) { contentVisible.targetState = true }

    // Función para navegar a la pantalla de inicio
    val navigateToHome = {
        viewModel.exitGame()
        navController.navigate("home") {
            popUpTo("home") { inclusive = true }
        }
    }

    // Animaciones
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

    // Efectos para gestionar estados de la UI
    LaunchedEffect(gameState.currentPlayerIndex) {
        // Resetear estados cuando cambia el jugador
        viewModel.resetSelectedDiceChangedFlag()
        showTurnLostIndicator = false
        showPointsSavedIndicator = false
        showScoreExceededIndicator = false
    }

    // Gestión de indicadores visuales basados en mensajes
    LaunchedEffect(gameState.message) {
        val message = gameState.message ?: return@LaunchedEffect
        
        // Resetear todos los indicadores
        showTurnLostIndicator = false
        showPointsSavedIndicator = false
        showScoreExceededIndicator = false
        
        // Activar el indicador apropiado según el mensaje
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
        
        // Ocultar el indicador después de un tiempo
        if (showTurnLostIndicator || showPointsSavedIndicator || showScoreExceededIndicator) {
            scope.launch {
                delay(2000)
                if (showTurnLostIndicator) showTurnLostIndicator = false
                if (showPointsSavedIndicator) showPointsSavedIndicator = false
                if (showScoreExceededIndicator) showScoreExceededIndicator = false
            }
        }
    }

    // Detectar cuando se excede la puntuación
    LaunchedEffect(gameState.scoreExceeded) {
        if (gameState.scoreExceeded) {
            showScoreExceededIndicator = true
            scope.launch {
                delay(2000)
                showScoreExceededIndicator = false
            }
        }
    }

    // Pantalla de victoria si el juego ha terminado
    if (gameState.isGameOver && gameState.winner != null) {
        GameVictoryScreen(
            winner = gameState.winner,
            score = gameState.winner?.id?.let { gameState.playerScores[it] } ?: 0,
            onBackToHome = navigateToHome
        )
        return
    }

    // Verificar si se ha excedido el límite de puntuación
    LaunchedEffect(gameState.currentTurnScore) {
        val currentPlayer = gameState.currentPlayer
        val playerScore = currentPlayer?.let { gameState.playerScores[it.id] } ?: 0
        val totalScore = playerScore + gameState.currentTurnScore

        // Verificar si se excede la puntuación objetivo
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
                onBackClick = {
                    // Llamar a exitGame para eliminar el bot si es necesario
                    viewModel.exitGame()
                    
                    // Navegar a la pantalla de inicio
                    navController.navigate("home") {
                        // Limpiar el backstack para que no se pueda volver a esta pantalla
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    ) { paddingValues ->
        // Fondo con gradiente dinámico
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
            // Elementos decorativos de fondo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Círculo decorativo superior
                Box(
                    modifier = Modifier
                        .size(DECORATIVE_CIRCLE_TOP_SIZE)
                        .offset(x = (-30).dp, y = (-50).dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Primary.copy(alpha = backgroundAlpha),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .blur(radius = 40.dp)
                )
                
                // Círculo decorativo inferior
                Box(
                    modifier = Modifier
                        .size(DECORATIVE_CIRCLE_BOTTOM_SIZE)
                        .align(Alignment.BottomEnd)
                        .offset(x = 30.dp, y = 50.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Secondary.copy(alpha = backgroundAlpha),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .blur(radius = 35.dp)
                )
            }

            // Contenido principal con animación de entrada
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
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Título de la ronda con animación sutil
                    Box(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(ROUND_INDICATOR_CORNER_RADIUS))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.round, gameState.currentRound),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }

                    // Tabla de puntuaciones con efecto de elevación mejorado
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .graphicsLayer {
                                shadowElevation = 6f
                                shape = RoundedCornerShape(CARD_CORNER_RADIUS)
                            },
                        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        // Usar el componente ScoreboardSection
                        ScoreboardSection(
                            players = gameState.players,
                            playerScores = gameState.playerScores,
                            currentPlayerIndex = gameState.currentPlayerIndex,
                            isSinglePlayerMode = gameState.isSinglePlayerMode,
                            botDifficulty = gameState.botDifficulty,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    // Sección de dados con efecto de elevación mejorado
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 8.dp)
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
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!gameState.gameStarted && !isBotTurn) {
                                // Mensaje inicial cuando el juego no ha comenzado y no es turno del bot
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

                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Text(
                                        text = "Usa el botón 'Lanzar dados' para comenzar",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            } else if (isBotTurn && gameState.dice.isEmpty() && !botActionInProgress) {
                                // Mensaje cuando es turno del bot pero aún no ha lanzado
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Turno del Bot...",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Indicador de carga con animación
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(48.dp),
                                        strokeWidth = 4.dp
                                    )
                                }
                            } else {
                                // Sección de dados cuando el juego ha comenzado
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Componente de dados
                                    DiceSection(
                                        dice = gameState.dice,
                                        onDiceClick = { viewModel.onDiceClick(it) },
                                        isRolling = gameState.isRolling,
                                        showTurnLostIndicator = showTurnLostIndicator,
                                        showPointsSavedIndicator = showPointsSavedIndicator,
                                        showScoreExceededIndicator = showScoreExceededIndicator
                                    )

                                    // Gestor de mensajes encima de los dados
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
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

                    // Puntuación actual con animación de escala
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .graphicsLayer {
                                scaleX = scoreScale
                                scaleY = scoreScale
                                shadowElevation = 8f
                                shape = CardShape
                            },
                        shape = CardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Puntuación Actual",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            // Puntuación con tamaño dinámico según el valor
                            val textSize = when {
                                gameState.currentTurnScore >= 1000 -> SCORE_TEXT_SIZE_SMALL
                                gameState.currentTurnScore >= 100 -> SCORE_TEXT_SIZE_MEDIUM
                                else -> SCORE_TEXT_SIZE_LARGE
                            }

                            Text(
                                text = "${gameState.currentTurnScore}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontSize = textSize),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Botones de acción con efectos visuales mejorados
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón Lanzar Dados
                        Button(
                            onClick = { viewModel.onRollClick() },
                            modifier = Modifier
                                .weight(1f)
                                .height(BUTTON_HEIGHT)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = ButtonShape,
                                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                ),
                            shape = ButtonShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.3f
                                )
                            ),
                            enabled = gameState.canRoll && !gameState.scoreExceeded && !gameState.isGameOver
                        ) {
                            Text(
                                text = stringResource(R.string.roll_dice),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        // Botón de acción secundaria (Guardar/Pasar/Finalizar)
                        val currentPlayer = gameState.currentPlayer
                        val isPlayerInGame = currentPlayer?.let { gameState.playersInGame[it.id] } ?: false
                        val isBot = currentPlayer?.name == "Bot"
                        val minimumPointsNeeded = !isPlayerInGame && gameState.currentTurnScore < 500 && !isBot

                        // Determinar color y texto del botón según el estado
                        val buttonColor = when {
                            gameState.isGameOver -> MaterialTheme.colorScheme.tertiary
                            gameState.scoreExceeded -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.secondary
                        }
                        
                        val buttonText = when {
                            gameState.isGameOver -> stringResource(R.string.end_game)
                            gameState.scoreExceeded -> stringResource(R.string.pass_turn)
                            minimumPointsNeeded -> stringResource(R.string.minimum_points_required)
                            else -> stringResource(R.string.bank_score)
                        }

                        Button(
                            onClick = {
                                when {
                                    gameState.isGameOver -> navigateToHome()
                                    gameState.scoreExceeded -> viewModel.onNextPlayerClick()
                                    else -> viewModel.onBankClick()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(BUTTON_HEIGHT)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = ButtonShape,
                                    spotColor = buttonColor.copy(alpha = 0.5f)
                                ),
                            shape = ButtonShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor,
                                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                            ),
                            enabled = gameState.canBank || gameState.scoreExceeded || gameState.isGameOver
                        ) {
                            Text(
                                text = buttonText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
