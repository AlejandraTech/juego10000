package com.bigotitech.juego10000.ui.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.ui.common.components.animation.AnimatedEntrance
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions
import com.bigotitech.juego10000.ui.stats.model.ScoreWithPlayer

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
        AnimatedEntrance {
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
}
