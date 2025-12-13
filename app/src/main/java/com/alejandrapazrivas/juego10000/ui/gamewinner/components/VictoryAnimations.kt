package com.alejandrapazrivas.juego10000.ui.gamewinner.components

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.alejandrapazrivas.juego10000.ui.common.theme.GoldColor
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Fondo animado con gradiente y rayos de luz para la pantalla de victoria.
 */
@Composable
fun AnimatedVictoryBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "victory_bg")

    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        Primary.copy(alpha = 0.05f + gradientOffset * 0.05f),
                        Secondary.copy(alpha = 0.08f),
                        GoldColor.copy(alpha = 0.03f + gradientOffset * 0.02f),
                        backgroundColor
                    )
                )
            )
    ) {
        LightRaysCanvas(gradientOffset = gradientOffset)
    }
}

@Composable
private fun LightRaysCanvas(
    gradientOffset: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height * 0.35f

        for (i in 0 until 12) {
            val angle = (i * 30f + gradientOffset * 30f) * (Math.PI / 180f)
            val length = size.width * 0.8f

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        GoldColor.copy(alpha = 0.15f),
                        GoldColor.copy(alpha = 0f)
                    ),
                    start = Offset(centerX, centerY),
                    end = Offset(
                        centerX + (cos(angle) * length).toFloat(),
                        centerY + (sin(angle) * length).toFloat()
                    )
                ),
                start = Offset(centerX, centerY),
                end = Offset(
                    centerX + (cos(angle) * length).toFloat(),
                    centerY + (sin(angle) * length).toFloat()
                ),
                strokeWidth = 40f
            )
        }
    }
}

/**
 * Animación de confeti cayendo para celebrar la victoria.
 */
@Composable
fun ConfettiAnimation(modifier: Modifier = Modifier) {
    val particles = remember {
        List(50) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -1f,
                speed = 0.002f + Random.nextFloat() * 0.003f,
                size = 8f + Random.nextFloat() * 12f,
                color = listOf(
                    GoldColor,
                    Primary,
                    Secondary,
                    Color(0xFFFF6B6B),
                    Color(0xFF4ECDC4),
                    Color(0xFFFFE66D)
                ).random(),
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = -2f + Random.nextFloat() * 4f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val currentY = (particle.y + time * particle.speed * 500f) % 1.2f
            val currentRotation = particle.rotation + time * particle.rotationSpeed * 360f

            if (currentY > -0.1f) {
                rotate(currentRotation, pivot = Offset(size.width * particle.x, size.height * currentY)) {
                    drawRoundRect(
                        color = particle.color,
                        topLeft = Offset(
                            size.width * particle.x - particle.size / 2,
                            size.height * currentY - particle.size / 2
                        ),
                        size = Size(particle.size, particle.size * 0.6f),
                        cornerRadius = CornerRadius(2f)
                    )
                }
            }
        }
    }
}

/**
 * Datos de una partícula de confeti.
 */
private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotation: Float,
    val rotationSpeed: Float
)
