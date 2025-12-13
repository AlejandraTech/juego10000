package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.components.emptystate.EmptyStateMessage as SharedEmptyStateMessage

/**
 * Componente que muestra un mensaje cuando no hay datos disponibles.
 * Wrapper sobre el componente compartido para mantener compatibilidad.
 */
@Composable
fun EmptyStateMessage(
    message: String,
    iconResId: Int = R.drawable.ic_stats,
    modifier: Modifier = Modifier
) {
    SharedEmptyStateMessage(
        message = message,
        icon = painterResource(id = iconResId),
        modifier = modifier,
        iconTint = Color.Gray.copy(alpha = 0.7f)
    )
}
