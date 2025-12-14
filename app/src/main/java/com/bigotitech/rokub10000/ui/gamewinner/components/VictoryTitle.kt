package com.bigotitech.rokub10000.ui.gamewinner.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.common.theme.GoldColor

/**
 * Título de victoria con efecto de shimmer y estrellas decorativas.
 */
@Composable
fun VictoryTitle(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "title")

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
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DecorativeStars()

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
private fun DecorativeStars(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedStarIcon(size = 20.dp, delay = 0)
        Spacer(modifier = Modifier.width(8.dp))
        AnimatedStarIcon(size = 28.dp, delay = 100)
        Spacer(modifier = Modifier.width(8.dp))
        AnimatedStarIcon(size = 20.dp, delay = 200)
    }
}

@Composable
private fun AnimatedStarIcon(
    size: Dp,
    delay: Int,
    modifier: Modifier = Modifier
) {
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
        modifier = modifier
            .size(size)
            .alpha(alpha)
            .rotate(rotation),
        tint = GoldColor
    )
}
