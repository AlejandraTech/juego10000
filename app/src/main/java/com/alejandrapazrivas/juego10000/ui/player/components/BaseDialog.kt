package com.alejandrapazrivas.juego10000.ui.player.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.window.Dialog
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import androidx.compose.ui.window.DialogProperties

/**
 * Componente base para diálogos con estilo común.
 * 
 * @param onDismiss Callback para cerrar el diálogo
 * @param content Contenido del diálogo
 */
@Composable
fun BaseDialog(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val dimensions = LocalDimensions.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
                .shadow(
                    elevation = dimensions.spaceSmall,
                    shape = RoundedCornerShape(dimensions.spaceMedium)
                ),
            shape = RoundedCornerShape(dimensions.spaceMedium),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.spaceLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}
