package com.alejandrapazrivas.juego10000.ui.game.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Dice

// Constantes
private val DICE_SIZE = 64.dp
private val DICE_ICON_SIZE = 56.dp
private val DICE_CORNER_RADIUS = 8.dp

/**
 * Componente que muestra un indicador visual con un icono y mensajes
 */
@Composable
private fun GameIndicator(
    visible: Boolean,
    icon: ImageVector,
    title: String,
    message: String? = null,
    detailMessage: String? = null,
    isError: Boolean = false
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            color = if (isError) 
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
            else 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isError) 
                        MaterialTheme.colorScheme.onErrorContainer
                    else 
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
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
                    Spacer(modifier = Modifier.height(8.dp))
                    
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

@Composable
fun DiceSection(
    dice: List<Dice>,
    onDiceClick: (Dice) -> Unit,
    isRolling: Boolean,
    modifier: Modifier = Modifier,
    showTurnLostIndicator: Boolean = false,
    showPointsSavedIndicator: Boolean = false,
    showScoreExceededIndicator: Boolean = false
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Sección principal de dados
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Mostrar dados en dos filas de 3
            if (dice.isNotEmpty()) {
                val firstRow = dice.take(3)
                val secondRow = dice.drop(3)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    firstRow.forEach { die ->
                        DiceView(
                            dice = die,
                            onClick = { onDiceClick(die) },
                            isRolling = isRolling
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    secondRow.forEach { die ->
                        DiceView(
                            dice = die,
                            onClick = { onDiceClick(die) },
                            isRolling = isRolling
                        )
                    }
                }
            }
        }
        
        // Indicador de turno perdido
        GameIndicator(
            visible = showTurnLostIndicator,
            icon = Icons.Default.Close,
            title = "¡Turno perdido!",
            message = "No hay dados con puntuación",
            detailMessage = "Observa los dados para ver tu tirada",
            isError = true
        )
        
        // Indicador de puntos guardados
        GameIndicator(
            visible = showPointsSavedIndicator,
            icon = Icons.Default.Check,
            title = "¡Puntos guardados!",
            isError = false
        )
        
        // Indicador de puntuación excedida
        GameIndicator(
            visible = showScoreExceededIndicator,
            icon = Icons.Default.Warning,
            title = "¡Puntuación excedida!",
            message = "Has superado los 10,000 puntos",
            detailMessage = "Observa los dados para ver tu tirada",
            isError = true
        )
    }
}

@Composable
fun DiceView(
    dice: Dice,
    onClick: () -> Unit,
    isRolling: Boolean,
    modifier: Modifier = Modifier
) {
    // Obtener el recurso de drawable correspondiente al valor del dado
    val diceResource = when (dice.value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1
    }
    
    // Animación de rotación para el lanzamiento
    val infiniteTransition = rememberInfiniteTransition(label = "diceRoll")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Escala para el efecto de selección
    val scale by animateFloatAsState(
        targetValue = if (dice.isSelected || dice.isLocked) 1.1f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )
    
    // Color de fondo según el estado del dado
    val backgroundColor = when {
        dice.isLocked -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        dice.isSelected -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        else -> Color.Transparent
    }
    
    // Contenedor del dado con efectos visuales
    Box(
        modifier = modifier
            .size(DICE_SIZE)
            .rotate(if (isRolling) rotation else 0f)
            .scale(scale)
            .clip(RoundedCornerShape(DICE_CORNER_RADIUS))
            .background(backgroundColor)
            .clickable(enabled = !isRolling && !dice.isLocked) { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Icono del dado
        Icon(
            painter = painterResource(id = diceResource),
            contentDescription = "Dado con valor ${dice.value}",
            modifier = Modifier.size(DICE_ICON_SIZE),
            tint = Color.Unspecified
        )
    }
}
