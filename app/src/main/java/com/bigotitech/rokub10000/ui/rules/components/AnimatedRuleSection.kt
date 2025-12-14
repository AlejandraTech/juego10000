package com.bigotitech.rokub10000.ui.rules.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AnimatedRuleSection(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(400)) +
                expandVertically(animationSpec = tween(400))
    ) {
        content()
    }
}
