package com.bigotitech.rokub10000.presentation.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigotitech.rokub10000.domain.repository.PlayerRepository
import com.bigotitech.rokub10000.domain.usecase.player.ManagePlayersUseCase
import com.bigotitech.rokub10000.presentation.feature.player.state.PlayerWithBestTurn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de gestión de jugadores.
 * Gestiona la lógica de crear, editar y eliminar jugadores.
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val managePlayersUseCase: ManagePlayersUseCase
) : ViewModel() {

    companion object {
        private const val BOT_PLAYER_NAME = "Bot"
    }

    val playersWithBestTurn: Flow<List<PlayerWithBestTurn>> = playerRepository.getAllActivePlayers().map { players ->
        players.filter { it.name != BOT_PLAYER_NAME }.map { player ->
            val bestTurnScore = playerRepository.getPlayerBestTurnScore(player.id)
            PlayerWithBestTurn(player = player, bestTurnScore = bestTurnScore)
        }
    }

    fun createPlayer(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            managePlayersUseCase.createPlayer(name)
        }
    }

    fun updatePlayer(playerId: Long, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            val currentPlayer = playerRepository.getPlayerById(playerId)
            currentPlayer?.let { player ->
                val updatedPlayer = player.copy(name = newName)
                managePlayersUseCase.updatePlayer(updatedPlayer)
            }
        }
    }

    fun deactivatePlayer(playerId: Long) {
        viewModelScope.launch {
            managePlayersUseCase.deactivatePlayer(playerId)
        }
    }
}
