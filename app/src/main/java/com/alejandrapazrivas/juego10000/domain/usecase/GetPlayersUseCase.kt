package com.alejandrapazrivas.juego10000.domain.usecase

import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para obtener información de jugadores.
 */
class GetPlayersUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    companion object {
        private const val BOT_PLAYER_NAME = "Bot"
    }

    /**
     * Obtiene todos los jugadores activos, excluyendo los jugadores bot
     * @return Flow con lista de jugadores activos (sin bots)
     */
    operator fun invoke(): Flow<List<Player>> {
        return playerRepository.getAllActivePlayers().map { players ->
            players.filter { it.name != BOT_PLAYER_NAME }
        }
    }
}
