package com.bigotitech.juego10000.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.ui.common.theme.Indigo
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions
import com.bigotitech.juego10000.ui.home.model.UserStats

@Composable
fun QuickStatsSection(
    stats: UserStats,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.your_stats),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = dimensions.spaceSmall)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
        ) {
            StatCard(
                title = stringResource(R.string.games_played),
                value = stats.totalGamesPlayed.toString(),
                icon = R.drawable.ic_dice,
                gradientColors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                ),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.games_won),
                value = stats.totalWins.toString(),
                icon = R.drawable.ic_trophy,
                gradientColors = listOf(
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
        ) {
            StatCard(
                title = stringResource(R.string.best_turn),
                value = stats.bestTurnScore.toString(),
                icon = R.drawable.ic_stats,
                gradientColors = listOf(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                ),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.win_rate),
                value = "${stats.winRate.toInt()}%",
                icon = R.drawable.ic_stats,
                gradientColors = listOf(
                    Indigo,
                    Indigo.copy(alpha = 0.7f)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: Int,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedAlpha by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "stat_card_alpha"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    val cardHeight = dimensions.buttonHeight * 2

    Card(
        modifier = modifier.height(cardHeight),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.cardElevation)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .background(
                    brush = Brush.linearGradient(colors = gradientColors)
                )
                .padding(dimensions.spaceSmall)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.iconSizeLarge)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(dimensions.iconSizeSmall)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}
