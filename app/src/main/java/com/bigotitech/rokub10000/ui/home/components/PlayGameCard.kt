package com.bigotitech.rokub10000.ui.home.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayGameCard(
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val infiniteTransition = rememberInfiniteTransition(label = "play_button_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        onClick = onPlayClick,
        modifier = modifier
            .fillMaxWidth()
            .scale(pulseScale),
        shape = RoundedCornerShape(dimensions.spaceMedium),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.cardElevation)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(dimensions.spaceMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono de dados
                Box(
                    modifier = Modifier
                        .size(dimensions.iconSizeExtraLarge)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dice),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(dimensions.iconSizeMedium)
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.spaceSmall))

                // Texto - usa weight para ocupar espacio disponible
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.new_game),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(dimensions.spaceExtraSmall))
                    Text(
                        text = stringResource(R.string.reach_10000_points),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.spaceSmall))

                // Botón de play
                Box(
                    modifier = Modifier
                        .size(dimensions.iconSizeExtraLarge)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(R.string.play),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensions.iconSizeMedium)
                    )
                }
            }
        }
    }
}
