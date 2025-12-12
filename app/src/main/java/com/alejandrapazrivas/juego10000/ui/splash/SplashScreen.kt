package com.alejandrapazrivas.juego10000.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val dimensions = LocalDimensions.current

    // Animaciones de entrada
    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animar logo
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        )
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(400)
        )

        // Animar texto
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(400)
        )

        // Esperar un momento antes de navegar
        delay(800)
        onSplashFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Fondo animado
        SplashBackground()

        // Contenido central
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo animado
            Box(
                modifier = Modifier
                    .size(dimensions.avatarSizeLarge + dimensions.spaceLarge)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Primary,
                                Primary.copy(alpha = 0.8f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.size(dimensions.avatarSizeLarge + dimensions.spaceLarge)
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceLarge))

            // Título de la app
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            // Indicador de carga
            LoadingIndicator(
                modifier = Modifier.alpha(textAlpha.value)
            )
        }
    }
}

@Composable
private fun SplashBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_bg")

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val primaryColor = Primary
    val secondaryColor = Secondary
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor,
                        primaryColor.copy(alpha = 0.08f)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Círculos decorativos
            val circles = listOf(
                Triple(0.15f, 0.2f, 100.dp.toPx()),
                Triple(0.85f, 0.15f, 80.dp.toPx()),
                Triple(0.75f, 0.8f, 120.dp.toPx()),
                Triple(0.1f, 0.75f, 90.dp.toPx())
            )

            circles.forEachIndexed { index, (xRatio, yRatio, baseRadius) ->
                val yOffset = sin(floatOffset * Math.PI + index).toFloat() * 20.dp.toPx()

                drawCircle(
                    color = if (index % 2 == 0) primaryColor.copy(alpha = 0.05f) else secondaryColor.copy(alpha = 0.04f),
                    radius = baseRadius + (floatOffset * 15.dp.toPx()),
                    center = Offset(
                        x = width * xRatio,
                        y = height * yRatio + yOffset
                    )
                )
            }

            // Dados decorativos
            val dicePositions = listOf(
                Triple(0.1f, 0.3f, 30.dp.toPx()),
                Triple(0.9f, 0.35f, 25.dp.toPx()),
                Triple(0.15f, 0.85f, 35.dp.toPx()),
                Triple(0.85f, 0.8f, 28.dp.toPx())
            )

            dicePositions.forEachIndexed { index, (xRatio, yRatio, diceSize) ->
                val diceRotation = rotation + (index * 90f)
                val yOffset = sin((floatOffset + index * 0.5f) * Math.PI).toFloat() * 10.dp.toPx()

                rotate(diceRotation, pivot = Offset(width * xRatio, height * yRatio + yOffset)) {
                    drawRoundRect(
                        color = primaryColor.copy(alpha = 0.1f),
                        topLeft = Offset(
                            x = width * xRatio - diceSize / 2,
                            y = height * yRatio + yOffset - diceSize / 2
                        ),
                        size = androidx.compose.ui.geometry.Size(diceSize, diceSize),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val dotScale1 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dotScale2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 150, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dotScale3 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoadingDot(scale = dotScale1)
        LoadingDot(scale = dotScale2)
        LoadingDot(scale = dotScale3)
    }
}

@Composable
private fun LoadingDot(scale: Float) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Primary.copy(alpha = 0.6f))
    )
}
