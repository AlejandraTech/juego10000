package com.bigotitech.rokub10000.presentation.common.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.presentation.common.theme.LocalDimensions

/**
 * Diálogo para controlar la música de fondo.
 * Permite activar/desactivar la música y ajustar el volumen.
 */
@Composable
fun AudioControlDialog(
    musicEnabled: Boolean,
    musicVolume: Float,
    onMusicEnabledChange: (Boolean) -> Unit,
    onMusicVolumeChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val dimensions = LocalDimensions.current
    var localMusicEnabled by remember { mutableStateOf(musicEnabled) }
    var localMusicVolume by remember { mutableFloatStateOf(musicVolume) }

    BaseDialog(onDismiss = onDismiss) {
        DialogHeader(
            title = stringResource(R.string.background_music),
            description = stringResource(R.string.audio_control_description),
            iconPainter = painterResource(id = R.drawable.ic_music)
        )

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Toggle para activar/desactivar música
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.background_music),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.enable_background_music),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Switch(
                    checked = localMusicEnabled,
                    onCheckedChange = {
                        localMusicEnabled = it
                        onMusicEnabledChange(it)
                    }
                )
            }

            // Control de volumen (solo visible si la música está activada)
            if (localMusicEnabled) {
                Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.music_volume),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                    Slider(
                        value = localMusicVolume,
                        onValueChange = {
                            localMusicVolume = it
                            onMusicVolumeChange(it)
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        DialogButtons(
            onCancel = onDismiss,
            onConfirm = onDismiss,
            confirmText = stringResource(R.string.confirm)
        )
    }
}
