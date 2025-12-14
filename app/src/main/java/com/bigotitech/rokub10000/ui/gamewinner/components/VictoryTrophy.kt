package com.bigotitech.rokub10000.ui.gamewinner.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.common.theme.GoldColor
import com.bigotitech.rokub10000.ui.common.theme.GoldDark
import com.bigotitech.rokub10000.ui.common.theme.GoldLight

/**
 * Sección del trofeo animado con efectos de glow y sparkles.
 */
@Composable
fun AnimatedTrophySection(modifier: Modifier = Modifier) {
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
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        TrophyGlow(scale = scale, glowAlpha = glowAlpha)
        TrophyCircle(scale = scale, rotation = rotation)
        SparklesAroundTrophy()
    }
}

@Composable
private fun TrophyGlow(
    scale: Float,
    glowAlpha: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
}

@Composable
private fun TrophyCircle(
    scale: Float,
    rotation: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
}

@Composable
private fun SparklesAroundTrophy(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkles")

    val positions = listOf(
        Pair((-70).dp, (-50).dp),
        Pair(70.dp, (-40).dp),
        Pair((-60).dp, 50.dp),
        Pair(65.dp, 55.dp),
        Pair(0.dp, (-75).dp)
    )

    Box(modifier = modifier) {
        positions.forEachIndexed { index, (x, y) ->
            AnimatedSparkle(
                infiniteTransition = infiniteTransition,
                index = index,
                offsetX = x,
                offsetY = y
            )
        }
    }
}

@Composable
private fun AnimatedSparkle(
    infiniteTransition: androidx.compose.animation.core.InfiniteTransition,
    index: Int,
    offsetX: Dp,
    offsetY: Dp,
    modifier: Modifier = Modifier
) {
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
        modifier = modifier
            .offset(x = offsetX, y = offsetY)
            .size(16.dp)
            .scale(scale)
            .alpha(alpha),
        tint = GoldColor
    )
}
