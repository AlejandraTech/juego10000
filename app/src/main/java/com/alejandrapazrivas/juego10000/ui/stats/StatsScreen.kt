package com.alejandrapazrivas.juego10000.ui.stats

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.stats.components.GameHistoryTab
import com.alejandrapazrivas.juego10000.ui.stats.components.PlayerStatsTab
import com.alejandrapazrivas.juego10000.ui.stats.components.TopScoresTab
import kotlinx.coroutines.launch

/**
 * Pantalla principal de estadísticas con navegación por pestañas
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: StatsViewModel = hiltViewModel()
) {
    // Recolectar estados del ViewModel
    val playerStats by viewModel.playerStats.collectAsState(initial = emptyList())
    val gameHistory by viewModel.gameHistory.collectAsState(initial = emptyList())
    val topScores by viewModel.topScores.collectAsState(initial = emptyList())
    val selectedPlayer by viewModel.selectedPlayer.collectAsState()
    val playerGameHistory by viewModel.playerGameHistory.collectAsState(initial = emptyList())

    // Configuración de pestañas
    val tabs = listOf("Jugadores", "Historial", "Récords")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val selectedTab = pagerState.currentPage

    Scaffold(
        topBar = { StatsTopAppBar(navController) }
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
            // Barra de pestañas
            StatsTabRow(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

            // Contenido de pestañas con paginación horizontal
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> PlayerStatsTab(
                        players = playerStats,
                        selectedPlayer = selectedPlayer,
                        onPlayerSelected = { viewModel.selectPlayer(it) },
                        playerGames = playerGameHistory
                    )
                    1 -> GameHistoryTab(gameHistory = gameHistory)
                    2 -> TopScoresTab(topScores = topScores)
                }
            }
        }
    }
}

/**
 * Barra superior de la pantalla de estadísticas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsTopAppBar(navController: NavController) {
    TopAppBar(
        title = { 
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            ) 
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Primary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}

/**
 * Fila de pestañas para la navegación
 */
@Composable
private fun StatsTabRow(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Primary,
        contentColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                height = 3.dp,
                color = Color.White
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = { 
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    ) 
                }
            )
        }
    }
}
