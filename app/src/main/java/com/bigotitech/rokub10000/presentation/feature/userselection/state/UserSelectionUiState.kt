package com.bigotitech.rokub10000.presentation.feature.userselection.state

import com.bigotitech.rokub10000.domain.model.Player

/**
 * Estado de UI para la pantalla de selección de usuario.
 *
 * @property isLoading Indica si se están cargando los jugadores
 * @property players Lista de jugadores disponibles para seleccionar
 * @property selectedUserId ID del usuario seleccionado
 * @property navigateToHome Indica si debe navegar al home
 * @property showCreatePlayerHint Indica si debe mostrar la sugerencia de crear jugador
 */
data class UserSelectionUiState(
    val isLoading: Boolean = true,
    val players: List<Player> = emptyList(),
    val selectedUserId: Long = 0L,
    val navigateToHome: Boolean = false,
    val showCreatePlayerHint: Boolean = false
)
