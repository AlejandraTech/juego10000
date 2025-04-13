package com.alejandrapazrivas.juego10000.ui.rules.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import com.alejandrapazrivas.juego10000.ui.rules.components.base.RuleCard

/**
 * Sección de reglas que muestra un icono, título y contenido de texto.
 * 
 * @param icon Recurso de icono a mostrar
 * @param title Título de la sección
 * @param content Texto de contenido
 */
@Composable
fun RuleSection(icon: Int, title: String, content: String) {
    RuleCard(
        icon = icon,
        title = title
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start
        )
    }
}
