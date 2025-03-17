package com.alejandrapazrivas.juego10000.domain.usecase

import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener información de jugadores.
 */
class GetPlayersUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    /**
     * Obtiene todos los jugadores activos
     * @return Flow con lista de jugadores activos
     */
    operator fun invoke(): Flow<List<Player>> {
        return playerRepository.getAllActivePlayers()
    }
}
