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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.stats.StatsViewModel.ScoreWithPlayer

/**
 * Pestaña que muestra las mejores puntuaciones
 */
@Composable
fun TopScoresTab(topScores: List<ScoreWithPlayer>) {
    if (topScores.isEmpty()) {
        EmptyStateMessage(
            message = "No hay puntuaciones registradas",
            iconResId = R.drawable.ic_trophy
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Mejores puntuaciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Usamos el componente de animación para la lista
            AnimatedEntrance {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
