package com.bigotitech.juego10000.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions

/**
 * Avatar del jugador con soporte para estilos de podio.
 * Componente compartido para las tarjetas de estadísticas.
 */
@Composable
fun StatsPlayerAvatar(
    name: String,
    modifier: Modifier = Modifier,
    isPodium: Boolean = false,
    podiumColor: Color = MaterialTheme.colorScheme.primary
) {
    val dimensions = LocalDimensions.current
    val avatarColor = if (isPodium) podiumColor else MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .size(dimensions.avatarSizeMedium)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        avatarColor.copy(alpha = 0.7f),
                        avatarColor.copy(alpha = 0.2f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        avatarColor,
                        avatarColor.copy(alpha = 0.5f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isPodium) Color.White else MaterialTheme.colorScheme.onPrimary
        )
    }
}
