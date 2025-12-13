package com.alejandrapazrivas.juego10000.ui.player.model

import com.alejandrapazrivas.juego10000.domain.model.Player

data class PlayerUiState(
    val dialogState: PlayerDialogState = PlayerDialogState.None,
    val playerName: String = ""
)

sealed class PlayerDialogState {
    data object None : PlayerDialogState()
    data object Add : PlayerDialogState()
    data class Edit(val player: Player) : PlayerDialogState()
    data class Delete(val player: Player) : PlayerDialogState()
}
