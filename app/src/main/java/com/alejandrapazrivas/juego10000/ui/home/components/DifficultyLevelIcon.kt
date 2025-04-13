package com.alejandrapazrivas.juego10000.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Icono que representa visualmente un nivel de dificultad mediante barras verticales.
 * Cuanto más alto sea el nivel, más barras aparecerán resaltadas.
 */
@Composable
fun DifficultyLevelIcon(
    level: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.size(24.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Barra izquierda (la más alta)
        DifficultyBar(
            height = 20.dp,
            isActive = level >= 3,
            isSelected = isSelected
        )
        
        // Barra central (altura media)
        DifficultyBar(
            height = 14.dp,
            isActive = level >= 2,
            isSelected = isSelected
        )
        
        // Barra derecha (la más baja)
        DifficultyBar(
            height = 8.dp,
            isActive = level >= 1,
            isSelected = isSelected
        )
    }
}

@Composable
private fun DifficultyBar(
    height: androidx.compose.ui.unit.Dp,
    isActive: Boolean,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .width(5.dp)
            .height(height)
            .clip(RoundedCornerShape(2.dp))
            .background(
                color = if (isActive) 
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                else 
                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
    )
}
