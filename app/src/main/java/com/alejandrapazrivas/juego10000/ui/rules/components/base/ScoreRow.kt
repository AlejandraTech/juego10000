package com.alejandrapazrivas.juego10000.ui.rules.components.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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

/**
 * Componente base para las filas de puntuación.
 * 
 * @param points Puntos a mostrar
 * @param leadingContent Contenido composable a mostrar al inicio de la fila
 * @param useGradient Si se debe usar un gradiente para el fondo de la puntuación
 * @param backgroundColor Color de fondo para la puntuación (ignorado si useGradient es true)
 */
@Composable
fun ScoreRow(
    points: Int,
    leadingContent: @Composable () -> Unit,
    useGradient: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingContent()
        
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    color = if (!useGradient) backgroundColor else Color.Transparent
                )
                .background(
                    brush = if (useGradient) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                backgroundColor,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(colors = listOf(backgroundColor, backgroundColor))
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$points pts",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
