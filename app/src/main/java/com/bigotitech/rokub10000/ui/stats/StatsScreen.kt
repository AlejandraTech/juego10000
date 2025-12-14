package com.bigotitech.rokub10000.ui.stats

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ads.AdConstants
import com.bigotitech.rokub10000.ui.common.components.ads.BannerAd
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary
import com.bigotitech.rokub10000.ui.stats.components.GameHistoryTab
import com.bigotitech.rokub10000.ui.stats.components.PlayerStatsTab
import com.bigotitech.rokub10000.ui.stats.components.StatsTopAppBar
import com.bigotitech.rokub10000.ui.stats.components.TopScoresTab
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

    // Configuración de pestañas
    val tabs = listOf(
        stringResource(R.string.tab_players),
        stringResource(R.string.tab_history),
        stringResource(R.string.tab_records)
    )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val selectedTab = pagerState.currentPage

    Scaffold(
        topBar = { StatsTopAppBar(onBackClick = { navController.popBackStack() }) }
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
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> PlayerStatsTab(players = playerStats)
                    1 -> GameHistoryTab(gameHistory = gameHistory)
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

@Composable
private fun StatsTabRow(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val dimensions = LocalDimensions.current
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Primary,
        contentColor = Color.White,
        indicator = { tabPositions ->
            SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                height = dimensions.spaceExtraSmall - 1.dp,
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
