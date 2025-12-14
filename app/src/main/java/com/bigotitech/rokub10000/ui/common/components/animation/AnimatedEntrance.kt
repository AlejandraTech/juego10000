package com.bigotitech.rokub10000.ui.common.components.animation

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
 * Componente que aplica una animación de entrada con efecto de rebote.
 * Reutilizable en cualquier parte de la aplicación.
 *
 * @param modifier Modificador opcional
 * @param initiallyVisible Si el contenido debe estar inicialmente visible
 * @param content Contenido a animar
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
