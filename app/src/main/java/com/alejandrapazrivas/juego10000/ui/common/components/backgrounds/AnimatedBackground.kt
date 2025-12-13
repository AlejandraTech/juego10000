package com.alejandrapazrivas.juego10000.ui.common.components.backgrounds

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary
import kotlin.math.sin

/**
 * Configuración para los elementos decorativos del fondo animado
 */
data class BackgroundConfig(
    val circles: List<CircleConfig> = defaultCircles,
    val dicePositions: List<DiceConfig> = defaultDice,
    val primaryColor: Color = Primary,
    val secondaryColor: Color = Secondary,
    val gradientAlpha: Float = 0.03f,
    val circleBaseAlpha: Float = 0.02f,
    val diceAlpha: Float = 0.06f,
    val floatAnimationDuration1: Int = 8000,
    val floatAnimationDuration2: Int = 10000,
    val rotationDuration: Int = 25000
) {
    companion object {
        val defaultCircles = listOf(
            CircleConfig(0.1f, 0.15f, 70f),
            CircleConfig(0.9f, 0.1f, 50f),
            CircleConfig(0.8f, 0.7f, 90f),
            CircleConfig(0.15f, 0.6f, 60f)
        )

        val defaultDice = listOf(
            DiceConfig(0.05f, 0.3f, 20f),
            DiceConfig(0.95f, 0.45f, 18f),
            DiceConfig(0.1f, 0.85f, 22f),
            DiceConfig(0.88f, 0.8f, 16f)
        )

        val gameConfig = BackgroundConfig(
            circles = listOf(
                CircleConfig(0.08f, 0.1f, 60f),
                CircleConfig(0.92f, 0.15f, 45f),
                CircleConfig(0.85f, 0.75f, 70f),
                CircleConfig(0.1f, 0.65f, 50f)
            ),
            dicePositions = listOf(
                DiceConfig(0.05f, 0.25f, 16f),
                DiceConfig(0.95f, 0.4f, 14f),
                DiceConfig(0.08f, 0.8f, 18f),
                DiceConfig(0.9f, 0.85f, 12f)
            ),
            gradientAlpha = 0.05f,
            circleBaseAlpha = 0.04f,
            diceAlpha = 0.08f,
            floatAnimationDuration1 = 6000,
            floatAnimationDuration2 = 8000,
            rotationDuration = 20000
        )

        val splashConfig = BackgroundConfig(
            circles = listOf(
                CircleConfig(0.15f, 0.2f, 100f),
                CircleConfig(0.85f, 0.15f, 80f),
                CircleConfig(0.75f, 0.8f, 120f),
                CircleConfig(0.1f, 0.75f, 90f)
            ),
            dicePositions = listOf(
                DiceConfig(0.1f, 0.3f, 30f),
                DiceConfig(0.9f, 0.35f, 25f),
                DiceConfig(0.15f, 0.85f, 35f),
                DiceConfig(0.85f, 0.8f, 28f)
            ),
            gradientAlpha = 0.08f,
            circleBaseAlpha = 0.05f,
            diceAlpha = 0.1f,
            floatAnimationDuration1 = 3000,
            floatAnimationDuration2 = 3000,
            rotationDuration = 15000
        )
    }
}

data class CircleConfig(
    val xRatio: Float,
    val yRatio: Float,
    val baseRadiusDp: Float
)

data class DiceConfig(
    val xRatio: Float,
    val yRatio: Float,
    val sizeDp: Float
)

/**
 * Fondo animado reutilizable con círculos y dados flotantes.
 * Utilizado en HomeScreen, GameScreen y SplashScreen.
 */
@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    config: BackgroundConfig = BackgroundConfig()
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_animation")

    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(config.floatAnimationDuration1, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(config.floatAnimationDuration2, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(config.rotationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor,
                        config.primaryColor.copy(alpha = config.gradientAlpha)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Círculos decorativos
            config.circles.forEachIndexed { index, circleConfig ->
                val offsetMultiplier = if (index % 2 == 0) floatOffset1 else floatOffset2
                val yOffset = sin(offsetMultiplier * Math.PI).toFloat() * 25.dp.toPx()
                val baseRadius = circleConfig.baseRadiusDp.dp.toPx()

                val color = if (index % 2 == 0) config.primaryColor else config.secondaryColor
                drawCircle(
                    color = color.copy(alpha = config.circleBaseAlpha + (index * 0.01f)),
                    radius = baseRadius + (offsetMultiplier * 15.dp.toPx()),
                    center = Offset(
                        x = width * circleConfig.xRatio,
                        y = height * circleConfig.yRatio + yOffset
                    )
                )
            }

            // Dados decorativos
            config.dicePositions.forEachIndexed { index, diceConfig ->
                val diceRotation = rotation + (index * 45f)
                val diceSize = diceConfig.sizeDp.dp.toPx()
                val yOffset = sin((floatOffset1 + index * 0.3f) * Math.PI).toFloat() * 12.dp.toPx()

                rotate(diceRotation, pivot = Offset(width * diceConfig.xRatio, height * diceConfig.yRatio + yOffset)) {
                    drawRoundRect(
                        color = config.primaryColor.copy(alpha = config.diceAlpha),
                        topLeft = Offset(
                            x = width * diceConfig.xRatio - diceSize / 2,
                            y = height * diceConfig.yRatio + yOffset - diceSize / 2
                        ),
                        size = Size(diceSize, diceSize),
                        cornerRadius = CornerRadius(3.dp.toPx())
                    )
                }
            }
        }
    }
}
