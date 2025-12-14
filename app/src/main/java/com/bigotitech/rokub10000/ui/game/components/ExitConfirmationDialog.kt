package com.bigotitech.rokub10000.ui.game.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.common.components.dialog.BaseDialog
import com.bigotitech.rokub10000.ui.common.components.dialog.DialogButtons
import com.bigotitech.rokub10000.ui.common.components.dialog.DialogHeader
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions

/**
 * Diálogo de confirmación para salir del juego.
 * Pregunta al usuario si desea abandonar la partida actual.
 *
 * @param onDismiss Callback cuando se cancela el diálogo
 * @param onConfirm Callback cuando se confirma salir
 */
@Composable
fun ExitConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val dimensions = LocalDimensions.current

    BaseDialog(onDismiss = onDismiss) {
        DialogHeader(
            title = stringResource(R.string.confirm),
            description = stringResource(R.string.confirm_exit_game),
            icon = Icons.AutoMirrored.Rounded.ExitToApp,
            iconTint = MaterialTheme.colorScheme.error,
            iconBackgroundColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        )

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        DialogButtons(
            onCancel = onDismiss,
            onConfirm = onConfirm,
            confirmText = stringResource(R.string.yes),
            confirmColor = MaterialTheme.colorScheme.error,
            confirmTextColor = MaterialTheme.colorScheme.onError
        )
    }
}
