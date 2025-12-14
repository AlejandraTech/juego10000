package com.bigotitech.juego10000.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigotitech.juego10000.domain.repository.PlayerRepository
import com.bigotitech.juego10000.ui.player.model.PlayerWithBestTurn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
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
            playerRepository.createPlayer(name)
        }
    }

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

    fun deactivatePlayer(playerId: Long) {
        viewModelScope.launch {
            playerRepository.deactivatePlayer(playerId)
        }
    }
}
