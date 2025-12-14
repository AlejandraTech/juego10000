package com.bigotitech.rokub10000.ui.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.ui.common.components.dialog.BaseDialog
import com.bigotitech.rokub10000.ui.common.components.dialog.DialogButtons
import com.bigotitech.rokub10000.ui.common.components.dialog.DialogHeader
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions

/**
 * Diálogo para seleccionar oponentes para una partida multijugador.
 * El usuario actual ya está incluido automáticamente en la partida.
 */
@Composable
fun PlayerSelectionDialog(
    availablePlayers: List<Player>,
    selectedPlayers: List<Player>,
    onPlayerSelected: (Player) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val dimensions = LocalDimensions.current

    BaseDialog(onDismiss = onDismiss) {
        DialogHeader(
            title = stringResource(R.string.select_opponents),
            description = stringResource(R.string.select_opponents_description),
            iconPainter = painterResource(id = R.drawable.ic_add_player)
        )

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            availablePlayers.forEach { player ->
                val isSelected = selectedPlayers.contains(player)

                SelectableOption(
                    title = player.name,
                    description = if (isSelected)
                        stringResource(R.string.opponent_selected)
                    else
                        stringResource(R.string.click_to_select),
                    isSelected = isSelected,
                    onClick = { onPlayerSelected(player) },
                    leadingContent = {
                        Box(
                            modifier = Modifier.size(dimensions.avatarSizeSmall),
                            contentAlignment = Alignment.Center
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensions.spaceExtraSmall)
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        DialogButtons(
            onCancel = onDismiss,
            onConfirm = onConfirm,
            confirmText = stringResource(id = R.string.confirm),
            confirmEnabled = selectedPlayers.isNotEmpty() && selectedPlayers.size <= 5
        )
    }
}
