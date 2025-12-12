package com.alejandrapazrivas.juego10000.ui.gamewinner

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ads.AdManager
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.ButtonShape
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val GoldColor = Color(0xFFFFD700)
private val GoldDark = Color(0xFFDAA520)
private val GoldLight = Color(0xFFFFF8DC)

@Composable
fun GameVictoryScreen(
    winner: Player?,
    score: Int,
    onBackToHome: () -> Unit,
    adManager: AdManager? = null
) {
    val dimensions = LocalDimensions.current
    val context = LocalContext.current

    // Animaciones de entrada
    val contentAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.8f) }

    // Animación del contador de puntuación
    var displayedScore by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        adManager?.loadInterstitial()

        // Animar entrada
        contentAlpha.animateTo(1f, animationSpec = tween(500))
        contentScale.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))

        // Animar contador de puntuación
        val steps = 30
        val increment = score / steps
        repeat(steps) {
            delay(30)
            displayedScore = minOf(displayedScore + increment, score)
        }
        displayedScore = score
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Fondo animado con gradiente
        AnimatedVictoryBackground()

        // Confeti cayendo
        ConfettiAnimation()

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensions.spaceLarge)
                .alpha(contentAlpha.value)
                .scale(contentScale.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título de victoria con efecto
            VictoryTitle()

            Spacer(modifier = Modifier.height(dimensions.spaceLarge))

            // Trofeo animado con corona
            AnimatedTrophySection()

            Spacer(modifier = Modifier.height(dimensions.spaceLarge))

            // Card del ganador
            winner?.let {
                WinnerCard(
                    playerName = it.name,
                    score = displayedScore
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

            // Botón de volver
            Button(
                onClick = {
                    if (adManager != null && adManager.isInterstitialReady()) {
                        (context as? Activity)?.let { activity ->
                            adManager.showInterstitial(activity) {
                                onBackToHome()
                            }
                        } ?: onBackToHome()
                    } else {
                        onBackToHome()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(dimensions.buttonHeight)
                    .shadow(
                        elevation = 8.dp,
                        shape = ButtonShape,
                        spotColor = Primary.copy(alpha = 0.5f)
                    ),
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(dimensions.spaceSmall))
                Text(
                    text = stringResource(R.string.back_to_main_menu),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AnimatedVictoryBackground() {
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
        modifier = Modifier
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
        // Rayos de luz desde el centro
        Canvas(modifier = Modifier.fillMaxSize()) {
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
}

@Composable
private fun ConfettiAnimation() {
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

    Canvas(modifier = Modifier.fillMaxSize()) {
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
                        size = androidx.compose.ui.geometry.Size(particle.size, particle.size * 0.6f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
                    )
                }
            }
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotation: Float,
    val rotationSpeed: Float
)

@Composable
private fun VictoryTitle() {
    val infiniteTransition = rememberInfiniteTransition(label = "title")

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Estrellas decorativas
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StarIcon(size = 20.dp, delay = 0)
            Spacer(modifier = Modifier.width(8.dp))
            StarIcon(size = 28.dp, delay = 100)
            Spacer(modifier = Modifier.width(8.dp))
            StarIcon(size = 20.dp, delay = 200)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.victory),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.scale(scale),
            color = GoldColor
        )
    }
}

@Composable
private fun StarIcon(size: androidx.compose.ui.unit.Dp, delay: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "star_$delay")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, delayMillis = delay, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = delay, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .alpha(alpha)
            .rotate(rotation),
        tint = GoldColor
    )
}

@Composable
private fun AnimatedTrophySection() {
    val dimensions = LocalDimensions.current
    val infiniteTransition = rememberInfiniteTransition(label = "trophy")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophy_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        contentAlignment = Alignment.Center
    ) {
        // Glow exterior
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(scale * 1.2f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GoldColor.copy(alpha = glowAlpha * 0.4f),
                            GoldColor.copy(alpha = glowAlpha * 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Círculo de fondo con gradiente
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(scale)
                .rotate(rotation)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    spotColor = GoldColor.copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GoldLight,
                            GoldColor,
                            GoldDark
                        )
                    )
                )
                .border(
                    width = 4.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            GoldColor,
                            GoldDark
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_trophy),
                contentDescription = stringResource(R.string.trophy),
                modifier = Modifier.size(80.dp),
                tint = Color.White
            )
        }

        // Sparkles alrededor del trofeo
        SparklesAroundTrophy()
    }
}

@Composable
private fun SparklesAroundTrophy() {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkles")

    val positions = listOf(
        Pair(-70.dp, -50.dp),
        Pair(70.dp, -40.dp),
        Pair(-60.dp, 50.dp),
        Pair(65.dp, 55.dp),
        Pair(0.dp, -75.dp)
    )

    positions.forEachIndexed { index, (x, y) ->
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, delayMillis = index * 150, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "sparkle_$index"
        )

        val scale by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, delayMillis = index * 150, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "sparkle_scale_$index"
        )

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier
                .offset(x = x, y = y)
                .size(16.dp)
                .scale(scale)
                .alpha(alpha),
            tint = GoldColor
        )
    }
}

@Composable
private fun WinnerCard(
    playerName: String,
    score: Int
) {
    val dimensions = LocalDimensions.current
    val infiniteTransition = rememberInfiniteTransition(label = "winner_card")

    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = GoldColor.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GoldColor.copy(alpha = borderAlpha),
                        GoldDark.copy(alpha = borderAlpha * 0.7f),
                        GoldColor.copy(alpha = borderAlpha)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(dimensions.spaceLarge)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar del ganador
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(8.dp, CircleShape, spotColor = Primary.copy(alpha = 0.4f))
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Primary,
                                Primary.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(GoldColor, GoldDark)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = playerName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            // Nombre del ganador
            Text(
                text = playerName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            // Etiqueta de campeón
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GoldColor, GoldDark)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.champion_label),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceLarge))

            // Puntuación final
            Text(
                text = stringResource(R.string.final_score),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            // Score con efecto
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.points_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
