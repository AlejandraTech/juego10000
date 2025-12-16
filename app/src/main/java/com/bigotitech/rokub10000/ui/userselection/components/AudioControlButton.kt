package com.bigotitech.rokub10000.ui.userselection.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.common.components.dialog.AudioControlDialog
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary

/**
 * Botón circular para controlar el audio en la pantalla de selección de usuario.
 * Al hacer clic muestra un diálogo con las opciones de música.
 */
@Composable
fun AudioControlButton(
    musicEnabled: Boolean,
    musicVolume: Float,
    onMusicEnabledChange: (Boolean) -> Unit,
    onMusicVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    var showDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "audio_button_scale"
    )

    Box(
        modifier = modifier
            .size(dimensions.buttonHeight)
            .scale(scale)
            .clip(CircleShape)
            .background(Primary.copy(alpha = if (musicEnabled) 0.1f else 0.05f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { showDialog = true }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = stringResource(R.string.background_music),
            tint = Primary.copy(alpha = if (musicEnabled) 1f else 0.5f),
            modifier = Modifier.size(dimensions.iconSizeMedium)
        )
    }

    if (showDialog) {
        AudioControlDialog(
            musicEnabled = musicEnabled,
            musicVolume = musicVolume,
            onMusicEnabledChange = onMusicEnabledChange,
            onMusicVolumeChange = onMusicVolumeChange,
            onDismiss = { showDialog = false }
        )
    }
}
