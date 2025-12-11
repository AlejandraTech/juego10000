package com.alejandrapazrivas.juego10000.ui.home.components

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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

@Composable
fun MiniPerformanceChart(
    scores: List<Int>,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val chartHeight = dimensions.buttonHeight * 2

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.cardElevation / 2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            Text(
                text = "Rendimiento Reciente",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(dimensions.spaceExtraSmall))

            Text(
                text = if (scores.isNotEmpty()) "Últimas ${scores.size} partidas" else "Sin datos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            if (scores.isNotEmpty()) {
                var animationPlayed by remember { mutableStateOf(false) }
                val animationProgress by animateFloatAsState(
                    targetValue = if (animationPlayed) 1f else 0f,
                    animationSpec = tween(durationMillis = 800),
                    label = "chart_animation"
                )

                LaunchedEffect(Unit) {
                    animationPlayed = true
                }

                SimpleBarChart(
                    data = scores.map { it.toFloat() },
                    barColor = primaryColor,
                    animationProgress = animationProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
                )

                Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                // Estadísticas rápidas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val maxScore = scores.maxOrNull() ?: 0
                    val avgScore = if (scores.isNotEmpty()) scores.average().toInt() else 0

                    ChartStat(label = "Máximo", value = maxScore.toString())
                    ChartStat(label = "Promedio", value = avgScore.toString())
                    ChartStat(label = "Partidas", value = scores.size.toString())
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    surfaceVariant.copy(alpha = 0.3f),
                                    surfaceVariant.copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(dimensions.spaceSmall)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Juega partidas para ver tu rendimiento",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleBarChart(
    data: List<Float>,
    barColor: Color,
    animationProgress: Float,
    modifier: Modifier = Modifier
) {
    val maxValue = data.maxOrNull() ?: 1f

    Canvas(modifier = modifier) {
        val barWidth = (size.width - (data.size - 1) * 8.dp.toPx()) / data.size
        val maxHeight = size.height * 0.9f

        data.forEachIndexed { index, value ->
            val barHeight = (value / maxValue) * maxHeight * animationProgress
            val x = index * (barWidth + 8.dp.toPx())
            val y = size.height - barHeight

            drawRoundRect(
                color = barColor.copy(alpha = 0.3f),
                topLeft = Offset(x, size.height - maxHeight),
                size = Size(barWidth, maxHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
}

@Composable
private fun ChartStat(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
