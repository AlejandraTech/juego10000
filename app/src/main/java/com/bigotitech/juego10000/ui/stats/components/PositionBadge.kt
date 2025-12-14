package com.bigotitech.juego10000.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions

/**
 * Colores para las posiciones del podio
 */
object PodiumColors {
    val Gold = Color(0xFFFFD700)
    val Silver = Color(0xFFC0C0C0)
    val Bronze = Color(0xFFCD7F32)
    val Default = Color(0xFF6B7280)

    fun getPositionColor(position: Int): Color {
        return when (position) {
            1 -> Gold
            2 -> Silver
            3 -> Bronze
            else -> Default
        }
    }

    fun isPodium(position: Int): Boolean = position in 1..3
}

/**
 * Insignia circular que muestra la posición en el ranking
 */
@Composable
fun PositionBadge(
    position: Int,
    modifier: Modifier = Modifier,
    color: Color = PodiumColors.getPositionColor(position)
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier
            .size(dimensions.spaceLarge)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.position_format, position),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
