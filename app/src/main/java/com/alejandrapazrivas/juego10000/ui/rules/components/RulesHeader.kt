package com.alejandrapazrivas.juego10000.ui.rules.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Encabezado animado para la pantalla de reglas.
 * Muestra un título, subtítulo e icono con una animación de entrada.
 */
@Composable
fun RulesHeader() {
    val dimensions = LocalDimensions.current
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 700),
        label = "header_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = scale
            }
            .shadow(
                elevation = dimensions.spaceSmall,
                shape = RoundedCornerShape(dimensions.spaceMedium + dimensions.spaceExtraSmall),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(dimensions.spaceMedium + dimensions.spaceExtraSmall),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceLarge),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.buttonHeight + dimensions.spaceMedium)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .padding(dimensions.spaceSmall + dimensions.spaceExtraSmall),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_rules),
                        contentDescription = "Reglas",
                        modifier = Modifier.size(dimensions.iconSizeLarge + dimensions.spaceExtraSmall),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                Text(
                    text = "Reglas del Juego 10000",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                Text(
                    text = "Aprende a jugar y dominar el juego",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
