package com.bigotitech.rokub10000.ui.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ads.AdConstants
import com.bigotitech.rokub10000.ui.common.components.ads.BannerAd
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.rules.components.AnimatedRuleSection
import com.bigotitech.rokub10000.ui.rules.components.RuleSection
import com.bigotitech.rokub10000.ui.rules.components.RuleSectionBulleted
import com.bigotitech.rokub10000.ui.rules.components.RuleSectionNumbered
import com.bigotitech.rokub10000.ui.rules.components.RulesHeader
import com.bigotitech.rokub10000.ui.rules.components.RulesTopAppBar
import com.bigotitech.rokub10000.ui.rules.components.ScoringCard
import com.bigotitech.rokub10000.ui.rules.model.RulesSection
import com.bigotitech.rokub10000.ui.rules.model.RulesUiState
import kotlinx.coroutines.delay

private const val ANIMATION_DELAY_MS = 100L

@Composable
fun RulesScreen(navController: NavController) {
    var uiState by remember { mutableStateOf(RulesUiState()) }

    LaunchedEffect(Unit) {
        RulesSection.entries.forEach { section ->
            uiState = uiState.showSection(section)
            delay(ANIMATION_DELAY_MS)
        }
    }

    RulesScreenContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() }
    )
}

@Composable
private fun RulesScreenContent(
    uiState: RulesUiState,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = { RulesTopAppBar(onBackClick = onBackClick) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            RulesContentColumn(uiState = uiState)
        }
    }
}

@Composable
private fun RulesContentColumn(uiState: RulesUiState) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.spaceMedium)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        AnimatedRuleSection(visible = uiState.isSectionVisible(RulesSection.HEADER)) {
            RulesHeader()
        }

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        AnimatedRuleSection(visible = uiState.isSectionVisible(RulesSection.OBJECTIVE)) {
            RuleSection(
                icon = R.drawable.ic_trophy,
                title = stringResource(R.string.game_objective),
                content = stringResource(R.string.objective_content)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        AnimatedRuleSection(visible = uiState.isSectionVisible(RulesSection.ENTRANCE)) {
            RuleSection(
                icon = R.drawable.ic_dice,
                title = stringResource(R.string.game_entry),
                content = stringResource(R.string.entry_content)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        AnimatedRuleSection(visible = uiState.isSectionVisible(RulesSection.MECHANICS)) {
            TurnMechanicsSection()
        }

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        AnimatedRuleSection(visible = uiState.isSectionVisible(RulesSection.SCORING)) {
            ScoringCard()
        }

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        AnimatedRuleSection(visible = uiState.isSectionVisible(RulesSection.STRATEGIES)) {
            StrategiesSection()
        }

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        BannerAd(
            adUnitId = AdConstants.BANNER_RULES,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))
    }
}

@Composable
private fun TurnMechanicsSection() {
    RuleSectionNumbered(
        icon = R.drawable.ic_dice,
        title = stringResource(R.string.turn_mechanics),
        items = listOf(
            stringResource(R.string.turn_mechanic_1),
            stringResource(R.string.turn_mechanic_2),
            stringResource(R.string.turn_mechanic_3),
            stringResource(R.string.turn_mechanic_4),
            stringResource(R.string.turn_mechanic_5),
            stringResource(R.string.turn_mechanic_6)
        )
    )
}

@Composable
private fun StrategiesSection() {
    RuleSectionBulleted(
        icon = R.drawable.ic_settings,
        title = stringResource(R.string.strategies),
        items = listOf(
            stringResource(R.string.strategy_1),
            stringResource(R.string.strategy_2),
            stringResource(R.string.strategy_3),
            stringResource(R.string.strategy_4)
        )
    )
}
