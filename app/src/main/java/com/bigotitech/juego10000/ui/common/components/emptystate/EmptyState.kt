package com.bigotitech.juego10000.ui.common.components.emptystate

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.bigotitech.juego10000.ui.common.theme.ButtonShape
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions

/**
 * Componente reutilizable para mostrar estados vacíos.
 * Puede mostrar un icono, título, descripción y opcionalmente un botón de acción.
 *
 * @param icon Painter del icono a mostrar
 * @param title Título principal
 * @param description Descripción opcional
 * @param actionButtonText Texto del botón de acción (si es null, no se muestra)
 * @param actionButtonIcon Icono del botón de acción
 * @param onActionClick Callback del botón de acción
 * @param useGradientBackground Si usar fondo con gradiente en el icono
 * @param animated Si aplicar animación de entrada
 */
@Composable
fun EmptyState(
    icon: Painter,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    actionButtonText: String? = null,
    actionButtonIcon: ImageVector = Icons.Default.Add,
    onActionClick: (() -> Unit)? = null,
    useGradientBackground: Boolean = true,
    animated: Boolean = true,
    iconTint: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
) {
    val dimensions = LocalDimensions.current

    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale_animation"
    )

    val scaleModifier = if (animated) {
        Modifier.graphicsLayer {
            scaleX = animatedScale
            scaleY = animatedScale
        }
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensions.spaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconContainer(
            icon = icon,
            useGradientBackground = useGradientBackground,
            iconTint = iconTint,
            modifier = scaleModifier
        )

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        description?.let {
            Spacer(modifier = Modifier.height(dimensions.spaceSmall))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = dimensions.spaceExtraLarge)
            )
        }

        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))
            ActionButton(
                text = actionButtonText,
                icon = actionButtonIcon,
                onClick = onActionClick
            )
        }
    }
}

@Composable
private fun IconContainer(
    icon: Painter,
    useGradientBackground: Boolean,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier
            .size(dimensions.emptyStateIconSize)
            .clip(CircleShape)
            .then(
                if (useGradientBackground) {
                    Modifier.background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        )
                    )
                } else {
                    Modifier
                }
            )
            .padding(dimensions.spaceMedium),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(dimensions.buttonHeight + dimensions.spaceMedium),
            tint = iconTint
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Button(
        onClick = onClick,
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = dimensions.spaceExtraSmall
        ),
        modifier = Modifier
            .height(dimensions.avatarSizeMedium)
            .padding(horizontal = dimensions.spaceExtraLarge)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = dimensions.spaceSmall + dimensions.spaceExtraSmall)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/**
 * Versión simplificada de EmptyState solo con mensaje e icono.
 * Útil para tabs de estadísticas o listas vacías sin acción.
 */
@Composable
fun EmptyStateMessage(
    message: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
) {
    val dimensions = LocalDimensions.current

    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale_animation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensions.spaceMedium),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(dimensions.buttonHeight + dimensions.spaceExtraLarge)
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                    },
                tint = iconTint
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = dimensions.spaceExtraLarge)
            )
        }
    }
}
