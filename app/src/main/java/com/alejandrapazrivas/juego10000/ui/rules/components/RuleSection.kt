package com.alejandrapazrivas.juego10000.ui.rules.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary
import com.alejandrapazrivas.juego10000.ui.rules.components.base.RuleCard

/**
 * Sección de reglas que muestra un icono, título y contenido de texto.
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

/**
 * Sección de reglas con lista numerada (números coloreados y espaciado).
 */
@Composable
fun RuleSectionNumbered(icon: Int, title: String, items: List<String>) {
    val dimensions = LocalDimensions.current

    RuleCard(
        icon = icon,
        title = title
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    // Número con color destacado
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.width(24.dp)
                    )

                    // Texto del ítem
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Espaciado entre ítems (excepto el último)
                if (index < items.lastIndex) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

/**
 * Sección de reglas con lista de viñetas (bullets coloreados y espaciado).
 */
@Composable
fun RuleSectionBulleted(icon: Int, title: String, items: List<String>) {
    val dimensions = LocalDimensions.current

    RuleCard(
        icon = icon,
        title = title
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    // Bullet con color destacado
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Secondary,
                        modifier = Modifier.width(20.dp)
                    )

                    // Texto del ítem
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Espaciado entre ítems (excepto el último)
                if (index < items.lastIndex) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
