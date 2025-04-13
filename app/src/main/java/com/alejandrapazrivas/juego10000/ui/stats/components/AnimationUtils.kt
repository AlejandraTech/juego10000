package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Componente que aplica una animación de entrada con efecto de rebote
 * Utilizado en múltiples componentes de la sección de estadísticas
 */
@Composable
fun AnimatedEntrance(
    modifier: Modifier = Modifier,
    initiallyVisible: Boolean = false,
    content: @Composable () -> Unit
) {
    val visibleState = remember { 
        MutableTransitionState(initiallyVisible).apply { 
            targetState = true 
        } 
    }

    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(300)) +
                slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
    ) {
        content()
    }
}
