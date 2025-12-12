package com.alejandrapazrivas.juego10000.ui.game.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Dice
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary

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
    val dimensions = LocalDimensions.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
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
    val dimensions = LocalDimensions.current

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

                Spacer(modifier = Modifier.height(dimensions.diceSpacing))

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
    val dimensions = LocalDimensions.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

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
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Animación de brillo para dados seleccionados
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Escala con efecto de rebote
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.9f
            dice.isSelected || dice.isLocked -> 1.08f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Colores según estado
    val stateColor = when {
        dice.isLocked -> Primary
        dice.isSelected -> Secondary
        else -> Color.Transparent
    }

    val accentColor = Color(0xFFFFD700) // Dorado

    // Contenedor del dado con efectos visuales mejorados
    Box(
        modifier = modifier.size(dimensions.diceSize + 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Efecto de glow para dados seleccionados/bloqueados
        if ((dice.isSelected || dice.isLocked) && !isRolling) {
            Box(
                modifier = Modifier
                    .size(dimensions.diceSize + 12.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                stateColor.copy(alpha = glowAlpha * 0.4f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(dimensions.diceCornerRadius + 4.dp)
                    )
            )
        }

        // Dado principal
        Box(
            modifier = Modifier
                .size(dimensions.diceSize)
                .rotate(if (isRolling) rotation else 0f)
                .scale(scale)
                .shadow(
                    elevation = when {
                        dice.isLocked -> 8.dp
                        dice.isSelected -> 6.dp
                        else -> 3.dp
                    },
                    shape = RoundedCornerShape(dimensions.diceCornerRadius),
                    spotColor = stateColor.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(dimensions.diceCornerRadius))
                .background(
                    brush = when {
                        dice.isLocked -> Brush.linearGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.15f),
                                Primary.copy(alpha = 0.25f)
                            )
                        )
                        dice.isSelected -> Brush.linearGradient(
                            colors = listOf(
                                Secondary.copy(alpha = 0.15f),
                                accentColor.copy(alpha = 0.2f)
                            )
                        )
                        else -> Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                )
                .then(
                    if (dice.isSelected || dice.isLocked) {
                        Modifier.border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    stateColor,
                                    stateColor.copy(alpha = 0.5f)
                                )
                            ),
                            shape = RoundedCornerShape(dimensions.diceCornerRadius)
                        )
                    } else Modifier
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !isRolling && !dice.isLocked
                ) { onClick() }
                .padding(dimensions.spaceExtraSmall),
            contentAlignment = Alignment.Center
        ) {
            // Icono del dado
            Icon(
                painter = painterResource(id = diceResource),
                contentDescription = "Dado con valor ${dice.value}",
                modifier = Modifier.size(dimensions.diceIconSize),
                tint = Color.Unspecified
            )
        }

        // Indicador de bloqueado
        if (dice.isLocked && !isRolling) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
