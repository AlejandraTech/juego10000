package com.alejandrapazrivas.juego10000.ui.rules.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Sección que agrupa elementos de puntuación bajo un título común.
 * 
 * @param title Título de la sección
 * @param content Contenido composable a mostrar dentro de la sección
 */
@Composable
fun ScoringSection(title: String, content: @Composable () -> Unit) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier.padding(vertical = dimensions.spaceSmall)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        content()

        Spacer(modifier = Modifier.height(dimensions.spaceExtraSmall))

        if (title != "Multiplicadores:") {
            Divider(
                modifier = Modifier.padding(vertical = dimensions.spaceSmall),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
    }
}
