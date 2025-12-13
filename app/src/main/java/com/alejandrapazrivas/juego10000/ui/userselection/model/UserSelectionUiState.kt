package com.alejandrapazrivas.juego10000.ui.userselection.model

import com.alejandrapazrivas.juego10000.domain.model.Player

/**
 * Estado de UI para la pantalla de selección de usuario
 */
data class UserSelectionUiState(
    val isLoading: Boolean = true,
    val players: List<Player> = emptyList(),
    val selectedUserId: Long = 0L,
    val navigateToHome: Boolean = false,
    val showCreatePlayerHint: Boolean = false
)
