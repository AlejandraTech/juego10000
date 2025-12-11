package com.alejandrapazrivas.juego10000.ui.rules.components.base

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Componente base para las tarjetas de reglas.
 * 
 * @param icon Recurso de icono a mostrar
 * @param title Título de la tarjeta
 * @param content Contenido composable a mostrar dentro de la tarjeta
 */
@Composable
fun RuleCard(
    icon: Int,
    title: String,
    content: @Composable () -> Unit
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimensions.spaceExtraSmall,
                shape = CardShape,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationNone)
    ) {
        Column(
            modifier = Modifier.padding(dimensions.spaceMedium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.avatarSizeSmall)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                        .padding(dimensions.spaceSmall),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.spaceSmall + dimensions.spaceExtraSmall))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceSmall + dimensions.spaceExtraSmall))

            Divider(
                modifier = Modifier.padding(vertical = dimensions.spaceExtraSmall),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            content()
        }
    }
}
