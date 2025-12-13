package com.alejandrapazrivas.juego10000.ui.userselection.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import kotlin.math.sin

/**
 * Fondo animado con dados flotantes y gradientes
 */
@Composable
fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_animation")

    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val primaryColor = Primary
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor,
                        primaryColor.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Círculos decorativos flotantes
            drawFloatingCircles(
                width = width,
                height = height,
                floatOffset1 = floatOffset1,
                floatOffset2 = floatOffset2,
                primaryColor = primaryColor
            )

            // Dados decorativos (cuadrados rotados)
            drawFloatingDice(
                width = width,
                height = height,
                rotation = rotation,
                floatOffset1 = floatOffset1,
                primaryColor = primaryColor
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFloatingCircles(
    width: Float,
    height: Float,
    floatOffset1: Float,
    floatOffset2: Float,
    primaryColor: androidx.compose.ui.graphics.Color
) {
    val circles = listOf(
        Triple(0.1f, 0.2f, 80.dp.toPx()),
        Triple(0.85f, 0.15f, 60.dp.toPx()),
        Triple(0.7f, 0.8f, 100.dp.toPx()),
        Triple(0.15f, 0.75f, 70.dp.toPx()),
        Triple(0.5f, 0.1f, 50.dp.toPx()),
        Triple(0.9f, 0.5f, 40.dp.toPx())
    )

    circles.forEachIndexed { index, (xRatio, yRatio, baseRadius) ->
        val offsetMultiplier = if (index % 2 == 0) floatOffset1 else floatOffset2
        val yOffset = sin(offsetMultiplier * Math.PI).toFloat() * 30.dp.toPx()

        drawCircle(
            color = primaryColor.copy(alpha = 0.03f + (index * 0.01f)),
            radius = baseRadius + (offsetMultiplier * 20.dp.toPx()),
            center = Offset(
                x = width * xRatio,
                y = height * yRatio + yOffset
            )
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFloatingDice(
    width: Float,
    height: Float,
    rotation: Float,
    floatOffset1: Float,
    primaryColor: androidx.compose.ui.graphics.Color
) {
    val dicePositions = listOf(
        Triple(0.08f, 0.35f, 24.dp.toPx()),
        Triple(0.92f, 0.4f, 20.dp.toPx()),
        Triple(0.2f, 0.9f, 28.dp.toPx()),
        Triple(0.8f, 0.85f, 22.dp.toPx()),
        Triple(0.95f, 0.7f, 18.dp.toPx())
    )

    dicePositions.forEachIndexed { index, (xRatio, yRatio, diceSize) ->
        val diceRotation = rotation + (index * 60f)
        val yOffset = sin((floatOffset1 + index * 0.3f) * Math.PI).toFloat() * 15.dp.toPx()

        rotate(diceRotation, pivot = Offset(width * xRatio, height * yRatio + yOffset)) {
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.08f),
                topLeft = Offset(
                    x = width * xRatio - diceSize / 2,
                    y = height * yRatio + yOffset - diceSize / 2
                ),
                size = Size(diceSize, diceSize),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
}
