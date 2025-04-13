package com.alejandrapazrivas.juego10000.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    // Flow de jugadores activos
    val players: Flow<List<Player>> = playerRepository.getAllActivePlayers()

    // Crear un nuevo jugador
    fun createPlayer(name: String) {
        if (name.isBlank()) return

        viewModelScope.launch {
            playerRepository.createPlayer(name)
        }
    }

    // Actualizar un jugador existente
    fun updatePlayer(playerId: Long, newName: String) {
        if (newName.isBlank()) return

        viewModelScope.launch {
            val currentPlayer = playerRepository.getPlayerById(playerId)

            currentPlayer?.let { player ->
                val updatedPlayer = player.copy(name = newName)
                playerRepository.updatePlayer(updatedPlayer)
            }
        }
    }

    // Desactivar un jugador
    fun deactivatePlayer(playerId: Long) {
        viewModelScope.launch {
            playerRepository.deactivatePlayer(playerId)
        }
    }
}
