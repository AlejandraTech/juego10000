package com.bigotitech.rokub10000.presentation.feature.stats

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.core.ads.AdConstants
import com.bigotitech.rokub10000.presentation.common.components.ads.BannerAd
import com.bigotitech.rokub10000.presentation.common.theme.AmberColor
import com.bigotitech.rokub10000.presentation.common.theme.LocalDimensions
import com.bigotitech.rokub10000.presentation.common.theme.Primary
import com.bigotitech.rokub10000.presentation.common.theme.ScorePositive
import com.bigotitech.rokub10000.presentation.feature.stats.state.GameWithWinner
import com.bigotitech.rokub10000.presentation.feature.stats.state.PlayerStats
import com.bigotitech.rokub10000.presentation.feature.stats.state.ScoreWithPlayer
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla principal de estadísticas con navegación por pestañas.
 * Muestra tres secciones: Jugadores, Historial y Récords.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val playerStats by viewModel.playerStats.collectAsState()
    val gameHistory by viewModel.gameHistory.collectAsState()
    val botGameHistory by viewModel.botGameHistory.collectAsState()
    val multiplayerGameHistory by viewModel.multiplayerGameHistory.collectAsState()
    val topScores by viewModel.topScores.collectAsState()

    val tabs = listOf(
        stringResource(R.string.tab_players),
        stringResource(R.string.tab_history),
        stringResource(R.string.tab_records)
    )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val selectedTab = pagerState.currentPage

    Scaffold(
        topBar = { StatsTopAppBar(onBackClick = onNavigateBack) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
            StatsTabRow(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> PlayerStatsTab(players = playerStats)
                    1 -> GameHistoryTab(
                        botGameHistory = botGameHistory,
                        multiplayerGameHistory = multiplayerGameHistory
                    )
                    2 -> TopScoresTab(topScores = topScores)
                }
            }

            BannerAd(
                adUnitId = AdConstants.BANNER_STATS,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ==================== Top App Bar ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsTopAppBar(onBackClick: () -> Unit) {
    val dimensions = LocalDimensions.current
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.statistics),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.shadow(elevation = dimensions.spaceExtraSmall)
    )
}

// ==================== Tab Row ====================

private data class StatsTab(
    val title: String,
    val icon: Int
)

@Composable
private fun StatsTabRow(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val dimensions = LocalDimensions.current

    val tabsWithIcons = listOf(
        StatsTab(tabs.getOrElse(0) { "" }, R.drawable.ic_add_player),
        StatsTab(tabs.getOrElse(1) { "" }, R.drawable.ic_history),
        StatsTab(tabs.getOrElse(2) { "" }, R.drawable.ic_trophy)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
    ) {
        tabsWithIcons.forEachIndexed { index, tab ->
            val isSelected = selectedTab == index

            val borderColor by animateColorAsState(
                targetValue = if (isSelected) Primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                animationSpec = tween(200),
                label = "tab_border_color"
            )

            val contentColor by animateColorAsState(
                targetValue = if (isSelected) Primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                animationSpec = tween(200),
                label = "tab_content_color"
            )

            val borderWidth by animateDpAsState(
                targetValue = if (isSelected) 2.dp else 1.dp,
                animationSpec = tween(200),
                label = "tab_border_width"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(dimensions.spaceSmall + dimensions.spaceExtraSmall))
                    .border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(dimensions.spaceSmall + dimensions.spaceExtraSmall)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = tab.icon),
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.iconSizeSmall),
                        tint = contentColor
                    )

                    Spacer(modifier = Modifier.width(dimensions.spaceExtraSmall))

                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = contentColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ==================== Tab Header ====================

@Composable
private fun TabHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = dimensions.spaceSmall)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

// ==================== Player Stats Tab ====================

@Composable
private fun PlayerStatsTab(players: List<PlayerStats>) {
    val dimensions = LocalDimensions.current

    if (players.isEmpty()) {
        EmptyStateMessage(
            message = stringResource(R.string.no_registered_players),
            iconResId = R.drawable.ic_add_player
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(dimensions.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(dimensions.spaceMedium)
        ) {
            item {
                TabHeader(
                    title = stringResource(R.string.tab_players),
                    subtitle = stringResource(R.string.players_count, players.size)
                )
            }

            itemsIndexed(players.sortedByDescending { it.player.gamesWon }) { index, playerStat ->
                PlayerStatCard(
                    playerStat = playerStat,
                    position = index + 1
                )
            }
        }
    }
}

// ==================== Game History Tab ====================

@Composable
private fun GameHistoryTab(
    botGameHistory: List<GameWithWinner>,
    multiplayerGameHistory: List<GameWithWinner>
) {
    val dimensions = LocalDimensions.current
    var selectedHistoryType by remember { mutableIntStateOf(0) }

    val historyTypes = listOf(
        stringResource(R.string.history_vs_bot),
        stringResource(R.string.history_multiplayer)
    )

    val currentHistory = if (selectedHistoryType == 0) botGameHistory else multiplayerGameHistory

    Column(modifier = Modifier.fillMaxSize()) {
        HistoryTypeSelector(
            options = historyTypes,
            selectedIndex = selectedHistoryType,
            onOptionSelected = { selectedHistoryType = it }
        )

        if (currentHistory.isEmpty()) {
            EmptyStateMessage(
                message = stringResource(R.string.no_registered_games),
                iconResId = R.drawable.ic_dice
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(dimensions.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(dimensions.spaceMedium)
            ) {
                item {
                    TabHeader(
                        title = stringResource(R.string.game_history),
                        subtitle = stringResource(R.string.games_count, currentHistory.size)
                    )
                }

                items(currentHistory) { gameWithWinner ->
                    GameHistoryCard(
                        gameWithWinner = gameWithWinner
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryTypeSelector(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    val dimensions = LocalDimensions.current
    val icons = listOf(R.drawable.ic_robot, R.drawable.ic_people)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spaceMedium)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex

            val borderColor by animateColorAsState(
                targetValue = if (isSelected) Primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                animationSpec = tween(200),
                label = "selector_border"
            )

            val contentColor by animateColorAsState(
                targetValue = if (isSelected) Primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                animationSpec = tween(200),
                label = "selector_content"
            )

            val borderWidth by animateDpAsState(
                targetValue = if (isSelected) 1.5.dp else 1.dp,
                animationSpec = tween(200),
                label = "selector_border_width"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(dimensions.spaceMedium))
                    .border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(dimensions.spaceMedium)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onOptionSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = icons.getOrElse(index) { R.drawable.ic_dice }),
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.iconSizeSmall + 2.dp),
                        tint = contentColor
                    )

                    Spacer(modifier = Modifier.width(dimensions.spaceSmall))

                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = contentColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ==================== Top Scores Tab ====================

@Composable
private fun TopScoresTab(topScores: List<ScoreWithPlayer>) {
    val dimensions = LocalDimensions.current

    if (topScores.isEmpty()) {
        EmptyStateMessage(
            message = stringResource(R.string.no_registered_scores),
            iconResId = R.drawable.ic_trophy
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(dimensions.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(dimensions.spaceMedium)
        ) {
            item {
                TabHeader(
                    title = stringResource(R.string.top_scores),
                    subtitle = stringResource(R.string.players_count, topScores.size)
                )
            }

            itemsIndexed(topScores) { index, scoreWithPlayer ->
                TopScoreCard(
                    scoreWithPlayer = scoreWithPlayer,
                    position = index + 1
                )
            }
        }
    }
}

// ==================== Empty State ====================

@Composable
private fun EmptyStateMessage(
    message: String,
    iconResId: Int,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(dimensions.emptyStateIconSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(dimensions.spaceMedium))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==================== Podium Colors ====================

private object PodiumColors {
    val Gold = Color(0xFFFFD700)
    val Silver = Color(0xFFC0C0C0)
    val Bronze = Color(0xFFCD7F32)
    val Default = Color(0xFF6B7280)

    fun getPositionColor(position: Int): Color {
        return when (position) {
            1 -> Gold
            2 -> Silver
            3 -> Bronze
            else -> Default
        }
    }

    fun isPodium(position: Int): Boolean = position in 1..3
}

// ==================== Position Badge ====================

@Composable
private fun PositionBadge(
    position: Int,
    modifier: Modifier = Modifier,
    color: Color = PodiumColors.getPositionColor(position)
) {
    val dimensions = LocalDimensions.current
    Box(
        modifier = modifier
            .size(dimensions.spaceLarge)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.position_format, position),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// ==================== Stats Player Avatar ====================

@Composable
private fun StatsPlayerAvatar(
    name: String,
    modifier: Modifier = Modifier,
    isPodium: Boolean = false,
    podiumColor: Color = MaterialTheme.colorScheme.primary
) {
    val dimensions = LocalDimensions.current
    val avatarColor = if (isPodium) podiumColor else MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .size(dimensions.avatarSizeMedium)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        avatarColor.copy(alpha = 0.7f),
                        avatarColor.copy(alpha = 0.2f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        avatarColor,
                        avatarColor.copy(alpha = 0.5f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isPodium) Color.White else MaterialTheme.colorScheme.onPrimary
        )
    }
}

// ==================== Player Stat Card ====================

@Composable
private fun PlayerStatCard(
    playerStat: PlayerStats,
    position: Int = 0
) {
    val dimensions = LocalDimensions.current
    val isPodium = PodiumColors.isPodium(position)
    val positionColor = PodiumColors.getPositionColor(position)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimensions.cardElevation,
                shape = RoundedCornerShape(dimensions.cardCornerRadius),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            PlayerStatCardHeader(
                playerStat = playerStat,
                position = position,
                isPodium = isPodium,
                positionColor = positionColor
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = dimensions.spaceExtraSmall)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            WinProgressBar(
                wins = playerStat.player.gamesWon,
                total = playerStat.player.gamesPlayed
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            PlayerStatCardStats(playerStat)
        }
    }
}

@Composable
private fun PlayerStatCardHeader(
    playerStat: PlayerStats,
    position: Int,
    isPodium: Boolean,
    positionColor: Color
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isPodium) {
            PositionBadge(position = position, color = positionColor)
            Spacer(modifier = Modifier.width(dimensions.spaceSmall))
        }

        StatsPlayerAvatar(
            name = playerStat.player.name,
            isPodium = isPodium,
            podiumColor = positionColor
        )

        Spacer(modifier = Modifier.width(dimensions.spaceMedium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playerStat.player.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.games_played_count, playerStat.player.gamesPlayed),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        WinPercentage(winRate = playerStat.player.winRate)
    }
}

@Composable
private fun WinPercentage(winRate: Float) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = stringResource(R.string.percentage_format, (winRate * 100).toInt()),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = ScorePositive
        )
        Text(
            text = stringResource(R.string.win_rate),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WinProgressBar(wins: Int, total: Int) {
    val dimensions = LocalDimensions.current
    val progress = if (total > 0) wins.toFloat() / total else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "win_progress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.victories_count, wins),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.wins_total_format, wins, total),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ScorePositive
            )
        }
        Spacer(modifier = Modifier.height(dimensions.spaceExtraSmall))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.spaceSmall)
                .clip(RoundedCornerShape(dimensions.spaceExtraSmall)),
            color = ScorePositive,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun PlayerStatCardStats(playerStat: PlayerStats) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
    ) {
        StatItem(
            icon = R.drawable.ic_trophy,
            label = stringResource(R.string.games_won),
            value = playerStat.player.gamesWon.toString(),
            modifier = Modifier.weight(1f)
        )
        StatItem(
            icon = R.drawable.ic_dice,
            label = stringResource(R.string.games_played),
            value = playerStat.player.gamesPlayed.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatItem(
    icon: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(dimensions.spaceSmall + dimensions.spaceExtraSmall)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationNone)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceExtraSmall))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ScorePositive
            )
        }
    }
}

// ==================== Game History Card ====================

@Composable
private fun GameHistoryCard(
    gameWithWinner: GameWithWinner,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val game = gameWithWinner.game
    val winner = gameWithWinner.winner

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimensions.cardElevation,
                shape = RoundedCornerShape(dimensions.cardCornerRadius),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            GameCardHeader(game)

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = dimensions.spaceExtraSmall)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            GameCardDetails(game, winner)
        }
    }
}

@Composable
private fun GameCardHeader(game: com.bigotitech.rokub10000.domain.model.Game) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dice),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensions.iconSizeSmall + dimensions.spaceExtraSmall)
            )
            Spacer(modifier = Modifier.width(dimensions.spaceSmall))
            Text(
                text = stringResource(R.string.game_number, game.id),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Surface(
            shape = RoundedCornerShape(dimensions.spaceMedium),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Text(
                text = formatFullDate(game.completedAt ?: game.startedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = dimensions.spaceSmall,
                    vertical = dimensions.spaceExtraSmall
                )
            )
        }
    }
}

@Composable
private fun GameCardDetails(
    game: com.bigotitech.rokub10000.domain.model.Game,
    winner: com.bigotitech.rokub10000.domain.model.Player?
) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_trophy),
            contentDescription = stringResource(R.string.trophy),
            tint = AmberColor,
            modifier = Modifier.size(dimensions.spaceLarge)
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        Text(
            text = winner?.name ?: stringResource(R.string.no_winner),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        GameStatChip(
            icon = R.drawable.ic_add_player,
            value = game.playerCount.toString()
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        GameStatChip(
            icon = R.drawable.ic_dice,
            value = game.currentRound.toString()
        )
    }
}

@Composable
private fun GameStatChip(
    icon: Int,
    value: String
) {
    val dimensions = LocalDimensions.current
    Surface(
        shape = RoundedCornerShape(dimensions.spaceMedium),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = dimensions.spaceSmall,
                vertical = dimensions.spaceExtraSmall
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensions.spaceMedium)
            )
            Spacer(modifier = Modifier.width(dimensions.spaceExtraSmall))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==================== Top Score Card ====================

@Composable
private fun TopScoreCard(
    scoreWithPlayer: ScoreWithPlayer,
    position: Int
) {
    val dimensions = LocalDimensions.current
    val isPodium = PodiumColors.isPodium(position)
    val positionColor = PodiumColors.getPositionColor(position)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimensions.cardElevation,
                shape = RoundedCornerShape(dimensions.cardCornerRadius),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            TopScoreCardHeader(
                scoreWithPlayer = scoreWithPlayer,
                position = position,
                isPodium = isPodium,
                positionColor = positionColor
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = dimensions.spaceExtraSmall)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            TopScoreCardFooter(
                scoreWithPlayer = scoreWithPlayer,
                isPodium = isPodium,
                positionColor = positionColor
            )
        }
    }
}

@Composable
private fun TopScoreCardHeader(
    scoreWithPlayer: ScoreWithPlayer,
    position: Int,
    isPodium: Boolean,
    positionColor: Color
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PositionBadge(position = position, color = positionColor)

        Spacer(modifier = Modifier.width(dimensions.spaceMedium))

        StatsPlayerAvatar(
            name = scoreWithPlayer.player.name,
            isPodium = isPodium,
            podiumColor = positionColor
        )

        Spacer(modifier = Modifier.width(dimensions.spaceMedium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = scoreWithPlayer.player.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formatShortDate(scoreWithPlayer.score.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${scoreWithPlayer.score.turnScore}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ScorePositive
            )
            Text(
                text = stringResource(R.string.points),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TopScoreCardFooter(
    scoreWithPlayer: ScoreWithPlayer,
    isPodium: Boolean,
    positionColor: Color
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(dimensions.spaceMedium),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = dimensions.spaceSmall,
                    vertical = dimensions.spaceExtraSmall
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dice),
                    contentDescription = null,
                    modifier = Modifier.size(dimensions.spaceMedium),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(dimensions.spaceExtraSmall))
                Text(
                    text = stringResource(R.string.round_number, scoreWithPlayer.score.round),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isPodium) {
            Icon(
                painter = painterResource(id = R.drawable.ic_trophy),
                contentDescription = null,
                tint = positionColor,
                modifier = Modifier.size(dimensions.spaceLarge)
            )
        }
    }
}

// ==================== Date Formatting Utilities ====================

private fun formatFullDate(date: Date): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(date)
}

private fun formatShortDate(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(date)
}
