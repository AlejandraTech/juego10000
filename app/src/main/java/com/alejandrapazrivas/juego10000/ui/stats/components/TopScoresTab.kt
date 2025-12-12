package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.stats.StatsViewModel.ScoreWithPlayer

/**
 * Pestaña que muestra las mejores puntuaciones
 */
@Composable
fun TopScoresTab(topScores: List<ScoreWithPlayer>) {
    val dimensions = LocalDimensions.current
    if (topScores.isEmpty()) {
        EmptyStateMessage(
            message = stringResource(R.string.no_registered_scores),
            iconResId = R.drawable.ic_trophy
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensions.spaceMedium)
        ) {
            Text(
                text = stringResource(R.string.top_scores),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = dimensions.spaceMedium)
            )

            // Usamos el componente de animación para la lista
            AnimatedEntrance {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimensions.spaceSmall + dimensions.spaceExtraSmall)
                ) {
                    itemsIndexed(topScores) { index, scoreWithPlayer ->
                        TopScoreCard(
                            scoreWithPlayer = scoreWithPlayer,
                            position = index + 1
                        )
                    }
                }
            }
        }
    }
}
