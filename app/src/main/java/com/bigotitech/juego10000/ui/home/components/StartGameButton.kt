package com.bigotitech.juego10000.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.ui.common.theme.ButtonShape
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions

/**
 * Botón principal para iniciar una nueva partida.
 * Incluye animaciones y efectos visuales para mejorar la experiencia de usuario.
 */
@Composable
fun StartGameButton(
    enabled: Boolean,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(100),
        label = "scale_animation"
    )

    val shadowElevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 6f,
        animationSpec = tween(100),
        label = "elevation_animation"
    )

    val gradientColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        MaterialTheme.colorScheme.primaryContainer
    )

    val buttonHeight = dimensions.buttonHeight + dimensions.spaceSmall

    Button(
        onClick = onStartGame,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .scale(scale)
            .shadow(
                elevation = shadowElevation.dp,
                shape = ButtonShape,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            .clip(ButtonShape),
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight)
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(colors = gradientColors)
                    } else {
                        Brush.horizontalGradient(
                            colors = gradientColors.map { it.copy(alpha = 0.5f) }
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = dimensions.spaceMedium)
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.iconSizeLarge + dimensions.spaceExtraSmall)
                        .clip(CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(dimensions.iconSizeMedium)
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.spaceMedium))

                Text(
                    text = stringResource(id = R.string.start_game),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
