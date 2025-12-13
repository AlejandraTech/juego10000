package com.alejandrapazrivas.juego10000.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary

private val AccentColor = Color(0xFFFF6B35)
private val SecondaryAccent = Color(0xFFFFD700)

/**
 * Botón principal de jugar con anillos animados.
 * Componente complejo con múltiples animaciones decorativas.
 */
@Composable
fun PlayButton(
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(100),
        label = "play_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "play_animations")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    val innerRingRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_ring_rotation"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val buttonSize = dimensions.avatarSizeLarge * 2

    Box(
        modifier = modifier
            .size(buttonSize + 40.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        OuterRing(
            size = buttonSize + 36.dp,
            rotation = ringRotation
        )

        InnerRing(
            size = buttonSize + 20.dp,
            rotation = innerRingRotation
        )

        GlowEffect(
            size = buttonSize + 8.dp,
            scale = pulseScale,
            alpha = glowAlpha
        )

        MainButton(
            size = buttonSize,
            scale = pulseScale,
            interactionSource = interactionSource,
            onClick = onPlayClick,
            iconSize = dimensions.iconSizeExtraLarge + 8.dp
        )
    }
}

@Composable
private fun OuterRing(
    size: androidx.compose.ui.unit.Dp,
    rotation: Float
) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation }
    ) {
        val strokeWidth = 4.dp.toPx()
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Primary,
                    AccentColor,
                    SecondaryAccent,
                    Primary.copy(alpha = 0.3f),
                    Primary
                )
            ),
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
private fun InnerRing(
    size: androidx.compose.ui.unit.Dp,
    rotation: Float
) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation }
    ) {
        val strokeWidth = 2.dp.toPx()
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    SecondaryAccent.copy(alpha = 0.8f),
                    Primary.copy(alpha = 0.5f),
                    AccentColor.copy(alpha = 0.6f),
                    SecondaryAccent.copy(alpha = 0.3f)
                )
            ),
            startAngle = 45f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
private fun GlowEffect(
    size: androidx.compose.ui.unit.Dp,
    scale: Float,
    alpha: Float
) {
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Primary.copy(alpha = alpha),
                        Primary.copy(alpha = alpha * 0.5f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
}

@Composable
private fun MainButton(
    size: androidx.compose.ui.unit.Dp,
    scale: Float,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    iconSize: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                spotColor = Primary.copy(alpha = 0.5f)
            )
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Primary,
                        Primary.copy(alpha = 0.9f),
                        AccentColor.copy(alpha = 0.8f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        LightOverlay()

        ButtonContent(iconSize = iconSize)
    }
}

@Composable
private fun LightOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(0.3f, 0.3f),
                    radius = 300f
                )
            )
    )
}

@Composable
private fun ButtonContent(iconSize: androidx.compose.ui.unit.Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_dice),
            contentDescription = stringResource(R.string.play),
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.play).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = 2.sp
        )
    }
}
