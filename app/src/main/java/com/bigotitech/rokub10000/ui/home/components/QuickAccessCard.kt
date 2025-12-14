package com.bigotitech.rokub10000.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.ui.common.theme.CardShape
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary

/**
 * Card de acceso rápido con icono, título y flecha de navegación.
 * Incluye animación de escala al presionar.
 */
@Composable
fun QuickAccessCard(
    title: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(100),
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = dimensions.cardElevation,
                shape = CardShape,
                spotColor = Primary.copy(alpha = 0.1f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(dimensions.iconSizeLarge + dimensions.spaceSmall)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(dimensions.iconSizeMedium)
                )
            }

            Spacer(modifier = Modifier.width(dimensions.spaceMedium))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Primary.copy(alpha = 0.5f),
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )
        }
    }
}
