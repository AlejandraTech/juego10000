package com.bigotitech.rokub10000.ui.userselection.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary

/**
 * Estado cuando no hay jugadores registrados
 */
@Composable
fun EmptyPlayersState() {
    val dimensions = LocalDimensions.current
    val infiniteTransition = rememberInfiniteTransition(label = "empty_animation")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.spaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

        // Icono animado
        Box(
            modifier = Modifier
                .size(dimensions.avatarSizeLarge + dimensions.spaceLarge)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.15f),
                            Primary.copy(alpha = 0.05f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(dimensions.avatarSizeMedium),
                tint = Primary.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        Text(
            text = stringResource(R.string.no_players_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Text(
            text = stringResource(R.string.no_players_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
