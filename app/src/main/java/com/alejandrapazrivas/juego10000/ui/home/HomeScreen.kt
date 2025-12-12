package com.alejandrapazrivas.juego10000.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ads.AdConstants
import com.alejandrapazrivas.juego10000.ui.common.components.ads.BannerAd
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.Juego10000Theme
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.home.components.GameModeSelectionDialog
import com.alejandrapazrivas.juego10000.ui.home.components.HomeDrawerContent
import com.alejandrapazrivas.juego10000.ui.home.components.PlayerSelectionDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

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
    var showContent by remember { mutableStateOf(false) }

    // Efecto para animar la aparición de los elementos
    LaunchedEffect(key1 = true) {
        delay(100)
        showContent = true
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
                Box(modifier = Modifier.fillMaxSize()) {
                    // Fondo animado
                    AnimatedBackground()

                    // Contenido principal
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
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(horizontal = dimensions.screenPaddingHorizontal)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                            // Header con saludo personalizado
                            UserGreetingHeader(
                                userName = uiState.currentUser?.name ?: ""
                            )

                            Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

                            // Botón principal de jugar
                            PlayButton(
                                onPlayClick = { viewModel.onNewGameClick() }
                            )

                            Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

                            // Estadísticas rápidas en fila
                            QuickStatsRow(
                                gamesPlayed = uiState.userStats.totalGamesPlayed,
                                wins = uiState.userStats.totalWins,
                                winRate = uiState.userStats.winRate.toInt()
                            )

                            Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

                            // Accesos rápidos
                            QuickAccessSection(
                                onStatsClick = onNavigateToStats,
                                onRulesClick = onNavigateToRules,
                                onPlayersClick = onNavigateToPlayers
                            )

                            Spacer(modifier = Modifier.height(dimensions.spaceLarge))

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

/**
 * Fondo animado con elementos flotantes
 */
@Composable
private fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_animation")

    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val primaryColor = Primary
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor,
                        primaryColor.copy(alpha = 0.03f)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Círculos decorativos
            val circles = listOf(
                Triple(0.1f, 0.15f, 70.dp.toPx()),
                Triple(0.9f, 0.1f, 50.dp.toPx()),
                Triple(0.8f, 0.7f, 90.dp.toPx()),
                Triple(0.15f, 0.6f, 60.dp.toPx())
            )

            circles.forEachIndexed { index, (xRatio, yRatio, baseRadius) ->
                val offsetMultiplier = if (index % 2 == 0) floatOffset1 else floatOffset2
                val yOffset = sin(offsetMultiplier * Math.PI).toFloat() * 25.dp.toPx()

                drawCircle(
                    color = primaryColor.copy(alpha = 0.02f + (index * 0.01f)),
                    radius = baseRadius + (offsetMultiplier * 15.dp.toPx()),
                    center = Offset(
                        x = width * xRatio,
                        y = height * yRatio + yOffset
                    )
                )
            }

            // Dados decorativos
            val dicePositions = listOf(
                Triple(0.05f, 0.3f, 20.dp.toPx()),
                Triple(0.95f, 0.45f, 18.dp.toPx()),
                Triple(0.1f, 0.85f, 22.dp.toPx()),
                Triple(0.88f, 0.8f, 16.dp.toPx())
            )

            dicePositions.forEachIndexed { index, (xRatio, yRatio, diceSize) ->
                val diceRotation = rotation + (index * 45f)
                val yOffset = sin((floatOffset1 + index * 0.3f) * Math.PI).toFloat() * 12.dp.toPx()

                rotate(diceRotation, pivot = Offset(width * xRatio, height * yRatio + yOffset)) {
                    drawRoundRect(
                        color = primaryColor.copy(alpha = 0.06f),
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
 * Header con saludo personalizado
 */
@Composable
private fun UserGreetingHeader(userName: String) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar del usuario
        Box(
            modifier = Modifier
                .size(dimensions.avatarSizeMedium)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.7f),
                            Primary.copy(alpha = 0.3f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Primary, Primary.copy(alpha = 0.5f))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Text(
            text = if (userName.isNotEmpty()) "¡Hola, $userName!" else stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.reach_10000_points),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Botón principal de jugar con anillos animados
 */
@Composable
private fun PlayButton(onPlayClick: () -> Unit) {
    val dimensions = LocalDimensions.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(100),
        label = "play_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "play_animations")

    // Pulso del botón
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Rotación del anillo exterior
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    // Rotación inversa del anillo interior
    val innerRingRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_ring_rotation"
    )

    // Efecto de brillo
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val buttonSize = dimensions.avatarSizeLarge * 2
    val accentColor = Color(0xFFFF6B35) // Naranja vibrante
    val secondaryAccent = Color(0xFFFFD700) // Dorado

    Box(
        modifier = Modifier
            .size(buttonSize + 40.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Anillo exterior con gradiente giratorio
        Canvas(
            modifier = Modifier
                .size(buttonSize + 36.dp)
                .graphicsLayer { rotationZ = ringRotation }
        ) {
            val strokeWidth = 4.dp.toPx()
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Primary,
                        accentColor,
                        secondaryAccent,
                        Primary.copy(alpha = 0.3f),
                        Primary
                    )
                ),
                startAngle = 0f,
                sweepAngle = 270f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
        }

        // Anillo interior decorativo
        Canvas(
            modifier = Modifier
                .size(buttonSize + 20.dp)
                .graphicsLayer { rotationZ = innerRingRotation }
        ) {
            val strokeWidth = 2.dp.toPx()
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        secondaryAccent.copy(alpha = 0.8f),
                        Primary.copy(alpha = 0.5f),
                        accentColor.copy(alpha = 0.6f),
                        secondaryAccent.copy(alpha = 0.3f)
                    )
                ),
                startAngle = 45f,
                sweepAngle = 180f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
        }

        // Efecto de glow detrás del botón
        Box(
            modifier = Modifier
                .size(buttonSize + 8.dp)
                .scale(pulseScale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Primary.copy(alpha = glowAlpha),
                            Primary.copy(alpha = glowAlpha * 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Botón principal
        Box(
            modifier = Modifier
                .size(buttonSize)
                .scale(pulseScale)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    spotColor = Primary.copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Primary,
                            Primary.copy(alpha = 0.9f),
                            accentColor.copy(alpha = 0.8f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onPlayClick
                ),
            contentAlignment = Alignment.Center
        ) {
            // Overlay con efecto de luz
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            center = Offset(0.3f, 0.3f),
                            radius = 300f
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de dados de la app
                Icon(
                    painter = painterResource(id = R.drawable.ic_dice),
                    contentDescription = stringResource(R.string.play),
                    tint = Color.White,
                    modifier = Modifier.size(dimensions.iconSizeExtraLarge + 8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.play).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

/**
 * Fila de estadísticas rápidas
 */
@Composable
private fun QuickStatsRow(
    gamesPlayed: Int,
    wins: Int,
    winRate: Int
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickStatItem(
            value = gamesPlayed.toString(),
            label = stringResource(R.string.games_played),
            icon = R.drawable.ic_dice
        )
        QuickStatItem(
            value = wins.toString(),
            label = stringResource(R.string.games_won),
            icon = R.drawable.ic_trophy
        )
        QuickStatItem(
            value = "$winRate%",
            label = stringResource(R.string.win_rate),
            icon = R.drawable.ic_stats
        )
    }
}

@Composable
private fun QuickStatItem(
    value: String,
    label: String,
    icon: Int
) {
    val dimensions = LocalDimensions.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.avatarSizeMedium)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Sección de accesos rápidos
 */
@Composable
private fun QuickAccessSection(
    onStatsClick: () -> Unit,
    onRulesClick: () -> Unit,
    onPlayersClick: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
    ) {
        Text(
            text = stringResource(R.string.quick_access),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = dimensions.spaceSmall)
        )

        QuickAccessCard(
            title = stringResource(R.string.statistics),
            icon = R.drawable.ic_stats,
            onClick = onStatsClick
        )

        QuickAccessCard(
            title = stringResource(R.string.rules),
            icon = R.drawable.ic_rules,
            onClick = onRulesClick
        )

        QuickAccessCard(
            title = stringResource(R.string.manage_players),
            icon = R.drawable.ic_add_player,
            onClick = onPlayersClick
        )
    }
}

@Composable
private fun QuickAccessCard(
    title: String,
    icon: Int,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(100),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = dimensions.cardElevation,
                shape = CardShape,
                spotColor = Primary.copy(alpha = 0.1f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(dimensions.iconSizeLarge + dimensions.spaceSmall)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(dimensions.iconSizeMedium)
                )
            }

            Spacer(modifier = Modifier.width(dimensions.spaceMedium))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Primary.copy(alpha = 0.5f),
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )
        }
    }
}
