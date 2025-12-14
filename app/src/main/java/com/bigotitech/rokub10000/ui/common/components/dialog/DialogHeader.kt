package com.bigotitech.rokub10000.ui.common.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary

/**
 * Encabezado reutilizable para diálogos con icono, título y descripción opcional.
 *
 * @param title Título del diálogo
 * @param description Descripción opcional del diálogo
 * @param icon Icono vectorial opcional (ImageVector)
 * @param iconPainter Icono painter opcional (para drawables)
 * @param iconTint Color del icono
 * @param iconBackgroundColor Color de fondo del icono
 */
@Composable
fun DialogHeader(
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    iconTint: Color = Primary,
    iconBackgroundColor: Color = Primary.copy(alpha = 0.1f)
) {
    val dimensions = LocalDimensions.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icono con fondo circular
        if (icon != null || iconPainter != null) {
            Box(
                modifier = Modifier
                    .size(dimensions.avatarSizeMedium)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(dimensions.iconSizeLarge)
                    )
                } else if (iconPainter != null) {
                    Icon(
                        painter = iconPainter,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(dimensions.iconSizeLarge)
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))
        }

        // Título
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        // Descripción opcional
        if (description != null) {
            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
