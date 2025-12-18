package com.bigotitech.rokub10000.presentation.feature.rules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.presentation.common.theme.LocalDimensions
import com.bigotitech.rokub10000.presentation.common.theme.Primary
import com.bigotitech.rokub10000.presentation.common.theme.Secondary
import com.bigotitech.rokub10000.presentation.feature.rules.state.RulesSection
import com.bigotitech.rokub10000.presentation.feature.rules.state.RulesUiState

/**
 * Pantalla de reglas del juego.
 * Muestra las reglas organizadas en secciones con animaciones de aparición.
 */
@Composable
fun RulesScreen(
    onNavigateBack: () -> Unit,
    viewModel: RulesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    RulesScreenContent(
        uiState = uiState,
        onBackClick = onNavigateBack
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
        Column(
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
            RulesContentColumn(
                uiState = uiState,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ==================== Top App Bar ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RulesTopAppBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.rules_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

// ==================== Rules Content ====================

@Composable
private fun RulesContentColumn(
    uiState: RulesUiState,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier
            .fillMaxWidth()
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

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))
    }
}

// ==================== Animated Section ====================

@Composable
private fun AnimatedRuleSection(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(400)) +
                expandVertically(animationSpec = tween(400))
    ) {
        content()
    }
}

// ==================== Rules Header ====================

@Composable
private fun RulesHeader() {
    val dimensions = LocalDimensions.current
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500),
        label = "header_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = scale
            }
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.15f),
                        Primary.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(vertical = dimensions.spaceLarge, horizontal = dimensions.spaceMedium),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Primary,
                                Primary.copy(alpha = 0.8f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_rules),
                    contentDescription = stringResource(R.string.rules),
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Text(
                text = stringResource(R.string.rules_header_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Primary
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            Text(
                text = stringResource(R.string.rules_header_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

// ==================== Rule Card ====================

@Composable
private fun RuleCard(
    icon: Int,
    title: String,
    content: @Composable () -> Unit
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spaceSmall)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.2f),
                                Primary.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(20.dp),
                    tint = Primary
                )
            }

            Spacer(modifier = Modifier.width(dimensions.spaceSmall))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.3f),
                            Primary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        content()
    }
}

// ==================== Rule Section ====================

@Composable
private fun RuleSection(icon: Int, title: String, content: String) {
    RuleCard(icon = icon, title = title) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start
        )
    }
}

// ==================== Turn Mechanics Section ====================

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
private fun RuleSectionNumbered(icon: Int, title: String, items: List<String>) {
    RuleCard(icon = icon, title = title) {
        Column(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, item ->
                NumberedListItem(
                    number = index + 1,
                    text = item
                )
                if (index < items.lastIndex) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun NumberedListItem(number: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = stringResource(R.string.number_format, number),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Primary,
            modifier = Modifier.width(24.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

// ==================== Strategies Section ====================

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

@Composable
private fun RuleSectionBulleted(icon: Int, title: String, items: List<String>) {
    RuleCard(icon = icon, title = title) {
        Column(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, item ->
                BulletedListItem(text = item)
                if (index < items.lastIndex) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun BulletedListItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = stringResource(R.string.bullet_point),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Secondary,
            modifier = Modifier.width(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

// ==================== Scoring Card ====================

@Composable
private fun ScoringCard() {
    RuleCard(
        icon = R.drawable.ic_stats,
        title = stringResource(R.string.scoring_system)
    ) {
        ScoringSection(
            title = stringResource(R.string.individual_dice),
            content = {
                DiceScoreRow(diceValue = 1, points = 100)
                DiceScoreRow(diceValue = 5, points = 50)
            }
        )

        ScoringSection(
            title = stringResource(R.string.combinations),
            content = {
                CombinationScoreRow(combination = stringResource(R.string.three_ones), points = 1000)
                CombinationScoreRow(combination = stringResource(R.string.three_twos), points = 200)
                CombinationScoreRow(combination = stringResource(R.string.three_threes), points = 300)
                CombinationScoreRow(combination = stringResource(R.string.three_fours), points = 400)
                CombinationScoreRow(combination = stringResource(R.string.three_fives), points = 500)
                CombinationScoreRow(combination = stringResource(R.string.three_sixes), points = 600)
                CombinationScoreRow(combination = stringResource(R.string.straight), points = 1500)
                CombinationScoreRow(combination = stringResource(R.string.three_pairs), points = 1500)
            }
        )

        ScoringSection(
            title = stringResource(R.string.multipliers),
            content = {
                MultiplierRow(text = stringResource(R.string.four_of_a_kind_desc))
                MultiplierRow(text = stringResource(R.string.five_of_a_kind_desc))
                MultiplierRow(text = stringResource(R.string.six_of_a_kind_desc))
            }
        )
    }
}

// ==================== Scoring Section ====================

@Composable
private fun ScoringSection(title: String, content: @Composable () -> Unit) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier.padding(vertical = dimensions.spaceSmall)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Secondary)
            )

            Spacer(modifier = Modifier.width(dimensions.spaceSmall))

            Text(
                text = title.removeSuffix(":"),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Column(
            modifier = Modifier.padding(start = dimensions.spaceMedium)
        ) {
            content()
        }
    }
}

// ==================== Score Rows ====================

@Composable
private fun ScoreRow(
    points: Int,
    leadingContent: @Composable () -> Unit,
    useGradient: Boolean = false,
    backgroundColor: Color = Primary.copy(alpha = 0.15f)
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingContent()

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = if (useGradient) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.2f),
                                Secondary.copy(alpha = 0.15f)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(backgroundColor, backgroundColor)
                        )
                    }
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.points_format, points),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
    }
}

@Composable
private fun DiceScoreRow(diceValue: Int, points: Int) {
    val dimensions = LocalDimensions.current

    ScoreRow(
        points = points,
        leadingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = getDiceDrawable(diceValue)),
                        contentDescription = stringResource(R.string.dice_description, diceValue),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.spaceSmall))

                Text(
                    text = stringResource(R.string.dice_value, diceValue),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
private fun CombinationScoreRow(combination: String, points: Int) {
    val dimensions = LocalDimensions.current

    ScoreRow(
        points = points,
        useGradient = true,
        leadingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Secondary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dice),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Secondary
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.spaceSmall))

                Text(
                    text = combination,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
private fun MultiplierRow(text: String) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Primary)
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

// ==================== Utility Functions ====================

private fun getDiceDrawable(value: Int): Int {
    return when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1
    }
}
