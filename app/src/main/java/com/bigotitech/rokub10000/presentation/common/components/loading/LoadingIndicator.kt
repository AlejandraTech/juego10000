package com.bigotitech.rokub10000.presentation.common.components.loading

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.presentation.common.theme.Primary

private const val ANIMATION_DURATION_MS = 600
private const val DOT_DELAY_MS = 150

/**
 * Indicador de carga animado con puntos pulsantes.
 * Componente reutilizable para mostrar estados de carga.
 *
 * @param modifier Modificador opcional
 * @param dotCount Número de puntos a mostrar
 * @param dotSize Tamaño de cada punto
 * @param dotSpacing Espaciado entre puntos
 * @param dotColor Color de los puntos
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    dotCount: Int = 3,
    dotSize: Dp = 10.dp,
    dotSpacing: Dp = 8.dp,
    dotColor: Color = Primary.copy(alpha = 0.6f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val dotScales = (0 until dotCount).map { index ->
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = ANIMATION_DURATION_MS,
                    delayMillis = index * DOT_DELAY_MS,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot$index"
        )
        scale
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dotScales.forEach { scale ->
            LoadingDot(
                scale = scale,
                size = dotSize,
                color = dotColor
            )
        }
    }
}

@Composable
private fun LoadingDot(
    scale: Float,
    size: Dp,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}
