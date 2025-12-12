package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.stats.StatsViewModel.PlayerStats

@Composable
fun PlayerScoresChart(
    players: List<PlayerStats>,
    modifier: Modifier = Modifier
) {
    if (players.isEmpty()) return

    val dimensions = LocalDimensions.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.spaceMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.spaceExtraSmall / 2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            Text(
                text = stringResource(R.string.best_scores_by_player),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            var animationPlayed by remember { mutableStateOf(false) }
            val animationProgress by animateFloatAsState(
                targetValue = if (animationPlayed) 1f else 0f,
                animationSpec = tween(durationMillis = 800),
                label = "scores_chart_animation"
            )

            LaunchedEffect(Unit) {
                animationPlayed = true
            }

            val scores = players.map { it.bestScore.toFloat() }
            val colors = players.mapIndexed { index, _ -> getChartColor(index) }

            HorizontalBarChart(
                data = scores,
                labels = players.map { it.player.name },
                colors = colors,
                animationProgress = animationProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensions.buttonHeight * 3)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall + dimensions.spaceExtraSmall))

            // Leyenda
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.spaceMedium)
            ) {
                players.take(4).forEachIndexed { index, player ->
                    LegendItem(
                        name = player.player.name,
                        color = getChartColor(index)
                    )
                }
            }
        }
    }
}

@Composable
private fun HorizontalBarChart(
    data: List<Float>,
    labels: List<String>,
    colors: List<Color>,
    animationProgress: Float,
    modifier: Modifier = Modifier
) {
    val maxValue = data.maxOrNull() ?: 1f

    Canvas(modifier = modifier) {
        val barHeight = (size.height - (data.size - 1) * 8.dp.toPx()) / data.size
        val maxWidth = size.width * 0.7f

        data.forEachIndexed { index, value ->
            val barWidth = (value / maxValue) * maxWidth * animationProgress
            val y = index * (barHeight + 8.dp.toPx())

            // Fondo de la barra
            drawRoundRect(
                color = colors.getOrElse(index) { Color.Gray }.copy(alpha = 0.2f),
                topLeft = Offset(0f, y),
                size = Size(maxWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            // Barra de progreso
            drawRoundRect(
                color = colors.getOrElse(index) { Color.Gray },
                topLeft = Offset(0f, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
}

@Composable
fun WinRateChart(
    players: List<PlayerStats>,
    modifier: Modifier = Modifier
) {
    if (players.isEmpty()) return

    val dimensions = LocalDimensions.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.spaceMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.spaceExtraSmall / 2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            Text(
                text = stringResource(R.string.win_rate_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                players.take(4).forEachIndexed { index, player ->
                    val winRate = if (player.player.gamesPlayed > 0) {
                        (player.player.gamesWon.toFloat() / player.player.gamesPlayed) * 100
                    } else 0f

                    WinRateItem(
                        name = player.player.name,
                        winRate = winRate,
                        color = getChartColor(index)
                    )
                }
            }
        }
    }
}

@Composable
private fun WinRateItem(
    name: String,
    winRate: Float,
    color: Color
) {
    val dimensions = LocalDimensions.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.avatarSizeMedium + dimensions.spaceExtraSmall)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${winRate.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1
        )
    }
}

@Composable
private fun LegendItem(
    name: String,
    color: Color
) {
    val dimensions = LocalDimensions.current
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.spaceSmall)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(dimensions.spaceExtraSmall))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1
        )
    }
}

private fun getChartColor(index: Int): Color {
    return when (index % 4) {
        0 -> Color(0xFF6366F1) // Indigo
        1 -> Color(0xFF10B981) // Emerald
        2 -> Color(0xFFF59E0B) // Amber
        3 -> Color(0xFFEF4444) // Red
        else -> Color(0xFF6366F1)
    }
}
