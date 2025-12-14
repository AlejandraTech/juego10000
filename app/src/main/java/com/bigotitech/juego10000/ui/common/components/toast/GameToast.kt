package com.bigotitech.juego10000.ui.common.components.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bigotitech.juego10000.R

/**
 * Tipos de toast para diferentes situaciones
 */
enum class ToastType {
    INFO,
    SUCCESS,
    ERROR
}

/**
 * Componente de Toast personalizado que muestra mensajes no intrusivos
 * 
 * @param message El mensaje a mostrar
 * @param isVisible Si el toast debe ser visible
 * @param type El tipo de toast (INFO, SUCCESS, ERROR)
 * @param onClose Callback para cerrar el toast manualmente
 * @param modifier Modificador opcional para personalizar el toast
 */
@Composable
fun GameToast(
    message: String,
    isVisible: Boolean,
    type: ToastType = ToastType.INFO,
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Constantes de animación
    val animationDurationIn = 200
    val animationDurationOut = 200
    val slideAnimationDuration = 300
    
    // Colores según el tipo de toast
    val backgroundColor = when (type) {
        ToastType.SUCCESS -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
        ToastType.ERROR -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
        ToastType.INFO -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
    }
    
    val textColor = when (type) {
        ToastType.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
        ToastType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
        ToastType.INFO -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(animationDurationIn)) + 
                slideInVertically(
                    animationSpec = tween(slideAnimationDuration),
                    initialOffsetY = { it }
                ),
        exit = fadeOut(animationSpec = tween(animationDurationOut)) + 
               slideOutVertically(
                   animationSpec = tween(slideAnimationDuration),
                   targetOffsetY = { it }
               )
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                
                // Botón de cierre
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close_message),
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
