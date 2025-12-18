package com.bigotitech.rokub10000.presentation.feature.home

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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.core.ads.AdConstants
import com.bigotitech.rokub10000.presentation.common.components.ads.BannerAd
import com.bigotitech.rokub10000.domain.model.BotDifficulty
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.presentation.common.components.backgrounds.AnimatedBackground
import com.bigotitech.rokub10000.presentation.common.components.dialog.BaseDialog
import com.bigotitech.rokub10000.presentation.common.components.dialog.DialogButtons
import com.bigotitech.rokub10000.presentation.common.components.dialog.DialogHeader
import com.bigotitech.rokub10000.presentation.common.theme.AccentOrange
import com.bigotitech.rokub10000.presentation.common.theme.CardShape
import com.bigotitech.rokub10000.presentation.common.theme.GoldColor
import com.bigotitech.rokub10000.presentation.common.theme.LocalDimensions
import com.bigotitech.rokub10000.presentation.common.theme.Primary
import com.bigotitech.rokub10000.presentation.common.theme.Secondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla de inicio del juego.
 * Muestra el saludo al usuario, botón de jugar, estadísticas rápidas y accesos directos.
 */
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
    var showNoPlayersDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        delay(100)
        showContent = true
    }

    // Mostrar diálogo cuando no hay jugadores disponibles
    uiState.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            showNoPlayersDialog = true
        }
    }

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
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedBackground()

                    this@Column.AnimatedVisibility(
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

                BannerAd(
                    adUnitId = AdConstants.BANNER_HOME,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Game dialogs
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

    // Diálogo cuando no hay otros jugadores para multijugador
    if (showNoPlayersDialog) {
        NoPlayersAvailableDialog(
            onDismiss = {
                showNoPlayersDialog = false
                viewModel.clearErrorMessage()
            },
            onGoToPlayers = {
                showNoPlayersDialog = false
                viewModel.clearErrorMessage()
                onNavigateToPlayers()
            }
        )
    }

    // Navigate to game when created
    LaunchedEffect(uiState.currentGameId) {
        if (uiState.currentGameId > 0) {
            onNavigateToGame(uiState.currentGameId)
            viewModel.onNavigationHandled()
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
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
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
        }
    }
}

// ==================== User Greeting Header ====================

@Composable
private fun UserGreetingHeader(
    userName: String,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserAvatar(
            userName = userName,
            size = dimensions.avatarSizeMedium
        )

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Text(
            text = if (userName.isNotEmpty())
                stringResource(R.string.greeting_user, userName)
            else
                stringResource(R.string.app_name),
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

@Composable
private fun UserAvatar(
    userName: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.7f),
                        Primary.copy(alpha = 0.3f)
                    )
                )
            )
            .background(
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
}

// ==================== Play Button ====================

@Composable
private fun PlayButton(
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(100),
        label = "play_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "play_animations")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    val innerRingRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_ring_rotation"
    )

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

    Box(
        modifier = modifier
            .size(buttonSize + 40.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        OuterRing(size = buttonSize + 36.dp, rotation = ringRotation)
        InnerRing(size = buttonSize + 20.dp, rotation = innerRingRotation)
        GlowEffect(size = buttonSize + 8.dp, scale = pulseScale, alpha = glowAlpha)
        MainButton(
            size = buttonSize,
            scale = pulseScale,
            interactionSource = interactionSource,
            onClick = onPlayClick,
            iconSize = dimensions.iconSizeExtraLarge + 8.dp
        )
    }
}

@Composable
private fun OuterRing(size: Dp, rotation: Float) {
    Canvas(
        modifier = Modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation }
    ) {
        val strokeWidth = 4.dp.toPx()
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Primary,
                    AccentOrange,
                    GoldColor,
                    Primary.copy(alpha = 0.3f),
                    Primary
                )
            ),
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun InnerRing(size: Dp, rotation: Float) {
    Canvas(
        modifier = Modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation }
    ) {
        val strokeWidth = 2.dp.toPx()
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    GoldColor.copy(alpha = 0.8f),
                    Primary.copy(alpha = 0.5f),
                    AccentOrange.copy(alpha = 0.6f),
                    GoldColor.copy(alpha = 0.3f)
                )
            ),
            startAngle = 45f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun GlowEffect(size: Dp, scale: Float, alpha: Float) {
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Primary.copy(alpha = alpha),
                        Primary.copy(alpha = alpha * 0.5f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
}

@Composable
private fun MainButton(
    size: Dp,
    scale: Float,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    iconSize: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
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
                        AccentOrange.copy(alpha = 0.8f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Light overlay
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

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dice),
                contentDescription = stringResource(R.string.play),
                tint = Color.White,
                modifier = Modifier.size(iconSize)
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

// ==================== Quick Stats ====================

@Composable
private fun QuickStatsRow(
    gamesPlayed: Int,
    wins: Int,
    winRate: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
    icon: Int,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier,
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

// ==================== Quick Access Section ====================

@Composable
private fun QuickAccessSection(
    onStatsClick: () -> Unit,
    onRulesClick: () -> Unit,
    onPlayersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier.fillMaxWidth(),
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
        modifier = modifier
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

// ==================== Drawer ====================

@Composable
private fun HomeDrawerContent(
    currentUserName: String?,
    onNavigateToPlayers: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToRules: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onChangeUser: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    ModalDrawerSheet(
        modifier = modifier.width(dimensions.drawerWidth),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            DrawerHeader(currentUserName = currentUserName)

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            DrawerMenuSection(title = stringResource(R.string.game_section)) {
                DrawerItem(
                    icon = R.drawable.ic_add_player,
                    title = stringResource(R.string.manage_players),
                    subtitle = stringResource(R.string.manage_players_description),
                    accentColor = Primary,
                    onClick = {
                        onCloseDrawer()
                        onNavigateToPlayers()
                    }
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.spaceSmall))
                DrawerItem(
                    icon = R.drawable.ic_stats,
                    title = stringResource(R.string.statistics),
                    subtitle = stringResource(R.string.stats_description),
                    accentColor = Secondary,
                    onClick = {
                        onCloseDrawer()
                        onNavigateToStats()
                    }
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.spaceSmall))
                DrawerItem(
                    icon = R.drawable.ic_rules,
                    title = stringResource(R.string.rules_title),
                    subtitle = stringResource(R.string.rules_description),
                    accentColor = Primary,
                    onClick = {
                        onCloseDrawer()
                        onNavigateToRules()
                    }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            DrawerMenuSection(title = stringResource(R.string.settings_section)) {
                DrawerItem(
                    icon = R.drawable.ic_settings,
                    title = stringResource(R.string.settings),
                    subtitle = stringResource(R.string.settings_description),
                    accentColor = Secondary,
                    onClick = {
                        onCloseDrawer()
                        onNavigateToSettings()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = dimensions.spaceMedium),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            ChangeProfileItem(
                currentUserName = currentUserName,
                onClick = {
                    onCloseDrawer()
                    onChangeUser()
                }
            )

            DrawerFooter()
        }
    }
}

@Composable
private fun DrawerHeader(currentUserName: String?) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Primary,
                        Primary.copy(alpha = 0.85f),
                        Secondary.copy(alpha = 0.7f)
                    )
                )
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-25).dp, y = 25.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )
        Box(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 10.dp, y = 20.dp)
                .clip(CircleShape)
                .background(Secondary.copy(alpha = 0.3f))
        )

        Column(modifier = Modifier.padding(dimensions.spaceLarge)) {
            Box(
                modifier = Modifier
                    .size(dimensions.avatarSizeLarge + 8.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.avatarSizeLarge)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Secondary,
                                    Secondary.copy(alpha = 0.8f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dice),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(dimensions.iconSizeLarge)
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Text(
                text = currentUserName ?: stringResource(R.string.no_user),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun DrawerMenuSection(
    title: String,
    content: @Composable () -> Unit
) {
    val dimensions = LocalDimensions.current

    Column(modifier = Modifier.padding(horizontal = dimensions.spaceMedium)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = dimensions.spaceSmall, horizontal = dimensions.spaceSmall)
        )
        content()
    }
}

@Composable
private fun DrawerItem(
    icon: Int,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cardCornerRadius))
            .clickable(onClick = onClick)
            .padding(dimensions.spaceSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.avatarSizeSmall)
                .clip(RoundedCornerShape(dimensions.spaceSmall + 2.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor,
                            accentColor.copy(alpha = 0.8f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(dimensions.iconSizeSmall)
            )
        }

        Spacer(modifier = Modifier.width(dimensions.spaceMedium))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ChangeProfileItem(
    currentUserName: String?,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Secondary.copy(alpha = 0.1f),
                            Secondary.copy(alpha = 0.05f)
                        )
                    )
                )
                .clickable(onClick = onClick)
                .padding(dimensions.spaceSmall + 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Secondary,
                                Secondary.copy(alpha = 0.8f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentUserName?.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(dimensions.spaceSmall + 4.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currentUserName ?: stringResource(R.string.no_user),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.tap_to_change_profile),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Secondary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = stringResource(R.string.change_profile),
                    tint = Secondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun DrawerFooter() {
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.spaceMedium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.version, "1.0"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

// ==================== Dialogs ====================

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

enum class GameMode {
    MULTIPLAYER, SINGLE_PLAYER
}

@Composable
private fun GameModeSelectionDialog(
    onDismiss: () -> Unit,
    onMultiplayerSelected: () -> Unit,
    onSinglePlayerSelected: (BotDifficulty) -> Unit
) {
    val dimensions = LocalDimensions.current
    var selectedMode by remember { mutableStateOf(GameMode.MULTIPLAYER) }
    var selectedDifficulty by remember { mutableStateOf(BotDifficulty.BEGINNER) }
    var showDifficultySelection by remember { mutableStateOf(false) }

    BaseDialog(
        onDismiss = {
            if (showDifficultySelection) {
                showDifficultySelection = false
            } else {
                onDismiss()
            }
        }
    ) {
        if (!showDifficultySelection) {
            DialogHeader(
                title = stringResource(id = R.string.game_mode),
                description = stringResource(R.string.select_game_mode_description),
                iconPainter = painterResource(id = R.drawable.ic_dice)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceLarge))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                SelectableOption(
                    title = stringResource(id = R.string.multiplayer_mode),
                    description = stringResource(R.string.multiplayer_description),
                    iconResId = R.drawable.ic_add_player,
                    isSelected = selectedMode == GameMode.MULTIPLAYER,
                    onClick = { selectedMode = GameMode.MULTIPLAYER },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                SelectableOption(
                    title = stringResource(id = R.string.single_player_mode),
                    description = stringResource(R.string.single_player_description),
                    iconResId = R.drawable.ic_dice,
                    isSelected = selectedMode == GameMode.SINGLE_PLAYER,
                    onClick = { selectedMode = GameMode.SINGLE_PLAYER },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceLarge))

            DialogButtons(
                onCancel = onDismiss,
                onConfirm = {
                    if (selectedMode == GameMode.MULTIPLAYER) {
                        onMultiplayerSelected()
                    } else {
                        showDifficultySelection = true
                    }
                },
                confirmText = stringResource(id = R.string.confirm)
            )
        } else {
            DialogHeader(
                title = stringResource(id = R.string.select_bot_difficulty),
                description = stringResource(R.string.select_difficulty_description),
                iconPainter = painterResource(id = R.drawable.ic_dice)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceLarge))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                SelectableOption(
                    title = stringResource(id = R.string.bot_beginner),
                    description = stringResource(R.string.beginner_description),
                    isSelected = selectedDifficulty == BotDifficulty.BEGINNER,
                    onClick = { selectedDifficulty = BotDifficulty.BEGINNER },
                    leadingContent = {
                        DifficultyLevelIcon(
                            level = 1,
                            isSelected = selectedDifficulty == BotDifficulty.BEGINNER
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                SelectableOption(
                    title = stringResource(id = R.string.bot_intermediate),
                    description = stringResource(R.string.intermediate_description),
                    isSelected = selectedDifficulty == BotDifficulty.INTERMEDIATE,
                    onClick = { selectedDifficulty = BotDifficulty.INTERMEDIATE },
                    leadingContent = {
                        DifficultyLevelIcon(
                            level = 2,
                            isSelected = selectedDifficulty == BotDifficulty.INTERMEDIATE
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                SelectableOption(
                    title = stringResource(id = R.string.bot_expert),
                    description = stringResource(R.string.expert_description),
                    isSelected = selectedDifficulty == BotDifficulty.EXPERT,
                    onClick = { selectedDifficulty = BotDifficulty.EXPERT },
                    leadingContent = {
                        DifficultyLevelIcon(
                            level = 3,
                            isSelected = selectedDifficulty == BotDifficulty.EXPERT
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceLarge))

            DialogButtons(
                onCancel = { showDifficultySelection = false },
                onConfirm = { onSinglePlayerSelected(selectedDifficulty) },
                confirmText = stringResource(id = R.string.confirm)
            )
        }
    }
}

@Composable
private fun PlayerSelectionDialog(
    availablePlayers: List<Player>,
    selectedPlayers: List<Player>,
    onPlayerSelected: (Player) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val dimensions = LocalDimensions.current

    BaseDialog(onDismiss = onDismiss) {
        DialogHeader(
            title = stringResource(R.string.select_opponents),
            description = stringResource(R.string.select_opponents_description),
            iconPainter = painterResource(id = R.drawable.ic_add_player)
        )

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            availablePlayers.forEach { player ->
                val isSelected = selectedPlayers.contains(player)

                SelectableOption(
                    title = player.name,
                    description = if (isSelected)
                        stringResource(R.string.opponent_selected)
                    else
                        stringResource(R.string.click_to_select),
                    isSelected = isSelected,
                    onClick = { onPlayerSelected(player) },
                    leadingContent = {
                        Box(
                            modifier = Modifier.size(dimensions.avatarSizeSmall),
                            contentAlignment = Alignment.Center
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensions.spaceExtraSmall)
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        DialogButtons(
            onCancel = onDismiss,
            onConfirm = onConfirm,
            confirmText = stringResource(id = R.string.confirm),
            confirmEnabled = selectedPlayers.isNotEmpty() && selectedPlayers.size <= 5
        )
    }
}

@Composable
private fun NoPlayersAvailableDialog(
    onDismiss: () -> Unit,
    onGoToPlayers: () -> Unit
) {
    val dimensions = LocalDimensions.current

    BaseDialog(onDismiss = onDismiss) {
        DialogHeader(
            title = stringResource(id = R.string.no_players_available_title),
            description = stringResource(id = R.string.no_players_available_description),
            iconPainter = painterResource(id = R.drawable.ic_add_player)
        )

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        DialogButtons(
            onCancel = onDismiss,
            onConfirm = onGoToPlayers,
            confirmText = stringResource(id = R.string.add_players)
        )
    }
}

@Composable
private fun SelectableOption(
    title: String,
    description: String,
    iconResId: Int? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    leadingContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary)
        else
            BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(dimensions.spaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingContent != null) {
                leadingContent()
            } else if (iconResId != null) {
                Box(
                    modifier = Modifier
                        .size(dimensions.avatarSizeSmall)
                        .clip(RoundedCornerShape(dimensions.spaceSmall))
                        .background(
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = null,
                        tint = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(dimensions.iconSizeMedium)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(start = dimensions.spaceMedium)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DifficultyLevelIcon(
    level: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val iconSize = dimensions.iconSizeMedium

    Row(
        modifier = modifier.size(iconSize),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        DifficultyBar(
            height = iconSize * 0.83f,
            isActive = level >= 3,
            isSelected = isSelected
        )
        DifficultyBar(
            height = iconSize * 0.58f,
            isActive = level >= 2,
            isSelected = isSelected
        )
        DifficultyBar(
            height = iconSize * 0.33f,
            isActive = level >= 1,
            isSelected = isSelected
        )
    }
}

@Composable
private fun DifficultyBar(
    height: Dp,
    isActive: Boolean,
    isSelected: Boolean
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier
            .width(dimensions.spaceExtraSmall + 1.dp)
            .height(height)
            .clip(RoundedCornerShape(2.dp))
            .background(
                color = if (isActive)
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                else
                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
    )
}
