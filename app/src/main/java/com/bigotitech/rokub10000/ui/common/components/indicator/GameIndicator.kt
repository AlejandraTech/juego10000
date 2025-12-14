package com.bigotitech.rokub10000.ui.common.components.indicator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions

/**
 * Indicador visual con icono y mensajes para mostrar estados del juego.
 * Soporta estilos de error y éxito con animación de fade.
 *
 * @param visible Si el indicador está visible
 * @param icon Icono a mostrar
 * @param title Título principal
 * @param message Mensaje secundario (opcional)
 * @param detailMessage Mensaje de detalle adicional (opcional)
 * @param isError Si debe usar estilo de error
 * @param modifier Modificador opcional
 */
@Composable
fun GameIndicator(
    visible: Boolean,
    icon: ImageVector,
    title: String,
    message: String? = null,
    detailMessage: String? = null,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(dimensions.spaceMedium),
            color = if (isError)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
            else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(dimensions.spaceMedium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isError)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(dimensions.iconSizeExtraLarge)
                )

                Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isError)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer
                )

                if (message != null) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = if (isError)
                            MaterialTheme.colorScheme.onErrorContainer
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                if (detailMessage != null) {
                    Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                    Text(
                        text = detailMessage,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = if (isError)
                            MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
