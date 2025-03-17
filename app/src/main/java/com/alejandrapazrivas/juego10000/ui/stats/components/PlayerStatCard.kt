package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.ScorePositive
import com.alejandrapazrivas.juego10000.ui.stats.StatsViewModel.PlayerStats

/**
 * Tarjeta que muestra las estadísticas de un jugador
 */
@Composable
fun PlayerStatCard(
    playerStat: PlayerStats,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = CardShape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            else MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar del jugador
            PlayerAvatar(playerStat.player.name)
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del jugador
            PlayerInfo(playerStat)
            
            // Indicador de tasa de victorias
            WinRateIndicator(playerStat.player.winRate)
        }
    }
}

/**
 * Avatar circular para el jugador
 */
@Composable
private fun PlayerAvatar(playerName: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = Primary.copy(alpha = 0.2f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = playerName.take(1).uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            color = Primary
        )
    }
}

/**
 * Información del jugador (nombre, partidas, victorias, mejor puntuación)
 */
@Composable
private fun PlayerInfo(playerStat: PlayerStats) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = playerStat.player.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Partidas jugadas
        PlayerStatRow(
            icon = R.drawable.ic_dice,
            text = "Partidas: ${playerStat.player.gamesPlayed}",
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )

        // Victorias
        PlayerStatRow(
            icon = R.drawable.ic_trophy,
            text = "Victorias: ${playerStat.player.gamesWon}",
            tint = Color(0xFFFFC107)
        )

        // Mejor puntuación
        Text(
            text = "Mejor puntuación: ${playerStat.bestScore}",
            style = MaterialTheme.typography.bodyMedium,
            color = ScorePositive
        )
    }
}

/**
 * Fila con icono y texto para mostrar una estadística
 */
@Composable
private fun PlayerStatRow(
    icon: Int,
    text: String,
    tint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = tint
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Indicador circular de tasa de victorias
 */
@Composable
private fun WinRateIndicator(winRate: Float) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        val animatedProgress by animateFloatAsState(
            targetValue = winRate,
            animationSpec = tween(1000),
            label = "progress_animation"
        )

        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 4.dp,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = ScorePositive
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(winRate * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ScorePositive
            )
        }
    }
}
