package com.bigotitech.rokub10000.ui.home.components

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions

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
    val dimensions = LocalDimensions.current
    val iconSize = dimensions.iconSizeMedium

    Row(
        modifier = modifier.size(iconSize),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Barra izquierda (la más alta)
        DifficultyBar(
            height = iconSize * 0.83f,
            isActive = level >= 3,
            isSelected = isSelected
        )

        // Barra central (altura media)
        DifficultyBar(
            height = iconSize * 0.58f,
            isActive = level >= 2,
            isSelected = isSelected
        )

        // Barra derecha (la más baja)
        DifficultyBar(
            height = iconSize * 0.33f,
            isActive = level >= 1,
            isSelected = isSelected
        )
    }
}

@Composable
private fun DifficultyBar(
    height: Dp,
    isActive: Boolean,
    isSelected: Boolean
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier
            .width(dimensions.spaceExtraSmall + 1.dp)
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
