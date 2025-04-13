package com.alejandrapazrivas.juego10000.ui.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Componente que envuelve su contenido con una animación de entrada.
 * 
 * @param modifier Modificador para aplicar al componente
 * @param content Contenido a mostrar con animación
 */
@Composable
fun AnimatedContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val contentVisible = remember { MutableTransitionState(false) }

    LaunchedEffect(Unit) {
        contentVisible.targetState = true
    }

    AnimatedVisibility(
        modifier = modifier,
        visibleState = contentVisible,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialOffsetY = { it / 2 }
                )
    ) {
        content()
    }
}
