package com.bigotitech.rokub10000.ui.rules.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Secondary

/**
 * Sección moderna que agrupa elementos de puntuación bajo un título común.
 * Sin divisores, diseño limpio.
 */
@Composable
fun ScoringSection(title: String, content: @Composable () -> Unit) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier.padding(vertical = dimensions.spaceSmall)
    ) {
        // Título con indicador visual
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pequeño indicador circular
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Secondary)
            )

            Spacer(modifier = Modifier.width(dimensions.spaceSmall))

            Text(
                text = title.removeSuffix(":"),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        // Contenido con padding izquierdo para alineación visual
        Column(
            modifier = Modifier.padding(start = dimensions.spaceMedium)
        ) {
            content()
        }
    }
}
