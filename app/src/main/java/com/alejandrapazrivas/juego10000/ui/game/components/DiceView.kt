package com.alejandrapazrivas.juego10000.ui.game.components

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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Dice
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.theme.Secondary

private val AccentColor = Color(0xFFFFD700) // Dorado

/**
 * Componente visual para un dado individual con animaciones y estados visuales.
 * Soporta estados de selección, bloqueo y animación de lanzamiento.
 *
 * @param dice Modelo del dado a mostrar
 * @param onClick Callback cuando se hace click en el dado
 * @param isRolling Si el dado está en animación de lanzamiento
 * @param modifier Modificador opcional
 */
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

    val diceResource = getDiceResource(dice.value)

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

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

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

    val stateColor = when {
        dice.isLocked -> Primary
        dice.isSelected -> Secondary
        else -> Color.Transparent
    }

    Box(
        modifier = modifier.size(dimensions.diceSize + 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Efecto de glow para dados seleccionados/bloqueados
        DiceGlowEffect(
            visible = (dice.isSelected || dice.isLocked) && !isRolling,
            stateColor = stateColor,
            glowAlpha = glowAlpha
        )

        // Dado principal
        DiceBody(
            dice = dice,
            diceResource = diceResource,
            rotation = if (isRolling) rotation else 0f,
            scale = scale,
            stateColor = stateColor,
            isRolling = isRolling,
            interactionSource = interactionSource,
            onClick = onClick
        )

        // Indicador de bloqueado
        if (dice.isLocked && !isRolling) {
            LockedIndicator(modifier = Modifier.align(Alignment.TopEnd))
        }
    }
}

@Composable
private fun DiceGlowEffect(
    visible: Boolean,
    stateColor: Color,
    glowAlpha: Float,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier
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

@Composable
private fun DiceBody(
    dice: Dice,
    diceResource: Int,
    rotation: Float,
    scale: Float,
    stateColor: Color,
    isRolling: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier
            .size(dimensions.diceSize)
            .rotate(rotation)
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
                brush = getDiceBackgroundBrush(dice)
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
        Icon(
            painter = painterResource(id = diceResource),
            contentDescription = stringResource(R.string.dice_content_description, dice.value),
            modifier = Modifier.size(dimensions.diceIconSize),
            tint = Color.Unspecified
        )
    }
}

@Composable
private fun LockedIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
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

@Composable
private fun getDiceBackgroundBrush(dice: Dice): Brush {
    return when {
        dice.isLocked -> Brush.linearGradient(
            colors = listOf(
                Primary.copy(alpha = 0.15f),
                Primary.copy(alpha = 0.25f)
            )
        )
        dice.isSelected -> Brush.linearGradient(
            colors = listOf(
                Secondary.copy(alpha = 0.15f),
                AccentColor.copy(alpha = 0.2f)
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surface
            )
        )
    }
}

private fun getDiceResource(value: Int): Int {
    return when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1
    }
}
